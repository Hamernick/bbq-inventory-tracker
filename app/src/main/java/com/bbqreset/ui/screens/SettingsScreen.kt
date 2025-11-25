@file:Suppress("LongMethod")

package com.bbqreset.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bbqreset.ui.design.BBQTheme
import com.bbqreset.ui.vm.SettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(vm: SettingsViewModel = viewModel()) {
    var token by remember { mutableStateOf(vm.currentToken() ?: "") }
    var merchant by remember { mutableStateOf(vm.currentMerchant() ?: "") }
    var baseUrl by remember { mutableStateOf(vm.baseUrl) }
    val logs by vm.logs.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { vm.loadLogs() }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "Inventory Change Log",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                Divider()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(logs) { log ->
                        ListItem(
                            headlineContent = { Text(log.action) },
                            supportingContent = { Text(log.meta) },
                            overlineContent = { Text(log.ts, style = MaterialTheme.typography.labelSmall) }
                        )
                        Divider()
                    }
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Settings", style = MaterialTheme.typography.titleLarge)
            Divider()

            ListItem(headlineContent = { Text("Environment") }, supportingContent = { Text(baseUrl) })
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(value = baseUrl, onValueChange = { baseUrl = it }, label = { Text("API Base URL") })
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Button(onClick = { vm.saveBaseUrl(baseUrl) }) { Text("Save") }
                    OutlinedButton(onClick = { vm.resetBaseUrl(); baseUrl = vm.baseUrl }) { Text("Reset to Sandbox") }
                }
            }

            Text("Access Token")
            TextField(value = token, onValueChange = { token = it })
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { vm.clearToken(); token = "" }) { Text("Clear Token") }
                Button(onClick = { vm.saveToken(token) }) { Text("Save Token") }
            }

            Text("Merchant ID")
            TextField(value = merchant, onValueChange = { merchant = it })
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { vm.clearMerchant(); merchant = "" }) { Text("Clear Merchant") }
                Button(onClick = { vm.saveMerchant(merchant) }) { Text("Save Merchant") }
            }

            Divider()
            Text("Data")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { vm.seedSample() }) { Text("Seed Sample Data") }
                Button(onClick = { vm.syncCatalog() }) { Text("Sync Catalog (Clover)") }
                OutlinedButton(onClick = { scope.launch { drawerState.open() } }) { Text("View Change History") }
            }

            ListItem(headlineContent = { Text("Default Location") }, supportingContent = { Text(vm.currentLocationName()) })
            Divider()
            Text(
                "Production builds: disable manual token entry; set base URL to prod; use PKCE only.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsPreview() { BBQTheme { SettingsScreen() } }
