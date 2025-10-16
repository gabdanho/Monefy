package com.example.monefy.presentation.navigation.model.nav_type

import com.example.monefy.presentation.model.Finance
import kotlinx.serialization.KSerializer

/**
 * [NavType] для передачи объектов [Finance] между экранами.
 */
class FinanceNavType(serializer: KSerializer<Finance> = Finance.serializer())
    : NavTypeSerializer<Finance>(
        serializer = serializer
    )