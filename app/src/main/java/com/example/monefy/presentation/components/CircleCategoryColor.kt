package com.example.monefy.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun CircleCategoryColor(
    colorLong: Long,
    center: Float,
    radius: Float,
    modifier: Modifier = Modifier
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