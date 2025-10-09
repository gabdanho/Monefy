package com.example.monefy.presentation.screens.create_finance

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.monefy.data.mappers.resources.StringToResourceIdMapperImpl
import com.example.monefy.presentation.components.CircleCategoryColor
import com.example.monefy.presentation.constants.ADD_CATEGORY_ID
import com.example.monefy.presentation.constants.MAX_COUNT
import com.example.monefy.presentation.model.Category
import com.example.monefy.presentation.model.FinanceType
import com.example.monefy.presentation.model.CREATION_CATEGORY
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate

@Composable
fun AddFinanceScreen(
    modifier: Modifier = Modifier,
    viewModel: AddFinanceScreenViewModel = hiltViewModel<AddFinanceScreenViewModel>(),
) {
    val scrollState = rememberScrollState()
    val snackBarHostState = remember { SnackbarHostState() }
    val dateDialogState = rememberMaterialDialogState()
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Показываем пользователю миганием, что не выбрана категория
    LaunchedEffect(uiState.isCategoryNotSelected) {
        viewModel.blinkingSelectedTypeFinance()
    }

    // Показываем пользователю миганием, что не выбрано название
    LaunchedEffect(uiState.isFinanceNameNotFilled) {
        viewModel.blinkingFinanceName()
    }

    // Показываем SnackBar
    LaunchedEffect(uiState.isShowSnackBar) {
        uiState.messageResName?.let {
            snackBarHostState.showSnackbar(
                message = context.getString(StringToResourceIdMapperImpl().map(it))
            )
        }
        viewModel.changeIsShowSnackBar(false)
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
                textColor = uiState.textColorFinanceName,
                onValueChange = { viewModel.onFinanceNameChange(it) },
                modifier = Modifier.fillMaxWidth()
            )
            // Стоимость/доход
            InputParamItem(
                paramName = "Стоимость / доход",
                value = uiState.price.toString(),
                onValueChange = { viewModel.onPriceChange(it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            // Количество
            ItemCounter(
                count = uiState.count,
                minusCount = { viewModel.minusCount() },
                plusCount = { viewModel.plusCount() },
                onValueChange = { viewModel.onCountChange(it) },
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // Категория
            Text(
                text = "Категория",
                color = Color.fromColorLong(uiState.textColorCategory),
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

    if (uiState.isShowDateDialog) {
        // Диалоговое окно с выбором даты
        MaterialDialog(
            dialogState = dateDialogState,
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
}

// Карточка категории
@Composable
private fun CategoryCard(
    categoryName: String,
    categoryId: Int,
    categoryColor: Color,
    selectedCategoryId: Int,
    changeSelectedCategory: (Int) -> Unit,
    onAddCategoryScreenClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
            .size(150.dp)
            .padding(4.dp)
            .clickable {
                // Если id = -1, то это карточка создания категории, иначе просто выбираем категорию
                if (categoryId == ADD_CATEGORY_ID) onAddCategoryScreenClick()
                else changeSelectedCategory(categoryId)
            }
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(10.dp),
                // Белая категория - выбранная категория
                color = if (selectedCategoryId == categoryId) MaterialTheme.colorScheme.onSurface else Color.Transparent,
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            // Рисуем категорию создания
            if (categoryId != ADD_CATEGORY_ID) {
                CircleCategoryColor(
                    colorLong = categoryColor.toColorLong(),
                    radius = 10f,
                    center = 5f,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Text(
                text = categoryName,
                textAlign = TextAlign.Center,
                style = if (categoryId == ADD_CATEGORY_ID) MaterialTheme.typography.displaySmall
                else MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun InputParamItem(
    paramName: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textColor: Long = 0L,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    textFieldColors: TextFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent
    ),
) {
    Column(modifier = modifier) {
        Text(
            text = paramName,
            color = Color.fromColorLong(textColor),
            modifier = Modifier.padding(4.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            TextField(
                value = value,
                onValueChange = { onValueChange(it) },
                singleLine = true,
                colors = textFieldColors,
                keyboardOptions = keyboardOptions,
                textStyle = TextStyle(fontSize = 20.sp),
                modifier = Modifier.weight(4f)
            )
            // Кнопка удаления названия
            IconButton(
                onClick = { onValueChange("") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Удалить значение",
                )
            }
        }
    }
}

@Composable
private fun ItemCounter(
    count: Int,
    minusCount: () -> Unit,
    plusCount: () -> Unit,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "Количество",
        modifier = Modifier.padding(4.dp)
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        // Уменьшить количество на 1 кнопкой
        IconButton(
            onClick = { if (count > 1) minusCount() }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Уменьшить количество"
            )
        }
        // Поле ввода количества
        BasicTextField(
            value = count.toString(),
            onValueChange = {
                // Если пусто, то количество = 1
                if (it == "") {
                    onValueChange("1")
                }
                // Если пользователь удаляет символ
                else if (it.length < count.toString().length) {
                    onValueChange(it)
                }
                // Если 0 - ничего не делаем, т.к. 0 быть не может
                else if (it == "0") {
                    // 52 ngg
                }
                // Проверяем, что только цифры && Количество меньше константы
                else if (it.isDigitsOnly() && it.toInt() < MAX_COUNT) {
                    onValueChange(it)
                }
            },
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                color = Color.White
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(70.dp)
        )
        // Увеличить количество на 1 кнопкой
        IconButton(
            onClick = { plusCount() }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Увеличить количество"
            )
        }
    }
}

@Composable
private fun CategoriesGrid(
    name: String,
    categories: List<Category>,
    selectedCategoryId: Int,
    onAddCategoryScreenClick: () -> Unit,
    changeSelectedCategory: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
        ) {
            Text(text = name)
        }

        LazyHorizontalGrid(
            rows = GridCells.Fixed(2),
            modifier = Modifier
                .height(maxOf(200.dp))
                .padding(bottom = 8.dp)
        ) {
            items(categories) { category ->
                CategoryCard(
                    categoryName = category.name,
                    categoryId = category.id,
                    categoryColor = category.colorLong?.let { Color.fromColorLong(it) }
                        ?: Color.Transparent,
                    selectedCategoryId = selectedCategoryId,
                    onAddCategoryScreenClick = { onAddCategoryScreenClick() },
                    changeSelectedCategory = { changeSelectedCategory(category.id) }
                )
            }
        }
    }
}

@Composable
private fun RegularPayment(
    isRegular: Boolean,
    onValueChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Checkbox(
            checked = isRegular,
            onCheckedChange = { onValueChange() }
        )
        Text(text = "Регулярный платёж")
    }
}

@Composable
private fun CreationCategoryItem(
    onAddCategoryScreenClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
        ) {
            Text(text = "Добавить категорию")
        }
        CategoryCard(
            categoryName = CREATION_CATEGORY.name,
            categoryId = CREATION_CATEGORY.id,
            categoryColor = CREATION_CATEGORY.colorLong?.let { Color.fromColorLong(it) }
                ?: Color.Transparent,
            selectedCategoryId = CREATION_CATEGORY.id,
            onAddCategoryScreenClick = { onAddCategoryScreenClick() },
            changeSelectedCategory = { },
            modifier = Modifier.size(70.dp)
        )
    }
}

@Composable
private fun FinanceDateCreatedPicker(
    pickedDate: LocalDate,
    showDialogState: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "Дата",
            modifier = Modifier.padding(4.dp)
        )
        TextField(
            value = if (pickedDate == LocalDate.now()) {
                "Сегодня"
            } else if (pickedDate == LocalDate.now().minusDays(1)) {
                "Вчера"
            } else {
                pickedDate.toString()
            },
            onValueChange = { },
            readOnly = true,
            trailingIcon = {
                IconButton(
                    // Нажимаем на иконку и вызываем диалоговое окно с выбором даты
                    onClick = { showDialogState() },
                ) {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = "Выбрать дату"
                    )
                }
            },
            modifier = Modifier
                .padding(bottom = 8.dp)
        )
    }
}