package com.example.monefy.presentation.screens.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.monefy.presentation.model.Finance
import com.example.monefy.presentation.model.FinanceType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val CURRENT_YEAR_DATE_PATTERN = "d MMMM"
private const val ANOTHER_YEAR_DATE_PATTERN = "d MMMM y"

@Composable
fun HistoryFinancesScreen(
    modifier: Modifier = Modifier,
    viewModel: HistoryFinancesScreenViewModel = hiltViewModel<HistoryFinancesScreenViewModel>(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val dates = uiState.finances.map { it.date }.distinct()

    // Выводим данные
    LazyColumn(modifier = modifier) {
        items(dates) { date ->
            Card(
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RectangleShape,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Делаем формат вывода даты (текущий год выводится без года, предыдущие с годом)
                val formattedDated = if (LocalDate.now().year == date.year)
                    date.format(DateTimeFormatter.ofPattern(CURRENT_YEAR_DATE_PATTERN))
                else date.format(DateTimeFormatter.ofPattern(ANOTHER_YEAR_DATE_PATTERN))

                Text(
                    text = formattedDated,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.W300,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 4.dp)
                )
            }
            FinancesHistoryBlock(
                finances = uiState.finances.filter { it.date == date },
                goToFinance = { viewModel.goToFinance(it) }
            )
        }
    }
}

@Composable
fun FinancesHistoryBlock(
    finances: List<Finance>,
    goToFinance: (Finance) -> Unit,
) {
    // Отображаем данные (дату и соот. ей финансы)
    Column {
        finances.forEach { finance ->
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { goToFinance(finance) }
            ) {
                Text(
                    text = finance.name,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier
                        .padding(top = 14.dp, bottom = 14.dp, start = 8.dp)
                        .widthIn(max = 220.dp)
                )

                val textColor = if (finance.type == FinanceType.REVENUE) Color.Green else Color.Red
                val text =
                    "${if (finance.type == FinanceType.REVENUE) "+" else "-"} ${finance.totalPrice}"

                Text(
                    text = text,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    color = textColor,
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 8.dp, end = 8.dp)
                        .widthIn(max = 180.dp)
                )
            }
        }
    }
}