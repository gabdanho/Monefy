package com.example.monefy.data

interface SpendsRepository {
    suspend fun insertSpend(spend: Spend)

    suspend fun deleteSpend(id: Int)

    suspend fun updateSpend(spend: Spend)
}