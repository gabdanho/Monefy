package com.example.monefy.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import com.example.monefy.R
import com.example.monefy.presentation.theme.defaultDimensions
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun ColorPicker(changeColorCategory: (Long) -> Unit) {
    val colorController = rememberColorPickerController()

    Card {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(defaultDimensions.colorPickerColumnPadding)
                .size(
                    width = defaultDimensions.colorPickerWidth,
                    height = defaultDimensions.colorPickerHeight
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(defaultDimensions.pickerHeight)
                    .background(
                        color = colorController.selectedColor.value.copy(alpha = defaultDimensions.fullWeight),
                        shape = RoundedCornerShape(defaultDimensions.verySmall)
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

            }
            HsvColorPicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(defaultDimensions.hsvPickerHeight)
                    .padding(defaultDimensions.pickerPadding),
                controller = colorController
            )
            BrightnessSlider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(defaultDimensions.pickerPadding)
                    .height(defaultDimensions.pickerHeight),
                controller = colorController,
                initialColor = Color.White
            )
            Button(
                onClick = {
                    changeColorCategory(
                        colorController.selectedColor.value.copy(alpha = defaultDimensions.fullWeight)
                            .toArgb().toLong()
                    )
                }
            ) {
                Text(text = stringResource(R.string.button_select))
            }
        }
    }
}