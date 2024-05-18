package com.example.monefy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.monefy.R

@Composable
fun BottomMenuBar(
    onPieChartClick: () -> Unit = { },
    onSpendingListClick: () -> Unit = { },
    onAddButtonClick: () -> Unit = { },
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier.fillMaxWidth()
    ) {
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = "Menu"
            )
        }
        IconButton(onClick = onAddButtonClick) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Menu"
            )
        }
        IconButton(onClick = onPieChartClick) {
            Icon(
                painter = painterResource(R.drawable.chart),
                contentDescription = "Донат с расходами",
                modifier = Modifier
                    .clip(CircleShape)
                    .size(40.dp)
            )
        }
        IconButton(onClick = onSpendingListClick) {
            Icon(
                painter = painterResource(R.drawable.list),
                contentDescription = "Список расходов"
            )
        }
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Filled.ShoppingCart,
                contentDescription = "Menu"
            )
        }
    }
}