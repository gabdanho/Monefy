package com.example.monefy.data

import androidx.compose.ui.graphics.Color
import androidx.room.TypeConverter

class ColorConverter {
    @TypeConverter
    fun toLong(color: Color): Long = color.value.toLong()

    @TypeConverter
    fun toColor(value: Long): Color = Color(value)
}