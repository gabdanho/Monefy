package com.example.monefy.presentation.navigation.model.nav_type

import com.example.monefy.presentation.model.Category
import kotlinx.serialization.KSerializer

/**
 * [NavType] для передачи объектов [Category] между экранами.
 */
class CategoryNavType(serializer: KSerializer<Category> = Category.serializer())
    : NavTypeSerializer<Category>(
        serializer = serializer
    )