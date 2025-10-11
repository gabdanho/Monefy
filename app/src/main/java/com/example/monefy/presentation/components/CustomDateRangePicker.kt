package com.example.monefy.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import com.example.monefy.presentation.utils.convertLongToLocalDate

@Composable
fun CustomDateRangePicker(
    updateDateRange: (List<LocalDate>) -> Unit,
) {
    val state = rememberDateRangePickerState()

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        DateRangePicker(
            state = state,
            title = {
                Text(text = "Выберите промежуток времени", modifier = Modifier.padding(16.dp))
            },
            headline = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        val startDate =
                            if (state.selectedStartDateMillis != null) convertLongToLocalDate(
                                state.selectedStartDateMillis!!
                            ) else "Начальная дата"
                        Text(
                            text = startDate.toString(),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        val endDate =
                            if (state.selectedEndDateMillis != null) convertLongToLocalDate(
                                state.selectedEndDateMillis!!
                            ) else "Конечная дата"
                        Text(
                            text = endDate.toString(),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Box(modifier = Modifier.weight(0.2f)) {
                        IconButton(
                            onClick = {
                                val startDate =
                                    if (state.selectedStartDateMillis != null)
                                        convertLongToLocalDate(state.selectedStartDateMillis!!)
                                    else LocalDate.now()
                                val endDate =
                                    if (state.selectedEndDateMillis == null && state.selectedStartDateMillis != null)
                                        convertLongToLocalDate(state.selectedStartDateMillis!!)
                                    else if (state.selectedEndDateMillis != null)
                                        convertLongToLocalDate(state.selectedEndDateMillis!!)
                                    else LocalDate.now()

                                updateDateRange(listOf(startDate, endDate))
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Done"
                            )
                        }
                    }
                }
            },
            showModeToggle = false
        )
    }
}