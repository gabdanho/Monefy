package com.example.monefy.utils

import java.time.LocalDate

fun isDateInRange(date: LocalDate, startDate: LocalDate, endDate: LocalDate): Boolean {
    return (date.isAfter(startDate) || date.isEqual(startDate)) &&
            (date.isBefore(endDate) || date.isEqual(endDate))
}