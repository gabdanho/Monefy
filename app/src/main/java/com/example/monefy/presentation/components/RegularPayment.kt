package com.example.monefy.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.monefy.R

/**
 * Чекбокс для отметки, является ли платеж регулярным.
 *
 * Позволяет включить или выключить повторяемость платежа.
 *
 * @param isRegular Флаг, указывающий, является ли платеж регулярным.
 * @param onValueChange Колбэк, вызываемый при изменении состояния чекбокса.
 * @param modifier Модификатор внешнего оформления.
 */
@Composable
fun RegularPayment(
    isRegular: Boolean,
    onValueChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Checkbox(
            checked = isRegular,
            onCheckedChange = { onValueChange() }
        )
        Text(
            text = stringResource(R.string.text_regular_finance),
            modifier = Modifier.clickable { onValueChange() }
        )
    }
}