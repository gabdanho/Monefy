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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.monefy.R
import com.example.monefy.presentation.model.Finance
import com.example.monefy.presentation.model.FinanceType
import com.example.monefy.presentation.theme.defaultDimensions
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val CURRENT_YEAR_DATE_PATTERN = "d MMMM"
private const val ANOTHER_YEAR_DATE_PATTERN = "d MMMM y"

/**
 * Экран истории всех финансовых операций.
 *
 * @param modifier Модификатор.
 * @param viewModel ViewModel.
 */
@Composable
fun HistoryFinancesScreen(
    modifier: Modifier = Modifier,
    viewModel: HistoryFinancesScreenViewModel = hiltViewModel<HistoryFinancesScreenViewModel>(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val dates = uiState.finances.map { it.date }.distinct()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(
            LifecycleEventObserver{ _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    viewModel.getFinances()
                }
            }
        )
    }

    if (dates.isNotEmpty()) {
        LazyColumn(modifier = modifier) {
            items(dates) { date ->
                Card(
                    elevation = CardDefaults.cardElevation(defaultDimensions.small),
                    shape = RectangleShape,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Делаем формат вывода даты (текущий год выводится без года, предыдущие с годом)
                    val formattedDated = if (LocalDate.now().year == LocalDate.parse(date).year)
                        date.format(DateTimeFormatter.ofPattern(CURRENT_YEAR_DATE_PATTERN))
                    else date.format(DateTimeFormatter.ofPattern(ANOTHER_YEAR_DATE_PATTERN))

                    Text(
                        text = formattedDated,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.W300,
                        modifier = Modifier.padding(
                            top = defaultDimensions.small,
                            bottom = defaultDimensions.small,
                            start = defaultDimensions.verySmall
                        )
                    )
                }
                FinancesHistoryBlock(
                    finances = uiState.finances.filter { it.date == date }.reversed(),
                    goToFinance = { viewModel.goToFinance(it) }
                )
            }
        }
    } else {
        Text(
            text = stringResource(R.string.text_no_data),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(defaultDimensions.small)
        )
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
                        .padding(
                            top = defaultDimensions.medium,
                            bottom = defaultDimensions.medium,
                            start = defaultDimensions.small
                        )
                        .widthIn(max = defaultDimensions.historyBlockMaxWidth)
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
                        .padding(
                            top = defaultDimensions.small,
                            bottom = defaultDimensions.small,
                            end = defaultDimensions.small
                        )
                        .widthIn(max = defaultDimensions.historyBlockMaxWidth)
                )
            }
        }
    }
}