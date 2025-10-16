package com.example.monefy.presentation.screens.finance_creator

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.monefy.presentation.mappers.resources.StringToResourceIdMapperImpl
import com.example.monefy.presentation.components.CategoriesGrid
import com.example.monefy.presentation.components.CreationCategoryItem
import com.example.monefy.presentation.components.FinanceDateCreatedPicker
import com.example.monefy.presentation.components.InputParamItem
import com.example.monefy.presentation.components.ItemCounter
import com.example.monefy.presentation.components.RegularPayment
import com.example.monefy.presentation.model.FinanceType
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState

@Composable
fun FinanceCreatorScreen(
    modifier: Modifier = Modifier,
    viewModel: FinanceCreatorScreenViewModel = hiltViewModel<FinanceCreatorScreenViewModel>(),
) {
    val scrollState = rememberScrollState()
    val snackBarHostState = remember { SnackbarHostState() }
    val dateDialogState = rememberMaterialDialogState()
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(
        uiState.isCategoryNotSelected,
        uiState.isFinanceNameNotFilled,
        uiState.isPriceEqualsZero
    ) {
        if (uiState.isCategoryNotSelected) viewModel.blinkingSelectedTypeFinance()
        if (uiState.isFinanceNameNotFilled) viewModel.blinkingFinanceName()
        if (uiState.isPriceEqualsZero) viewModel.blinkingFinancePrice()
    }

    LaunchedEffect(uiState.isShowDateDialog) {
        if (uiState.isShowDateDialog) {
            dateDialogState.show()
        } else {
            dateDialogState.hide()
        }
    }

    // Показываем SnackBar
    LaunchedEffect(uiState.messageResName != null) {
        uiState.messageResName?.let {
            snackBarHostState.showSnackbar(
                message = context.getString(StringToResourceIdMapperImpl().map(it))
            )
        }
        viewModel.clearMessage()
    }

    Scaffold(
        // Настройка снэкбара
        snackbarHost = {
            SnackbarHost(snackBarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(8.dp)
                .padding(innerPadding)
                .verticalScroll(state = scrollState)
        ) {
            // Название
            InputParamItem(
                paramName = "Название",
                value = uiState.financeName,
                textColor = Color(uiState.textColorFinanceName),
                onValueChange = { viewModel.onFinanceNameChange(it) },
                modifier = Modifier.fillMaxWidth()
            )
            // Стоимость/доход
            InputParamItem(
                paramName = "Стоимость / доход",
                value = uiState.price,
                textColor = Color(uiState.textColorFinancePrice),
                onValueChange = { viewModel.onPriceChange(it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            // Количество
            ItemCounter(
                count = uiState.count.toInt(),
                minusCount = { viewModel.minusCount() },
                plusCount = { viewModel.plusCount() },
                onValueChange = { viewModel.onCountChange(it) },
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // Категория
            Text(
                text = "Категория",
                color = Color(uiState.textColorCategory),
                modifier = Modifier.padding(4.dp)
            )

            if (uiState.categories.isNotEmpty()) {
                val revenueCategories = uiState.categories.filter { it.type == FinanceType.REVENUE }
                val spendCategories = uiState.categories.filter { it.type == FinanceType.EXPENSE }

                // Выводим категории доходов, если они существуют
                if (revenueCategories.isNotEmpty()) {
                    // Категории доходов
                    CategoriesGrid(
                        name = "Доходы",
                        categories = revenueCategories,
                        selectedCategoryId = uiState.selectedCategoryId,
                        onAddCategoryScreenClick = { },
                        changeSelectedCategory = { viewModel.changeSelectedCategory(it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Выводим категории расходов, если они существуют
                if (spendCategories.isNotEmpty()) {
                    // Категории расходов
                    CategoriesGrid(
                        name = "Расходы",
                        categories = spendCategories,
                        selectedCategoryId = uiState.selectedCategoryId,
                        onAddCategoryScreenClick = { },
                        changeSelectedCategory = { viewModel.changeSelectedCategory(it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Выводим категорию добавления
            CreationCategoryItem(
                onAddCategoryScreenClick = { viewModel.onAddCategoryScreenClick() },
                modifier = Modifier.fillMaxWidth()
            )

            // Регулярный платёж
            RegularPayment(
                isRegular = uiState.isRegular,
                onValueChange = { viewModel.onChangeRegular() },
                modifier = Modifier.fillMaxWidth()
            )

            // Дата
            FinanceDateCreatedPicker(
                pickedDate = uiState.pickedDate,
                showDialogState = { viewModel.changeIsShowDateDialog(true) },
                modifier = Modifier.fillMaxWidth()
            )
            // Описание
            InputParamItem(
                paramName = "Описание",
                value = uiState.financeDescription,
                onValueChange = { viewModel.onDescriptionChange(it) },
                modifier = Modifier.fillMaxWidth()
            )
            // Добавляем финанс
            Button(
                onClick = { viewModel.createFinance() },
                colors = ButtonDefaults.buttonColors(Color.White),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Добавить")
            }
        }
    }

    // Диалоговое окно с выбором даты
    MaterialDialog(
        dialogState = dateDialogState,
        onCloseRequest = { viewModel.changeIsShowDateDialog(false) },
        buttons = {
            positiveButton(text = "ОК") {
                Toast.makeText(
                    context,
                    "Выбрана дата: ${uiState.pickedDate}",
                    Toast.LENGTH_LONG
                ).show()
                viewModel.changeIsShowDateDialog(false)
            }
            negativeButton(text = "ОТМЕНА") {
                viewModel.changeIsShowDateDialog(false)
            }
        }
    ) {
        datepicker(
            initialDate = uiState.pickedDate,
            title = "Pick a date"
        ) {
            viewModel.onPickedDateChange(it)
        }
    }
}