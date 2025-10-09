package com.bbqreset.ui.design

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class BBQTab(
    val label: String,
    val badgeCount: Int? = null
)

@Composable
fun BBQTabs(
    tabs: List<BBQTab>,
    selectedIndex: Int,
    onSelectedChange: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedIndex,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.extendedColors.primary,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                color = MaterialTheme.extendedColors.primary,
                height = 3.dp
            )
        }
    ) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                selected = selectedIndex == index,
                onClick = { onSelectedChange(index) },
                selectedContentColor = MaterialTheme.extendedColors.primary,
                unselectedContentColor = MaterialTheme.extendedColors.mutedForeground,
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = tab.label,
                            style = MaterialTheme.extendedTypography.bodyMedium
                        )
                        tab.badgeCount?.takeIf { it > 0 }?.let {
                            BBQBadge(
                                text = it.toString(),
                                background = MaterialTheme.extendedColors.primary,
                                contentColor = MaterialTheme.extendedColors.primaryForeground
                            )
                        }
                    }
                }
            )
        }
    }
}
