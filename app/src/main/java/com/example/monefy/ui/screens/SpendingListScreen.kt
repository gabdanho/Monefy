package com.example.monefy.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.monefy.data.Spend
import kotlinx.coroutines.flow.Flow

@Composable
fun SpendingListScreen(
    spendingViewModel: SpendingViewModel,
    rewriteSpendClick: () -> Unit,
) {
    val uiState by spendingViewModel.uiState.collectAsState()

    SpendingList(
        currentCategoryId = uiState.selectedCategoryIdSpends,
        getSpendsByCategoryId = spendingViewModel::getSpendsByCategoryId,
        changeSelectedSpendToChange = spendingViewModel::changeSelectedSpendToChange,
        changeSelectedCategory = spendingViewModel::changeSelectedCategory,
        rewriteSpendClick = rewriteSpendClick
    )
}

@Composable
fun SpendingList(
    currentCategoryId: Int,
    getSpendsByCategoryId: (Int) -> Flow<List<Spend>>,
    changeSelectedSpendToChange: (Spend) -> Unit,
    rewriteSpendClick: () -> Unit,
    changeSelectedCategory: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val spends by getSpendsByCategoryId(currentCategoryId).collectAsState(emptyList())

    if (spends.isNotEmpty()) {
        LazyColumn(modifier = modifier) {
            items(spends) { spend ->
                SpendingCard(
                    spend = spend,
                    rewriteSpendClick = rewriteSpendClick,
                    changeSelectedSpendToChange = changeSelectedSpendToChange,
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
    spend: Spend,
    rewriteSpendClick: () -> Unit,
    changeSelectedSpendToChange: (Spend) -> Unit,
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
                changeSelectedCategory(spend.categoryId)
                changeSelectedSpendToChange(spend)
                rewriteSpendClick()
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = spend.name,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = String.format("%.2f", 52.525252),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun SpendingListPreview() {
//    val spendingViewModel = SpendingViewModel(FakeData.fakeCategories)
//    SpendingListScreen(
//        spendingViewModel = spendingViewModel,
//        rewriteSpendClick = { _ -> }
//    )
//}