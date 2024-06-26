package com.example.monefy.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.monefy.utils.Constants
import com.example.monefy.utils.CustomDateRangePicker
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagramsScreen(
    showDateRangeDialog: Boolean,
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    changeShowDateRangeDialog: (Boolean) -> Unit,
    updateCustomDateRange: (List<LocalDate>) -> Unit,
    changeSelectedTabIndex: (Int) -> Unit,
    updateScreen: () -> Unit,
) {
    Scaffold(modifier = modifier) { innerPadding ->
        if (showDateRangeDialog) {
            ModalBottomSheet(onDismissRequest = { changeShowDateRangeDialog(false) }) {
                CustomDateRangePicker(
                    changeShowDateRangeDialog = changeShowDateRangeDialog,
                    updateDateRange = updateCustomDateRange,
                    updateScreen = updateScreen
                )
            }
        }
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                Constants.tabItems.forEachIndexed { index, item ->
                    Tab(
                        selected = index == selectedTabIndex,
                        onClick = {
                            changeSelectedTabIndex(index)
                            updateScreen()
                        },
                        text = { Text(item) }
                    )
                }
            }
        }
    }
}