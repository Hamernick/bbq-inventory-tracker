@file:Suppress("FunctionNaming")

package com.bbqreset

import android.os.Bundle
import android.net.Uri
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bbqreset.ui.design.BBQTheme
import com.bbqreset.ui.screens.SettingsScreen
import com.bbqreset.ui.screens.WeekGridScreen
import com.bbqreset.ui.vm.AuthViewModel
import com.bbqreset.ui.vm.WeekGridViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val callbackUri = mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        callbackUri.value = intent?.data
        setContent { BBQApp(callbackUri) }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        callbackUri.value = intent.data
    }
}

private const val ROUTE_SPLASH = "splash"
private const val ROUTE_WEEK = "week"
private const val ROUTE_SETTINGS = "settings"

@Composable
fun BBQApp(callbackUriState: androidx.compose.runtime.MutableState<Uri?>) {
    BBQTheme {
        val nav = rememberNavController()
        val view = LocalView.current
        val surfaceColor = MaterialTheme.colorScheme.surfaceVariant.toArgb()
        SideEffect {
            val window = (view.context as? ComponentActivity)?.window
            if (window != null) {
                WindowCompat.setDecorFitsSystemWindows(window, false)
                val controller = WindowCompat.getInsetsController(window, window.decorView)
                controller.isAppearanceLightStatusBars = true
                controller.isAppearanceLightNavigationBars = true
                controller.hide(
                    android.view.WindowInsets.Type.statusBars() or
                        android.view.WindowInsets.Type.navigationBars()
                )
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                window.statusBarColor = surfaceColor
                window.navigationBarColor = surfaceColor
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            AppNavHost(nav = nav, callbackUri = callbackUriState.value, onConsumed = { callbackUriState.value = null })
        }
    }
}

@Composable
private fun AppNavHost(
    nav: NavHostController,
    modifier: Modifier = Modifier,
    callbackUri: Uri? = null,
    onConsumed: () -> Unit = {}
) {
    NavHost(
        navController = nav,
        startDestination = ROUTE_SPLASH,
        modifier = modifier
    ) {
        composable(ROUTE_SPLASH) {
            SplashRoute(onReady = {
                nav.navigate(ROUTE_WEEK) {
                    popUpTo(ROUTE_SPLASH) { inclusive = true }
                }
            })
        }
        composable(ROUTE_WEEK) {
            val vm: WeekGridViewModel = viewModel()
            val uiState by vm.ui.collectAsState()
            val authVm: AuthViewModel = viewModel()
            val authState by authVm.state.collectAsState()

            LaunchedEffect(callbackUri) {
                if (callbackUri != null) {
                    authVm.handleCallback(
                        clientId = "T2MW3FEKXMF8W",
                        uri = callbackUri,
                        redirectUri = com.bbqreset.data.api.ApiConfig.DEFAULT_REDIRECT_URI
                    )
                    onConsumed()
                }
            }

            LaunchedEffect(authState.success) {
                if (authState.success) vm.connect()
            }

            WeekGridScreen(
                state = uiState,
                authState = authState,
                onStartAuth = {
                    authVm.startAuth(
                        clientId = "T2MW3FEKXMF8W",
                        redirectUri = com.bbqreset.data.api.ApiConfig.DEFAULT_REDIRECT_URI,
                        scopes = listOf("inventory", "inventory.read", "merchant.read", "employees.read")
                    )
                },
                onConnect = { vm.connect() },
                onOpenSettings = { nav.navigate(ROUTE_SETTINGS) },
                onCreateItem = vm::createItem,
                onReseedSample = vm::reseedSample,
                onSelectLocation = vm::selectLocation,
                onWeekPicked = vm::setWeekFromDate,
                onToggleSelect = vm::toggleSelection,
                onAddItems = vm::addItems,
                onDeleteSelected = vm::deleteSelected,
                onUpdateQuantity = vm::updateQuantity,
                onApply = { vm.applyNow() }
            )
        }
        composable(ROUTE_SETTINGS) {
            SettingsScreen()
        }
    }
}

@Composable
private fun SplashRoute(onReady: () -> Unit) {
    LaunchedEffect(Unit) {
        // Boot gate: token/DB checks would go here
        delay(300)
        onReady()
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Loading...",
            style = MaterialTheme.typography.titleMedium
        )
    }
}
