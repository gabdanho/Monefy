package com.example.monefy.data

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

class DateConverter {
    @TypeConverter
    fun toLong(date: LocalDate): Long = date.toEpochDay()

    @TypeConverter
    fun toDate(value: Long): LocalDate = LocalDate.ofEpochDay(value)
}