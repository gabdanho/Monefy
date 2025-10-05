package com.example.monefy.data.mappers.resources

import com.example.monefy.R
import com.example.monefy.presentation.model.StringResName

/**
 * Реализация маппера, которая сопоставляет все возможные
 * идентификаторы из presentation слоя с конкретными ресурсами строк в R.string.
 * Используется для отображения локализованных текстов в UI.
 */
class StringToResourceIdMapperImpl : StringToResourceIdMapper {

    private val resourceMapPresentation = mapOf(
        StringResName.ERROR_NO_NAME_AND_COLOR_CATEGORY to R.string.error_no_name_and_color_category,
        StringResName.ERROR_NO_NAME_CATEGORY to R.string.error_no_name_category,
        StringResName.ERROR_NO_COLOR_CATEGORY to R.string.error_no_color_category,
        StringResName.ERROR_TO_CREATE_CATEGORY to R.string.error_to_create_category,

        StringResName.SUCCESS_CATEGORY_CREATED to R.string.success_category_created
    )

    override fun map(resId: StringResName): Int {
        return resourceMapPresentation[resId]
            ?: throw IllegalArgumentException("CANT_MAP_THIS_ID_$resId")
    }
}