package com.example.monefy.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * Отрисовывает круг с указанным цветом категории.
 *
 * Используется для отображения визуального цвета категории.
 *
 * @param colorLong Цвет в формате Long (ARGB).
 * @param center Координата центра круга.
 * @param radius Радиус круга.
 * @param modifier Модификатор для настройки внешнего вида.
 */
@Composable
fun CircleCategoryColor(
    colorLong: Long,
    center: Float,
    radius: Float,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier
    ) {
        drawCircle(
            color = Color(colorLong),
            radius = radius,
            center = Offset(center, center)
        )
        drawCircle(
            color = Color.Black,
            radius = radius,
            center = Offset(center, center),
            style = Stroke(width = 1f)
        )
    }
}