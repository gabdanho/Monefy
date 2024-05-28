package com.example.monefy.data

import androidx.compose.ui.graphics.Color

class ColorConverter {
    fun toLong(color: Color): Long = color.value.toLong()

    fun toColor(value: Long): Color = Color(value)
}