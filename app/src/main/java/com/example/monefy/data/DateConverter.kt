package com.example.monefy.data

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

class DateConverter {
    @TypeConverter
    fun toLong(date: LocalDateTime): Long = date.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()

    @TypeConverter
    fun toDate(value: Long): LocalDateTime {
        return value.let { LocalDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneOffset.UTC) }
    }
}