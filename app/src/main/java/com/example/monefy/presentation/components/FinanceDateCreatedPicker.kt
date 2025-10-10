package com.example.monefy.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun FinanceDateCreatedPicker(
    pickedDate: LocalDate,
    showDialogState: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "Дата",
            modifier = Modifier.padding(4.dp)
        )
        TextField(
            value = if (pickedDate == LocalDate.now()) {
                "Сегодня"
            } else if (pickedDate == LocalDate.now().minusDays(1)) {
                "Вчера"
            } else {
                pickedDate.toString()
            },
            onValueChange = { },
            readOnly = true,
            trailingIcon = {
                IconButton(
                    // Нажимаем на иконку и вызываем диалоговое окно с выбором даты
                    onClick = { showDialogState() },
                ) {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = "Выбрать дату"
                    )
                }
            },
            modifier = Modifier
                .padding(bottom = 8.dp)
        )
    }
}