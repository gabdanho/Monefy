package com.example.monefy.presentation.screens.finance_editor

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.monefy.R
import com.example.monefy.presentation.mappers.resources.StringToResourceIdMapperImpl
import com.example.monefy.presentation.components.CategoriesGrid
import com.example.monefy.presentation.components.CreationCategoryItem
import com.example.monefy.presentation.components.FinanceDateCreatedPicker
import com.example.monefy.presentation.components.InputParamItem
import com.example.monefy.presentation.components.ItemCounter
import com.example.monefy.presentation.components.RegularPayment
import com.example.monefy.presentation.model.Finance
import com.example.monefy.presentation.model.FinanceType
import com.example.monefy.presentation.theme.blackColor
import com.example.monefy.presentation.theme.defaultDimensions
import com.example.monefy.presentation.theme.whiteColor
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState

/**
 * Экран редактирования финансовой операции.
 *
 * @param finance Финансовая запись, которую необходимо отредактировать.
 * @param modifier Модификатор.
 * @param viewModel ViewModel.
 */
@Composable
fun FinanceEditorScreen(
    finance: Finance,
    modifier: Modifier = Modifier,
    viewModel: FinanceEditorScreenViewModel = hiltViewModel<FinanceEditorScreenViewModel>(),
) {
    val scrollState = rememberScrollState()
    val snackBarHostState = remember { SnackbarHostState() }
    val dateDialogState = rememberMaterialDialogState()
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(
            LifecycleEventObserver{ _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    viewModel.getCategories()
                }
            }
        )
    }

    LaunchedEffect(finance) {
        viewModel.initFinance(finance)
    }

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
                    containerColor = whiteColor,
                    contentColor = blackColor
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(defaultDimensions.small)
                .padding(innerPadding)
                .verticalScroll(state = scrollState)
        ) {
            // Название
            InputParamItem(
                paramName = stringResource(R.string.input_finance_name),
                value = uiState.financeName,
                textColor = Color(uiState.textColorFinanceName),
                onValueChange = { viewModel.onFinanceNameChange(it) },
                modifier = Modifier.fillMaxWidth()
            )
            // Стоимость/доход
            InputParamItem(
                paramName = stringResource(R.string.input_finance_price),
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
                modifier = Modifier.padding(bottom = defaultDimensions.small)
            )
            // Категория
            Text(
                text = stringResource(R.string.text_finance_category),
                color = Color(uiState.textColorCategory),
                modifier = Modifier.padding(defaultDimensions.verySmall)
            )

            if (uiState.categories.isNotEmpty()) {
                val revenueCategories = uiState.categories.filter { it.type == FinanceType.REVENUE }
                val spendCategories = uiState.categories.filter { it.type == FinanceType.EXPENSE }

                // Выводим категории доходов, если они существуют
                if (revenueCategories.isNotEmpty()) {
                    // Категории доходов
                    CategoriesGrid(
                        name = stringResource(R.string.text_revenues),
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
                        name = stringResource(R.string.text_expenses),
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
                paramName = stringResource(R.string.input_finance_description),
                value = uiState.financeDescription,
                onValueChange = { viewModel.onDescriptionChange(it) },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { viewModel.editFinance() },
                    colors = ButtonDefaults.buttonColors(Color.White),
                    modifier = Modifier
                        .padding(end = defaultDimensions.small)
                ) {
                    Text(text = stringResource(R.string.button_change))
                }
                // Удалить финанс
                Button(
                    onClick = { viewModel.deleteFinance() },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onError)
                ) {
                    Text(
                        text = stringResource(R.string.button_delete),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }

    // Диалоговое окно с выбором даты
    MaterialDialog(
        dialogState = dateDialogState,
        onCloseRequest = { viewModel.changeIsShowDateDialog(false) },
        buttons = {
            positiveButton(text = stringResource(R.string.button_ok)) {
                Toast.makeText(
                    context,
                    context.getString(R.string.text_selected_date, uiState.pickedDate),
                    Toast.LENGTH_LONG
                ).show()
                viewModel.changeIsShowDateDialog(false)
            }
            negativeButton(text = stringResource(R.string.button_cancel)) {
                viewModel.changeIsShowDateDialog(false)
            }
        }
    ) {
        datepicker(
            initialDate = uiState.pickedDate,
            title = stringResource(R.string.text_pick_a_date)
        ) {
            viewModel.onPickedDateChange(it)
        }
    }
}