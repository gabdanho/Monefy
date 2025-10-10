package com.example.monefy.presentation.screens.diagrams

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monefy.R
import kotlinx.coroutines.launch
import kotlin.math.abs
import androidx.compose.ui.platform.LocalResources
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.monefy.presentation.model.DiagramInfo

/*
* Сделать диаграммы по месяцам, по годам и т.д.
* В соответствии с пунктом выше, получить мапу с датой и соответсвующими доходами/расходами за этот период
* Выводим минимум 5 лет, месяцев и т.д. (если есть за предыдущие до вплоть до них)
* Строим рядом с этими диаграммами диаграммы с прибылью, убытками
* Снизу, под диаграммами, будут подписаны даты. Вверху суммы.
* По скольку цвета будут статичными, то делаем легенду об этих цветах
*/

@Composable
fun DiagramScreen(
    modifier: Modifier = Modifier,
    viewModel: DiagramsScreenViewModel = hiltViewModel<DiagramsScreenViewModel>(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val diagramsItems = LocalResources.current.getStringArray(R.array.diagramsItems)

    Scaffold(modifier = modifier) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            TabRow(
                selectedTabIndex = uiState.selectedTabIndex,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                diagramsItems.forEachIndexed { index, item ->
                    Tab(
                        selected = index == uiState.selectedTabIndex,
                        onClick = { viewModel.changeSelectedDiagramTabIndex(index) },
                        text = { Text(item) }
                    )
                }
            }

            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier.fillMaxSize()
            ) {
                if (uiState.diagramsInfo.isNotEmpty()) Diagrams(
                    diagramsInfo = uiState.diagramsInfo,
                    modifier = Modifier.padding(start = 8.dp)
                )
                else Text(text = "Доходов и расходов не найдено")
            }
        }
    }

}

// Вызываем функцию для рисования диаграмм, легенд, дат
@Composable
private fun Diagrams(
    diagramsInfo: List<DiagramInfo>,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Скроллим к последнему элементу
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            listState.scrollToItem(diagramsInfo.size - 1)
        }
    }

    DrawLegend(
        Modifier
            .fillMaxWidth()
            .height(50.dp)
    )

    LazyRow(
        state = listState,
        modifier = modifier
    ) {
        items(diagramsInfo) { sums ->
            DrawDiagramBlock(
                revenuesSum = sums.totalRevenuesToExpenses.first,
                spendsSum = sums.totalRevenuesToExpenses.second,
                date = sums.date,
                modifier = Modifier
                    .height(300.dp)
                    .width(90.dp)
            )
        }
    }
}

// Рисуем легенду о цветах диаграмм
@Composable
private fun DrawLegend(
    modifier: Modifier = Modifier,
    paddingY: Float = 60f,
) {
    val textMeasurer = rememberTextMeasurer()
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Card(
        shape = RoundedCornerShape(
            topStart = 20.dp,
            topEnd = 20.dp
        ),
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Canvas(
                modifier = Modifier
                    .height(300.dp)
                    .width(320.dp)
            ) {
                // Расходы
                drawCircle(
                    color = Color.Magenta,
                    radius = 10f,
                    center = Offset(40f, size.height - paddingY)
                )

                drawText(
                    textMeasurer = textMeasurer,
                    style = TextStyle(color = onSurfaceColor),
                    text = "Расходы",
                    topLeft = Offset(60f, size.height - (paddingY + 30))
                )

                // Доходы
                drawCircle(
                    color = Color.Blue,
                    radius = 10f,
                    center = Offset(250f, size.height - paddingY)
                )

                drawText(
                    textMeasurer = textMeasurer,
                    style = TextStyle(color = onSurfaceColor),
                    text = "Доходы",
                    topLeft = Offset(270f, size.height - (paddingY + 30))
                )

                // Прибыль
                drawCircle(
                    color = Color.Green,
                    radius = 10f,
                    center = Offset(470f, size.height - paddingY)
                )

                drawText(
                    textMeasurer = textMeasurer,
                    style = TextStyle(color = onSurfaceColor),
                    text = "Прибыль",
                    topLeft = Offset(490f, size.height - (paddingY + 30))
                )

                // Убыток
                drawCircle(
                    color = Color.Red,
                    radius = 10f,
                    center = Offset(690f, size.height - paddingY)
                )

                drawText(
                    textMeasurer = textMeasurer,
                    style = TextStyle(color = onSurfaceColor),
                    text = "Убыток",
                    topLeft = Offset(710f, size.height - (paddingY + 30))
                )
            }
        }
    }
}

