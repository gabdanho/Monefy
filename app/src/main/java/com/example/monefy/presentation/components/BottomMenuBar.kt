package com.example.monefy.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.monefy.R
import com.example.monefy.presentation.theme.defaultDimensions

/**
 * Нижняя панель навигации приложения.
 *
 * @param modifier Модификатор для настройки внешнего вида панели.
 * @param onPieChartClick Обработчик нажатия на кнопку "Диаграмма расходов".
 * @param onFinancesListClick Обработчик нажатия на кнопку "Список финансов".
 * @param onAddButtonClick Обработчик нажатия на кнопку добавления нового финанса.
 * @param onDiagramClick Обработчик нажатия на кнопку "Диаграммы".
 * @param onHistoryClick Обработчик нажатия на кнопку "История".
 */
@Composable
fun BottomMenuBar(
    modifier: Modifier = Modifier,
    onPieChartClick: () -> Unit = { },
    onFinancesListClick: () -> Unit = { },
    onAddButtonClick: () -> Unit = { },
    onDiagramClick: () -> Unit = { },
    onHistoryClick: () -> Unit = { },
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier.fillMaxWidth()
    ) {
        // Диаграммы
        IconButton(onClick = onDiagramClick) {
            Icon(
                painter = painterResource(R.drawable.diagrams),
                contentDescription = stringResource(R.string.content_diagrams),
                modifier = Modifier.size(defaultDimensions.iconSize)
            )
        }
        // Добавить финанс
        IconButton(onClick = onAddButtonClick) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.content_add_finance)
            )
        }
        // Донат с расходами
        IconButton(onClick = onPieChartClick) {
            Icon(
                painter = painterResource(R.drawable.chart),
                contentDescription = stringResource(R.string.content_donat_with_finanances),
                modifier = Modifier
                    .clip(CircleShape)
                    .size(defaultDimensions.bottomBarPieChartIconSize)
            )
        }
        // Список финансов
        IconButton(onClick = onFinancesListClick) {
            Icon(
                painter = painterResource(R.drawable.list),
                contentDescription = stringResource(R.string.content_finances_list)
            )
        }
        // История транзакций
        IconButton(onClick = onHistoryClick) {
            Icon(
                painter = painterResource(R.drawable.history),
                contentDescription = stringResource(R.string.content_history),
                modifier = Modifier.size(defaultDimensions.iconSize)
            )
        }
    }
}