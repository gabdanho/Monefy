package com.example.monefy.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.monefy.R
import com.example.monefy.presentation.model.FinanceType
import com.example.monefy.presentation.theme.defaultDimensions

@Composable
fun CategoryType(
    selectedFinanceType: FinanceType,
    changeSelectedFinanceType: (FinanceType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.text_category_type),
        modifier = Modifier.padding(defaultDimensions.verySmall)
    )
    Row(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedFinanceType == FinanceType.EXPENSE,
                onClick = {
                    changeSelectedFinanceType(FinanceType.EXPENSE)
                },
            )
            Text(
                text = stringResource(R.string.text_expenses),
                modifier = Modifier.clickable {
                    changeSelectedFinanceType(FinanceType.EXPENSE)
                }
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedFinanceType == FinanceType.REVENUE,
                onClick = {
                    changeSelectedFinanceType(FinanceType.REVENUE)
                },
            )
            Text(
                text = stringResource(R.string.text_revenues),
                modifier = Modifier.clickable {
                    changeSelectedFinanceType(FinanceType.REVENUE)
                }
            )
        }
    }
}