package com.bbqreset

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.bbqreset.core.di.DatabaseModule
import com.bbqreset.data.repo.CounterRepository
import com.bbqreset.data.repo.JobRepository
import com.bbqreset.data.repo.LogRepository
import com.bbqreset.domain.ResetPlanner
import com.bbqreset.ui.design.BBQTab
import com.bbqreset.ui.design.BBQTabs
import com.bbqreset.ui.design.BBQTheme
import com.bbqreset.ui.screens.DashboardScreen
import com.bbqreset.ui.screens.TodayScreen
import com.bbqreset.ui.screens.loadTodayUiState
import com.bbqreset.ui.screens.sampleTodayUiState
import java.time.Clock
import java.time.ZoneId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.bbqreset.work.DailyResetScheduler

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BBQApp()
        }
    }
}

@Composable
fun BBQApp() {
    val context = LocalContext.current.applicationContext
    val clock = remember { Clock.systemUTC() }
    val database = remember {
        DatabaseModule.provideAppDatabase(context = context, clock = clock)
    }
    val counterRepository = remember { CounterRepository.fromDatabase(database) }
    val logRepository = remember { LogRepository(database.logDao(), clock) }
    val jobRepository = remember { JobRepository(database.jobDao(), clock) }
    val resetPlanner = remember { ResetPlanner(clock) }
    val scheduler = remember { DailyResetScheduler.create(context, jobRepository, resetPlanner) }
    val appScope = rememberCoroutineScope()
    var refreshKey by rememberSaveable { mutableIntStateOf(0) }
    var selectedSection by rememberSaveable { mutableStateOf(AppSection.DASHBOARD) }
    val todayState by produceState(
        initialValue = sampleTodayUiState,
        database,
        refreshKey
    ) {
        value = withContext(Dispatchers.IO) {
            loadTodayUiState(database)
        }
    }
    val locationName = todayState.locationName

    BBQTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column {
                val tabs = listOf(BBQTab("Dashboard"), BBQTab("Inventory"))
                BBQTabs(
                    tabs = tabs,
                    selectedIndex = selectedSection.ordinal,
                    onSelectedChange = { index -> selectedSection = AppSection.values()[index] }
                )

                when (selectedSection) {
                    AppSection.DASHBOARD -> {
                        val dailyReports = todayState.days.mapIndexed { idx, d ->
                            val base = todayState.summary.soldTotal
                            val value = (base / 7) + (idx * 3 % 9) // simple placeholder trend
                            com.bbqreset.ui.screens.DayReport(d.dayOfWeek, value)
                        }
                        DashboardScreen(
                            locationName = locationName,
                            overview = todayState.summary,
                            dailyReports = dailyReports,
                            momDeltaPercent = 12,
                            onGoToInventory = { selectedSection = AppSection.INVENTORY },
                            onScheduleReset = {
                                val zoneId = runCatching { ZoneId.of(todayState.locationTimeZoneId) }
                                    .getOrElse { ZoneId.of("UTC") }
                                appScope.launch {
                                    scheduler.schedule(
                                        locationId = todayState.locationId,
                                        openHour = DEFAULT_OPEN_HOUR,
                                        openMinute = DEFAULT_OPEN_MINUTE,
                                        zoneId = zoneId
                                    )
                                }
                            },
                            onExportCsv = {}
                        )
                    }
                    AppSection.INVENTORY -> {
                        TodayScreen(
                            state = todayState,
                            onScheduleReset = {
                                val zoneId = runCatching { ZoneId.of(todayState.locationTimeZoneId) }
                                    .getOrElse { ZoneId.of("UTC") }
                                appScope.launch {
                                    scheduler.schedule(
                                        locationId = todayState.locationId,
                                        openHour = DEFAULT_OPEN_HOUR,
                                        openMinute = DEFAULT_OPEN_MINUTE,
                                        zoneId = zoneId
                                    )
                                }
                            },
                            onExportCsv = {},
                            onAdjustItem = {}
                        )
                    }
                }
            }
        }
    }
}
private enum class AppSection { DASHBOARD, INVENTORY }
private const val DEFAULT_OPEN_HOUR = 4
private const val DEFAULT_OPEN_MINUTE = 30
