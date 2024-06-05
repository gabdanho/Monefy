package com.example.monefy.ui.screens

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.monefy.data.Finance
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Composable
fun SpendingListScreen(
    spendingViewModel: SpendingViewModel,
    rewriteSpendClick: () -> Unit,
) {
    val uiState by spendingViewModel.uiState.collectAsState()

    SpendingList(
        currentCategoryId = uiState.currentCategoryIdForFinances,
        changeSelectedFinanceToChange = spendingViewModel::changeSelectedFinanceToChange,
        changeSelectedCategory = spendingViewModel::changeSelectedCategory,
        getFinancesByCategoryId = spendingViewModel::getFinancesByCategoryId,
        rewriteSpendClick = rewriteSpendClick
    )
}

@Composable
fun SpendingList(
    currentCategoryId: Int,
    getFinancesByCategoryId: (Int) -> Flow<List<Finance>>,
    changeSelectedFinanceToChange: (Finance) -> Unit,
    rewriteSpendClick: () -> Unit,
    changeSelectedCategory: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var finances by rememberSaveable { mutableStateOf(listOf<Finance>()) }
    finances = getFinancesByCategoryId(currentCategoryId).collectAsState(initial = emptyList()).value

    if (finances.isNotEmpty()) {
        LazyColumn(modifier = modifier) {
            items(finances) { finance ->
                SpendingCard(
                    finance = finance,
                    rewriteSpendClick = rewriteSpendClick,
                    changeSelectedSpendToChange = changeSelectedFinanceToChange,
                    changeSelectedCategory = changeSelectedCategory,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
    else {
        Text(text = "Расходов нет. Добавь их!")
    }
}

@Composable
fun SpendingCard(
    finance: Finance,
    rewriteSpendClick: () -> Unit,
    changeSelectedSpendToChange: (Finance) -> Unit,
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
                changeSelectedSpendToChange(finance)
                rewriteSpendClick()
            }
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp)
        ) {
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
                Text(
                    text = finance.name,
                    style = MaterialTheme.typography.bodyLarge
                )
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
            Text(
                text = String.format("%.2f", finance.count.toDouble() * finance.price),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview
@Composable
fun SpendingCardPreview() {
    SpendingCard(
        finance = Finance(
            name = "Обед",
            date = LocalDate.now(),
            description = "Вкусненько поел",
            count = 1,
            price = 350.0
        ),
        rewriteSpendClick = { /*TODO*/ },
        changeSelectedSpendToChange = { },
        changeSelectedCategory = { }
    )
}