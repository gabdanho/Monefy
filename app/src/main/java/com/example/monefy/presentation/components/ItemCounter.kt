package com.example.monefy.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.monefy.R
import com.example.monefy.presentation.theme.defaultDimensions

/**
 * Счётчик количества (например, количества товаров или повторов платежа).
 *
 * @param count Текущее значение счётчика.
 * @param minusCount Колбэк для уменьшения значения.
 * @param plusCount Колбэк для увеличения значения.
 * @param modifier Модификатор внешнего вида.
 */
@Composable
fun ItemCounter(
    count: Int,
    minusCount: () -> Unit,
    plusCount: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.text_count),
        modifier = Modifier.padding(defaultDimensions.verySmall)
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        // Уменьшить количество на 1 кнопкой
        IconButton(
            onClick = { if (count > 1) minusCount() }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.content_decrease_count)
            )
        }
        BasicTextField(
            value = count.toString(),
            onValueChange = { },
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                color = Color.White
            ),
            readOnly = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(defaultDimensions.mediumSize)
        )
        // Увеличить количество на 1 кнопкой
        IconButton(
            onClick = { plusCount() }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = stringResource(R.string.count_increase_count)
            )
        }
    }
}