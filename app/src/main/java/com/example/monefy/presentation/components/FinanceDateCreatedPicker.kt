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
import androidx.compose.ui.res.stringResource
import com.example.monefy.R
import com.example.monefy.presentation.theme.defaultDimensions
import java.time.LocalDate

@Composable
fun FinanceDateCreatedPicker(
    pickedDate: LocalDate,
    showDialogState: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.text_date),
            modifier = Modifier.padding(defaultDimensions.verySmall)
        )
        TextField(
            value = if (pickedDate == LocalDate.now()) {
                stringResource(R.string.text_today)
            } else if (pickedDate == LocalDate.now().minusDays(1)) {
                stringResource(R.string.text_tomorrow)
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
                        contentDescription = stringResource(R.string.button_select_date)
                    )
                }
            },
            modifier = Modifier
                .padding(bottom = defaultDimensions.small)
        )
    }
}