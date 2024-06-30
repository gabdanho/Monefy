package com.example.monefy.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun isDateInRange(date: LocalDate, startDate: LocalDate, endDate: LocalDate): Boolean {
    return (date.isAfter(startDate) || date.isEqual(startDate)) &&
            (date.isBefore(endDate) || date.isEqual(endDate))
}

fun convertLongToLocalDate(time: Long): LocalDate {
    return Instant.ofEpochMilli(time)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}