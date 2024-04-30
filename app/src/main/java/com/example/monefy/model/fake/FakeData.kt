package com.example.monefy.model.fake

import androidx.compose.ui.graphics.Color
import com.example.monefy.model.Category
import com.example.monefy.model.Expense

object FakeData {
    val fakeCategories = listOf(
        Category(
            name = "Дом и быт",
            color = Color.Red,
            expenses = listOf(
                Expense(
                    name = "Утюг",
                    price = 4599.0,
                    count = 1
                ),
                Expense(
                    name = "Коврик для ванны",
                    price = 599.99,
                    count = 1
                ),
                Expense(
                    name = "Краска для стен (красная)",
                    price = 1499.0,
                    count = 2
                )
            )
        ),
        Category(
            name = "Продукты питания",
            color = Color.Green,
            expenses = listOf(
                Expense(
                    name = "Макароны",
                    price = 69.99,
                    count = 2
                ),
                Expense(
                    name = "Сливки 20%",
                    price = 120.99,
                    count = 1
                ),
                Expense(
                    name = "Сыр Российский",
                    price = 256.78,
                    count = 1
                )
            )
        ),
        Category(
            name = "Красота",
            color = Color.Cyan,
            expenses = listOf(
                Expense(
                    name = "Помада",
                    price = 1200.0,
                    count = 1
                ),
                Expense(
                    name = "Тушь для ресниц",
                    price = 899.99,
                    count = 1
                )
            )
        ),
        Category(
            name = "Одежда",
            color = Color.Yellow,
            expenses = listOf(
                Expense(
                    name = "Кроссовки Adidas",
                    price = 4599.0,
                    count = 1
                )
            )
        )
    )
}