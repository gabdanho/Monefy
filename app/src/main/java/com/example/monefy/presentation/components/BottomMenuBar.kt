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
import androidx.compose.ui.unit.dp
import com.example.monefy.R

// Нижнее меню
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
                contentDescription = "Диаграммы",
                modifier = Modifier.size(25.dp)
            )
        }
        // Добавить финанс
        IconButton(onClick = onAddButtonClick) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Добавить финанс"
            )
        }
        // Донат с расходами
        IconButton(onClick = onPieChartClick) {
            Icon(
                painter = painterResource(R.drawable.chart),
                contentDescription = "Донат с расходами",
                modifier = Modifier
                    .clip(CircleShape)
                    .size(40.dp)
            )
        }
        // Список финансов
        IconButton(onClick = onFinancesListClick) {
            Icon(
                painter = painterResource(R.drawable.list),
                contentDescription = "Список финансов"
            )
        }
        // История транзакций
        IconButton(onClick = onHistoryClick) {
            Icon(
                painter = painterResource(R.drawable.history),
                contentDescription = "История транзакций",
                modifier = Modifier.size(25.dp)
            )
        }
    }
}