package com.example.monefy.presentation.utils

import com.example.monefy.presentation.constants.MAX_PRICE

fun priceFormatter(value: String): String? {
    if (value.all { it.isDigit() } || value.contains('.')) {
        when {
            // Если первый символ 0 и вводится число
            value.length > 1 && value[0] == '0' && value[1].isDigit() -> return value.drop(1).trim()
            // Если пользователь удаляет символ
            value.isEmpty() -> return value
            // Если пользователь пытается ввести после введённого нуля еще один - запрещаем
            value == "00" -> { /* Nichego */ }
            // Первый символ . - запрещаем
            value == "." -> { /* Nichego */ }
            // Проверяем чтобы число не было больше константы
            value.count { it == '.' } <= 1 && value.toDouble() < MAX_PRICE -> { return value.trim() }
            else -> null
        }
    }
    return null
}