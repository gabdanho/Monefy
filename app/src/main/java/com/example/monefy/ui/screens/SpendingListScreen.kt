package com.example.monefy.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.monefy.R
import com.example.monefy.model.Expense
import com.example.monefy.model.fake.FakeData

@Composable
fun SpendingListScreen(
    spendingViewModel: SpendingViewModel,
    rewriteSpendClick: (Expense) -> Unit,
    modifier: Modifier = Modifier
) {
    val spends = spendingViewModel.uiState.value.selectedSpendingList
    if (spends.isNotEmpty()) {
        LazyColumn(modifier = modifier) {
            items(spends) { spend ->
                SpendingCard(
                    spend = spend,
                    rewriteSpendClick = rewriteSpendClick,
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
    spend: Expense,
    rewriteSpendClick: (Expense) -> Unit,
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
            .clickable { rewriteSpendClick(spend) }
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
                text = String.format("%.2f", spend.totalPrice),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SpendingListPreview() {
    val spendingViewModel = SpendingViewModel(FakeData.fakeCategories)
    SpendingListScreen(
        spendingViewModel = spendingViewModel,
        rewriteSpendClick = { _ -> }
    )
}