package com.example.monefy.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monefy.utils.Constants
import kotlinx.coroutines.launch
import kotlin.math.abs

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
    financesViewModel: FinancesViewModel,
    updateScreen: () -> Unit
) {
    val uiState by financesViewModel.uiState.collectAsState()

    MainDiagramScreen(
        selectedTabIndex = uiState.selectedDiagramTabIndex,
        getFinancesInDateRange = financesViewModel::getFinancesInDateRange,
        changeSelectedDiagramTabIndex = financesViewModel::changeSelectedDiagramTabIndex,
        updateScreen = updateScreen
    )
}

@Composable
fun MainDiagramScreen(
    selectedTabIndex: Int,
    getFinancesInDateRange: suspend (Int) -> Map<String, List<Double>>,
    changeSelectedDiagramTabIndex: (Int) -> Unit,
    updateScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                Constants.diagramsItems.forEachIndexed { index, item ->
                    Tab(
                        selected = index == selectedTabIndex,
                        onClick = {
                            changeSelectedDiagramTabIndex(index)
                            updateScreen()
                        },
                        text = { Text(item) }
                    )
                }
            }

            // Мапа, где первый аргумент это период даты, а второй список сумм, где первый доходы, а второй расходы
            var diagramsInfo by remember { mutableStateOf<Map<String, List<Double>>>(emptyMap()) }

            LaunchedEffect(selectedTabIndex) {
                diagramsInfo = getFinancesInDateRange(selectedTabIndex)
            }

            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier.fillMaxSize()
            ) {
                if (diagramsInfo.isNotEmpty()) Diagrams(diagramsInfo)
                else Text(text = "Доходов и расходов не найдено")
            }
        }
    }
}

@Composable
fun Diagrams(
    diagramsInfo: Map<String, List<Double>>
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Скроллим к последнему элементу
    DisposableEffect(Unit) {
        coroutineScope.launch {
            listState.scrollToItem(diagramsInfo.size - 1)
        }
        onDispose {  }
    }

    DrawLegend()

    LazyRow(
        state = listState,
        modifier = Modifier.padding(start = 8.dp)
    ) {
        items(diagramsInfo.keys.toList()) {
            DrawDiagramBlock(
                revenuesSum = diagramsInfo[it]?.first() ?: 0.0,
                spendsSum = diagramsInfo[it]?.last() ?: 0.0,
                date = it
            )
        }
    }
}

// Рисуем легенду о цветах диаграмм
@Composable
fun DrawLegend() {
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = Modifier
            .height(300.dp)
            .width(320.dp)
    ) {
        // Доходы
        drawCircle(
            color = Color.Magenta,
            radius = 10f,
            center = Offset(40f, size.height - 30f)
        )

        drawText(
            textMeasurer = textMeasurer,
            text = "Расходы",
            topLeft = Offset(60f, size.height - 60f)
        )

        // Расходы
        drawCircle(
            color = Color.Blue,
            radius = 10f,
            center = Offset(250f, size.height - 30f)
        )

        drawText(
            textMeasurer = textMeasurer,
            text = "Доходы",
            topLeft = Offset(270f, size.height - 60f)
        )

        // Прибыль
        drawCircle(
            color = Color.Green,
            radius = 10f,
            center = Offset(470f, size.height - 30f)
        )

        drawText(
            textMeasurer = textMeasurer,
            text = "Прибыль",
            topLeft = Offset(490f, size.height - 60f)
        )

        // Убыток
        drawCircle(
            color = Color.Red,
            radius = 10f,
            center = Offset(690f, size.height - 30f)
        )

        drawText(
            textMeasurer = textMeasurer,
            text = "Убыток",
            topLeft = Offset(710f, size.height - 60f)
        )
    }
}

@Composable
fun DrawDiagramBlock(
    revenuesSum: Double,
    spendsSum: Double,
    date: String,
    paddingX: Float = 5f,
    paddingY: Float = 200f,
) {
    val textMeasurer = rememberTextMeasurer()

    val totalSum = revenuesSum + spendsSum
    val revenuesPercent = (revenuesSum / totalSum).toFloat()
    val spendsPercent = (spendsSum / totalSum).toFloat()

    Canvas(
        modifier = Modifier
            .height(300.dp)
            .width(90.dp)
    ) {
        val maxHeight = size.height - 400f
        val paddingSumText = 740f

        // Дата
        drawText(
            textMeasurer = textMeasurer,
            text = date,
            topLeft = Offset(paddingX + 35f, size.height - paddingY),
            style = TextStyle(fontSize = 15.sp)
        )

        // Рисуем блок диаграммы
        drawLine(
            start = Offset(x = (paddingX + 5f), y = size.height - paddingY),
            end = Offset(x = (paddingX + 5f), y = size.height - (paddingY + 20f)),
            color = Color.Black,
            strokeWidth = 2f
        )

        drawLine(
            start = Offset(x = (paddingX + 5f), y = size.height - paddingY),
            end = Offset(x = (paddingX + 180f), y = size.height - paddingY),
            color = Color.Black,
            strokeWidth = 2f
        )

        drawLine(
            start = Offset(x = (paddingX + 180f), y = size.height - paddingY),
            end = Offset(x = (paddingX + 180f), y = size.height - (paddingY + 20f)),
            color = Color.Black,
            strokeWidth = 2f
        )

        // Диаграммы
        drawLine(
            start = Offset(x = (paddingX + 35f), y = size.height - paddingY),
            end = Offset(x = (paddingX + 35f), y = size.height - (paddingY + (revenuesPercent * maxHeight))),
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
                android.graphics.Paint().apply {
                    textSize = 15.sp.toPx()
                    color = android.graphics.Color.BLACK
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
            canvas.restore()
        }

        drawLine(
            start = Offset(x = (paddingX + 93f), y = size.height - paddingY),
            end = Offset(x = (paddingX + 93f), y = size.height - (paddingY + (spendsPercent * maxHeight))),
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
                android.graphics.Paint().apply {
                    textSize = 15.sp.toPx()
                    color = android.graphics.Color.BLACK
                    textAlign = android.graphics.Paint.Align.CENTER
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
            end = Offset(x = (paddingX + 150f), y = size.height - (paddingY + (differencePercent * maxHeight))),
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
                android.graphics.Paint().apply {
                    textSize = 15.sp.toPx()
                    color = android.graphics.Color.BLACK
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
            canvas.restore()
        }
    }
}

//@Preview
//@Composable
//fun DiagramsTestPreview() {
//    DiagramsTest(
//        getFinancesInDateRange = suspend { }
//    )
//}