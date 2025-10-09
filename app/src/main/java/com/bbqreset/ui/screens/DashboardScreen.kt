package com.bbqreset.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bbqreset.ui.design.BBQButton
import com.bbqreset.ui.design.BBQButtonVariant
import com.bbqreset.ui.design.BBQCard
import com.bbqreset.ui.design.BBQTheme
import com.bbqreset.ui.design.extendedColors
import com.bbqreset.ui.design.extendedTypography
import com.bbqreset.ui.design.spacing

data class DayReport(
    val label: String,
    val sold: Int
)

@Composable
fun DashboardScreen(
    locationName: String,
    overview: InventorySummary,
    dailyReports: List<DayReport>,
    momDeltaPercent: Int,
    onGoToInventory: () -> Unit,
    onScheduleReset: () -> Unit,
    onExportCsv: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = MaterialTheme.spacing.xl, vertical = MaterialTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)) {
            Text(
                text = "Dashboard",
                style = MaterialTheme.extendedTypography.displayLarge
            )
            Text(
                text = "Location: $locationName",
                style = MaterialTheme.extendedTypography.bodyMedium,
                color = MaterialTheme.extendedColors.mutedForeground
            )
        }

        OverviewCard(overview)

        QuickActionsCard(
            onGoToInventory = onGoToInventory,
            onScheduleReset = onScheduleReset,
            onExportCsv = onExportCsv
        )

        TrendsCard(
            momDeltaPercent = momDeltaPercent,
            dailyReports = dailyReports
        )
    }
}

@Composable
private fun OverviewCard(overview: InventorySummary) {
    BBQCard {
        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)) {
            Text(
                text = "Inventory Overview",
                style = MaterialTheme.extendedTypography.titleLarge
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Stat(label = "Start qty", value = overview.startTotal)
                Stat(label = "Sold", value = overview.soldTotal)
                Stat(label = "On hand", value = overview.onHandTotal)
                Stat(label = "Low stock", value = overview.lowStockCount)
            }
        }
    }
}

@Composable
private fun Stat(label: String, value: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)) {
        Text(text = value.toString(), style = MaterialTheme.extendedTypography.titleLarge)
        Text(
            text = label,
            style = MaterialTheme.extendedTypography.labelMedium,
            color = MaterialTheme.extendedColors.mutedForeground
        )
    }
}

@Composable
private fun QuickActionsCard(
    onGoToInventory: () -> Unit,
    onScheduleReset: () -> Unit,
    onExportCsv: () -> Unit
) {
    BBQCard {
        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.extendedTypography.titleLarge
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
            ) {
                BBQButton(text = "Open Inventory", onClick = onGoToInventory)
                BBQButton(text = "Schedule Reset", variant = BBQButtonVariant.SECONDARY, onClick = onScheduleReset)
                BBQButton(text = "Export CSV", variant = BBQButtonVariant.OUTLINE, onClick = onExportCsv)
            }
        }
    }
}

@Composable
private fun TrendsCard(
    momDeltaPercent: Int,
    dailyReports: List<DayReport>
) {
    BBQCard {
        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)) {
            Text(
                text = "Reporting",
                style = MaterialTheme.extendedTypography.titleLarge
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)) {
                    Text("MoM", style = MaterialTheme.extendedTypography.labelMedium, color = MaterialTheme.extendedColors.mutedForeground)
                    val sign = if (momDeltaPercent >= 0) "+" else ""
                    Text("$sign$momDeltaPercent%", style = MaterialTheme.extendedTypography.titleLarge)
                }
                Column(modifier = Modifier.padding(start = MaterialTheme.spacing.lg)) {
                    Text("Day to day (sold)", style = MaterialTheme.extendedTypography.labelMedium, color = MaterialTheme.extendedColors.mutedForeground)
                    Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)) {
                        dailyReports.forEach { r ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(r.label, style = MaterialTheme.extendedTypography.bodyMedium)
                                Text(r.sold.toString(), style = MaterialTheme.extendedTypography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardPreview() {
    BBQTheme {
        DashboardScreen(
            locationName = "Downtown BBQ",
            overview = InventorySummary(136, 62, 74, 1),
            dailyReports = listOf(
                DayReport("Wed", 54), DayReport("Thu", 48), DayReport("Fri", 72),
                DayReport("Sat", 95), DayReport("Sun", 66), DayReport("Mon", 40), DayReport("Tue", 58)
            ),
            momDeltaPercent = 12,
            onGoToInventory = {},
            onScheduleReset = {},
            onExportCsv = {}
        )
    }
}
