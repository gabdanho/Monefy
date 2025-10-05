package com.example.monefy.presentation.screens.history

import android.annotation.SuppressLint
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.monefy.data.local.entity.Finance
import com.example.monefy.presentation.screens.FinancesViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HistoryFinancesScreen(
    financesViewModel: FinancesViewModel,
    goToFinance: (Finance) -> Unit
) {
    val finances = financesViewModel.getSortedFinancesByDate().collectAsState(initial = emptyList()).value
    // Список дат должен быть без дупликатов
    val dates = finances.map { it.date }.distinct()

    HistoryFinances(finances, dates, goToFinance)
}

@SuppressLint("WeekBasedYear")
@Composable
fun HistoryFinances(
    finances: List<Finance>,
    dates: List<LocalDate>,
    goToFinance: (Finance) -> Unit,
    modifier: Modifier = Modifier
) {
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
                    date.format(DateTimeFormatter.ofPattern("d MMMM"))
                else date.format(DateTimeFormatter.ofPattern("d MMMM YYYY"))

                Text(
                    text = formattedDated,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.W300,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 4.dp)
                )
            }
            FinancesHistoryBlock(
                finances = finances.filter { it.date == date },
                goToFinance = goToFinance
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

                // В зависимости от дохода или расхода меняется цвет
                val colorText = if (finance.type == "Доход") Color.Green
                else Color.Red

                Text(
                    text = "${if (finance.type == "Доход") "+" else "-"} ${finance.price * finance.count}",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = TextStyle(color = colorText),
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 8.dp, end = 8.dp)
                        .widthIn(max = 180.dp)
                )
            }
        }
    }
}