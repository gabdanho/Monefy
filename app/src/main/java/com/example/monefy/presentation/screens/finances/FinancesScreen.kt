package com.example.monefy.presentation.screens.finances

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.monefy.data.local.entity.Finance
import com.example.monefy.presentation.screens.FinancesViewModel

@Composable
fun FinancesScreen(
    financesViewModel: FinancesViewModel,
    rewriteFinanceClick: () -> Unit,
) {
    val uiState by financesViewModel.uiState.collectAsState()

    var finances by rememberSaveable { mutableStateOf(listOf<Finance>()) }
    finances = financesViewModel.getFinancesByCategoryId(uiState.currentCategoryIdForFinances).collectAsState(initial = emptyList()).value

    FinancesList(
        finances = finances,
        changeSelectedFinanceToChange = financesViewModel::changeSelectedFinanceToChange,
        changeSelectedCategory = financesViewModel::changeSelectedCategory,
        rewriteFinanceClick = rewriteFinanceClick
    )
}

// Список финансов
@Composable
fun FinancesList(
    finances: List<Finance>,
    changeSelectedFinanceToChange: (Finance) -> Unit,
    rewriteFinanceClick: () -> Unit,
    changeSelectedCategory: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Если есть финансы, то отображаем их
    if (finances.isNotEmpty()) {
        LazyColumn(modifier = modifier) {
            items(finances) { finance ->
                FinanceCard(
                    finance = finance,
                    rewriteFinanceClick = rewriteFinanceClick,
                    changeSelectedFinanceToChange = changeSelectedFinanceToChange,
                    changeSelectedCategory = changeSelectedCategory,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
    // Расходов нет, то говорим об этом пользователю
    else {
        Text(text = "Расходов нет. Добавь их!")
    }
}

// Карточка финанса
@Composable
fun FinanceCard(
    finance: Finance,
    rewriteFinanceClick: () -> Unit,
    changeSelectedFinanceToChange: (Finance) -> Unit,
    changeSelectedCategory: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                ambientColor = Color.Black,
                spotColor = Color.Black,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable {
                changeSelectedCategory(finance.categoryId)
                changeSelectedFinanceToChange(finance)
                rewriteFinanceClick()
            }
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp)
        ) {
            // Дата сделки
            Text(
                text = finance.date.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 24.dp, top = 8.dp)
                .fillMaxWidth()
        ) {
            Column {
                // Название финанса
                Text(
                    text = finance.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                // Если есть описание, то выводим
                if (finance.description.isNotEmpty()) {
                    Text(
                        text = finance.description,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = 200.dp)
                    )
                }
            }
            // Выводим прайс
            Text(
                text = String.format("%.2f", finance.count.toDouble() * finance.price),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}