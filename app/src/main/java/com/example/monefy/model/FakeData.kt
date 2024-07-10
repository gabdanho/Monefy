package com.example.monefy.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.monefy.data.Category
import com.example.monefy.data.Finance

object FakeData {
    val fakeCategoriesList = listOf(
        Category(
            name = "Category 1",
            color = Color.Red.toArgb(),
            totalCategoryPrice = 52.0
        ),
        Category(
            name = "Category 2",
            color = Color.Green.toArgb(),
            totalCategoryPrice = 62.0
        ),
        Category(
            name = "Category 3",
            color = Color.Blue.toArgb(),
            totalCategoryPrice = 72.0
        ),
        Category(
            name = "Category 4",
            color = Color.Yellow.toArgb(),
            totalCategoryPrice = 82.0
        )
    )

    val fakeFinances = listOf(
        Finance(
            name = "Finance 1",
            price = 100.0,
            count = 1
        ),
        Finance(
            name = "Finance 2",
            price = 100.0,
            count = 3
        ),
        Finance(
            name = "Finance 3",
            price = 50.0,
            count = 5,
            description = "Lorem ipsum"
        ),
        Finance(
            name = "Finance 4",
            price = 100.0,
            count = 1
        )
    )
}