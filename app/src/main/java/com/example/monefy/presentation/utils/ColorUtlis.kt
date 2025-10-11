package com.example.monefy.presentation.utils

import androidx.compose.ui.graphics.Color

fun Color.darken(factor: Float): Color {
    return Color(
        red = (this.red * (1 - factor)).coerceIn(0f, 1f),
        green = (this.green * (1 - factor)).coerceIn(0f, 1f),
        blue = (this.blue * (1 - factor)).coerceIn(0f, 1f),
        alpha = this.alpha
    )
}