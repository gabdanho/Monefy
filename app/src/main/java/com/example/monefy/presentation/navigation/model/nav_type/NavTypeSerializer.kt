package com.example.monefy.presentation.navigation.model.nav_type

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

/**
 * Универсальный [NavType] для сериализации/десериализации объектов с использованием Kotlinx.serialization.
 *
 * @param T Тип передаваемого объекта
 * @property serializer Сериализатор [KSerializer] для типа [T]
 */
open class NavTypeSerializer<T>(
    private val serializer: KSerializer<T>
) : NavType<T>(isNullableAllowed = true) {

    override fun put(bundle: Bundle, key: String, value: T) {
        bundle.putString(key, serializeAsValue(value))
    }

    override fun get(bundle: Bundle, key: String): T? {
        val data = bundle.getString(key) ?: return null
        return parseValue(data)
    }

    override fun parseValue(value: String): T {
        val decoded = Uri.decode(value)
        return Json.decodeFromString(serializer, decoded)
    }

    override fun serializeAsValue(value: T): String {
        val rawJson = Json.encodeToString(serializer, value)
        return Uri.encode(rawJson)
    }
}