// Рисуем диаграммы
@Composable
private fun DrawDiagramBlock(
    revenuesSum: Double,
    spendsSum: Double,
    date: String,
    modifier: Modifier = Modifier,
    paddingX: Float = 5f,
    paddingY: Float = 200f,
) {
    val textMeasurer = rememberTextMeasurer()

    val totalSum = revenuesSum + spendsSum
    val revenuesPercent = (revenuesSum / totalSum).toFloat()
    val spendsPercent = (spendsSum / totalSum).toFloat()

    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Canvas(
        modifier = modifier
    ) {
        val maxHeight = size.height - 400f
        val paddingSumText = 680f

        // Дата
        drawText(
            textMeasurer = textMeasurer,
            text = date,
            topLeft = Offset(paddingX + 35f, size.height - paddingY),
            style = TextStyle(fontSize = 15.sp, color = onSurfaceColor)
        )

        // Рисуем блок диаграммы
        drawLine(
            start = Offset(x = (paddingX + 5f), y = size.height - paddingY),
            end = Offset(x = (paddingX + 5f), y = size.height - (paddingY + 20f)),
            color = onSurfaceColor,
            strokeWidth = 2f
        )

        drawLine(
            start = Offset(x = (paddingX + 5f), y = size.height - paddingY),
            end = Offset(x = (paddingX + 180f), y = size.height - paddingY),
            color = onSurfaceColor,
            strokeWidth = 2f
        )

        drawLine(
            start = Offset(x = (paddingX + 180f), y = size.height - paddingY),
            end = Offset(x = (paddingX + 180f), y = size.height - (paddingY + 20f)),
            color = onSurfaceColor,
            strokeWidth = 2f
        )

        // Диаграммы
        drawLine(
            start = Offset(x = (paddingX + 35f), y = size.height - paddingY),
            end = Offset(
                x = (paddingX + 35f),
                y = size.height - (paddingY + (revenuesPercent * maxHeight))
            ),
            color = Color.Blue,
            strokeWidth = 40f
        )

        // Пишем сумму для доходов
        drawIntoCanvas { canvas ->
            canvas.save()
            canvas.rotate(270f)
            drawContext.canvas.nativeCanvas.drawText(
                if (revenuesSum != 0.0) revenuesSum.toString() else "",
                size.height - (paddingY + (spendsPercent * maxHeight)) - paddingSumText,
                paddingX + 45f,
                Paint().apply {
                    textSize = 15.sp.toPx()
                    color = onSurfaceColor.toArgb()
                    textAlign = Paint.Align.CENTER
                }
            )
            canvas.restore()
        }

        drawLine(
            start = Offset(x = (paddingX + 93f), y = size.height - paddingY),
            end = Offset(
                x = (paddingX + 93f),
                y = size.height - (paddingY + (spendsPercent * maxHeight))
            ),
            color = Color.Magenta,
            strokeWidth = 40f
        )

        // Пишем сумму для расходов
        drawIntoCanvas { canvas ->
            canvas.save()
            canvas.rotate(270f)
            drawContext.canvas.nativeCanvas.drawText(
                if (spendsSum != 0.0) spendsSum.toString() else "",
                size.height - (paddingY + (revenuesPercent * maxHeight)) - paddingSumText,
                paddingX + 105f,
                Paint().apply {
                    textSize = 15.sp.toPx()
                    color = onSurfaceColor.toArgb()
                    textAlign = Paint.Align.CENTER
                }
            )
            canvas.restore()
        }

        // Диаграмма убытка или дохода
        val differenceSum = revenuesSum - spendsSum
        val differencePercent = (abs(differenceSum) / totalSum).toFloat()

        // Выбираем цвет в зависимости чего больше доходов или расходов (difference > 0 - прибыль, иначе убыль)
        val differenceColor = if (differenceSum > 0) Color.Green else Color.Red

        drawLine(
            start = Offset(x = (paddingX + 150f), y = size.height - paddingY),
            end = Offset(
                x = (paddingX + 150f),
                y = size.height - (paddingY + (differencePercent * maxHeight))
            ),
            color = differenceColor,
            strokeWidth = 40f
        )

        // Пишем сумму для разницы
        drawIntoCanvas { canvas ->
            canvas.save()
            canvas.rotate(270f)
            drawContext.canvas.nativeCanvas.drawText(
                if (differenceSum != 0.0) abs(differenceSum).toString() else "",
                size.height - (paddingY + ((1f - differencePercent) * maxHeight)) - paddingSumText,
                paddingX + 165f,
                Paint().apply {
                    textSize = 15.sp.toPx()
                    color = onSurfaceColor.toArgb()
                    textAlign = Paint.Align.CENTER
                }
            )
            canvas.restore()
        }
    }
}