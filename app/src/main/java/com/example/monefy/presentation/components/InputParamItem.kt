package com.example.monefy.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.monefy.R
import com.example.monefy.presentation.theme.defaultDimensions

/**
 * Элемент для ввода текстового параметра (название, сумма и т.д.).
 *
 * Включает текстовое поле и кнопку очистки введённого значения.
 *
 * @param paramName Название параметра (заголовок поля).
 * @param value Текущее значение поля.
 * @param onValueChange Обработчик изменения значения.
 * @param modifier Модификатор внешнего вида.
 * @param textColor Цвет текста заголовка.
 * @param keyboardOptions Настройки клавиатуры (тип ввода).
 * @param textFieldColors Цвета поля ввода (фон, рамка и т.д.).
 */
@Composable
fun InputParamItem(
    paramName: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = Color.White,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    textFieldColors: TextFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent
    ),
) {
    Column(modifier = modifier) {
        Text(
            text = paramName,
            color = textColor,
            modifier = Modifier.padding(defaultDimensions.verySmall)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = defaultDimensions.small)
        ) {
            TextField(
                value = value,
                onValueChange = { onValueChange(it) },
                singleLine = true,
                colors = textFieldColors,
                keyboardOptions = keyboardOptions,
                textStyle = TextStyle(fontSize = 20.sp),
                modifier = Modifier.weight(4f)
            )
            // Кнопка удаления названия
            IconButton(
                onClick = { onValueChange("") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.content_delete_value),
                )
            }
        }
    }
}