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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.monefy.R
import com.example.monefy.presentation.model.Finance
import com.example.monefy.presentation.theme.defaultDimensions
import java.util.Locale

@Composable
fun FinancesScreen(
    categoryId: Int,
    modifier: Modifier = Modifier,
    viewModel: FinancesScreenViewModel = hiltViewModel<FinancesScreenViewModel>(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(categoryId) {
        viewModel.getFinanceByCategoryId(categoryId)
    }

    if (uiState.finances != null && uiState.finances?.isNotEmpty() == true) {
        uiState.finances?.let {
            LazyColumn(modifier = modifier) {
                items(it) { finance ->
                    FinanceCard(
                        finance = finance,
                        navigateToFinanceEditorScreen = {
                            viewModel.navigateToFinanceEditorScreen(
                                finance
                            )
                        },
                        modifier = Modifier.padding(defaultDimensions.medium)
                    )
                }
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

// Карточка финанса
@Composable
private fun FinanceCard(
    finance: Finance,
    navigateToFinanceEditorScreen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = defaultDimensions.categoryCardElevation,
                ambientColor = Color.Black,
                spotColor = Color.Black,
                shape = RoundedCornerShape(defaultDimensions.financeCardCornerShape)
            )
            .clickable { navigateToFinanceEditorScreen() }
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = defaultDimensions.medium)
        ) {
            // Дата финанса
            Text(
                text = finance.date,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(
                    start = defaultDimensions.medium,
                    end = defaultDimensions.medium,
                    bottom = defaultDimensions.veryMedium,
                    top = defaultDimensions.small
                )
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
                        modifier = Modifier.widthIn(max = defaultDimensions.financeMaxWidth)
                    )
                }
            }
            // Выводим прайс
            Text(
                text = String.format(
                    Locale.getDefault(),
                    "%.2f",
                    finance.count.toDouble() * finance.price
                ),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}