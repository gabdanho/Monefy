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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.example.monefy.presentation.constants.MAX_COUNT

@Composable
fun ItemCounter(
    count: Int,
    minusCount: () -> Unit,
    plusCount: () -> Unit,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "Количество",
        modifier = Modifier.padding(4.dp)
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
                contentDescription = "Уменьшить количество"
            )
        }
        // Поле ввода количества
        BasicTextField(
            value = count.toString(),
            onValueChange = {
                // Если пусто, то количество = 1
                if (it == "") {
                    onValueChange("1")
                }
                // Если пользователь удаляет символ
                else if (it.length < count.toString().length) {
                    onValueChange(it)
                }
                // Если 0 - ничего не делаем, т.к. 0 быть не может
                else if (it == "0") {
                    // 52 ngg
                }
                // Проверяем, что только цифры && Количество меньше константы
                else if (it.isDigitsOnly() && it.toInt() < MAX_COUNT) {
                    onValueChange(it)
                }
            },
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                color = Color.White
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(70.dp)
        )
        // Увеличить количество на 1 кнопкой
        IconButton(
            onClick = { plusCount() }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Увеличить количество"
            )
        }
    }
}