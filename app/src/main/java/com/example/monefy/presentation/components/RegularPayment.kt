package com.example.monefy.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

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
            text = "Регулярный платёж",
            modifier = Modifier.clickable { onValueChange() }
        )
    }
}