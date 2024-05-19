package com.example.monefy.ui.screens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.monefy.model.Expense
import com.example.monefy.model.fake.FakeData

@Composable
fun SpendingListScreen(
    spendings: List<Expense>,
    modifier: Modifier = Modifier
) {
    if (spendings.isNotEmpty()) {
        LazyColumn(modifier = modifier) {
            items(spendings) { spend ->
                SpendingCard(
                    spend = spend,
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
                text = spend.totalPrice.toString(),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SpendingListPreview() {
    SpendingListScreen(
        spendings = FakeData.fakeCategories.first().expenses
    )
}