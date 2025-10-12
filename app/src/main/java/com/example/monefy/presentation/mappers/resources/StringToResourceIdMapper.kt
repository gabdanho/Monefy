package com.example.monefy.presentation.mappers.resources

import androidx.annotation.StringRes
import com.example.monefy.presentation.model.StringResName

/**
 * Интерфейс для преобразования идентификаторов строк (StringResName)
 * в реальные ресурсы строк Android (@StringRes).
 */
interface StringToResourceIdMapper {
    @StringRes
    fun map(resId: StringResName): Int
}