package com.example.monefy.data

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

class DateConverter {
    fun toLong(date: LocalDate): Long = date.toEpochDay()

    fun toDate(value: Long): LocalDate = LocalDate.ofEpochDay(value)
}