package com.example.monefy.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.example.monefy.data.Category
import com.example.monefy.data.Finance
import com.example.monefy.model.FakeData
import com.example.monefy.utils.Constants
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

@Composable
fun RewriteFinanceScreen(
    financesViewModel: FinancesViewModel,
    endOfScreen: () -> Unit,
    context: Context,
    modifier: Modifier = Modifier
) {
    val uiState by financesViewModel.uiState.collectAsState()

    val categories by financesViewModel.getAllCategories().collectAsState(emptyList())

    RewriteFinance(
        categories = categories,
        selectedCategoryId = uiState.selectedCategoryId,
        initialFinance = uiState.selectedFinanceToChange,
        endOfScreen = endOfScreen,
        changeSelectedCategory = financesViewModel::changeSelectedCategory,
        rewriteFinance = financesViewModel::rewriteFinance,
        removeSelectedCategoryId = financesViewModel::removeSelectedCategoryId,
        deleteFinance = financesViewModel::deleteFinance,
        context = context,
        modifier = modifier
    )
}

// Переписать финанс
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewriteFinance(
    categories: List<Category>,
    selectedCategoryId: Int,
    initialFinance: Finance,
    endOfScreen: () -> Unit,
    changeSelectedCategory: (Int) -> Unit,
    rewriteFinance: suspend (Finance, Finance) -> Unit,
    removeSelectedCategoryId: () -> Unit,
    deleteFinance: suspend (Finance) -> Unit,
    context: Context,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    var financeName by rememberSaveable { mutableStateOf(initialFinance.name) }
    var financePrice by rememberSaveable { mutableStateOf(initialFinance.price) }
    var financePriceForTextFieldValue by rememberSaveable { mutableStateOf(String.format("%.2f", initialFinance.price)) }
    var financeDescription by rememberSaveable { mutableStateOf(initialFinance.description) }
    var count by rememberSaveable { mutableStateOf(initialFinance.count) }
    var countForTextFieldValue by rememberSaveable { mutableStateOf(initialFinance.count.toString()) }

    var isFinanceNameNotSelected by rememberSaveable { mutableStateOf(false) }
    var isSelectedCategoryNotSelected by rememberSaveable { mutableStateOf(false) }
    val colorTextFinanceName = remember { mutableStateOf(Color.Black) }
    val colorTextSelectedCategory = remember { mutableStateOf(Color.Black) }

    // Если категория не выбрана, уведомляем пользователю миганием
    LaunchedEffect(isSelectedCategoryNotSelected) {
        for (i in 1..3) {
            colorTextSelectedCategory.value = Color.Red
            delay(500)
            colorTextSelectedCategory.value = Color.Black
            delay(500)
        }
        isSelectedCategoryNotSelected = false
    }

    // Если название финанса не выбрано, уведомляем пользователю миганием
    LaunchedEffect(isFinanceNameNotSelected) {
        for (i in 1..3) {
            colorTextFinanceName.value = Color.Red
            delay(500)
            colorTextFinanceName.value = Color.Black
            delay(500)
        }
        isFinanceNameNotSelected = false
    }

    var pickedDate by rememberSaveable { mutableStateOf(initialFinance.date) }
    val dateDialogState = rememberMaterialDialogState()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        // Настройка снэкбара
        snackbarHost = { SnackbarHost(snackbarHostState) { data ->
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
            // Название финанса
            Text(
                text = "Название",
                color = if (!isFinanceNameNotSelected) Color.Black else colorTextFinanceName.value,
                modifier = Modifier.padding(4.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                // Поле ввода названия
                TextField(
                    value = financeName,
                    onValueChange = { financeName = it },
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
                    textStyle = TextStyle(fontSize = 20.sp),
                    modifier = Modifier.weight(4f)
                )
            }
            // Цена финанса
            Text(
                text = "Цена",
                modifier = Modifier.padding(4.dp)
            )
            Row {
                TextField(
                    value = financePriceForTextFieldValue,
                    onValueChange = {
                        // Если значение пусто или нажимается первой точка, то значение 0.0
                        if (it == "" || it == ".") {
                            financePriceForTextFieldValue = ""
                            financePrice = 0.0
                        }
                        // Если пользователь удаляет символ
                        else if (it.length < financePriceForTextFieldValue.length) {
                            financePriceForTextFieldValue = it
                            financePrice = it.toDouble()
                        }
                        // Если пользователь пытается ввести после введённого нуля еще один - запрещаем
                        else if (it == "00") { }
                        // Проверяем, что вводится цифра или точка && Точка одна или нет && Проверяем чтобы число не было больше константы
                        else if (it.all { it.isDigit() || it == '.' } && it.count { it == '.' } <= 1 && it.toDouble() < Constants.maxPrice) {
                            financePriceForTextFieldValue = it
                            financePrice = it.toDouble()
                        }
                    },
                    textStyle = LocalTextStyle.current.copy(fontSize = 20.sp),
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(4f)
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                if (financePrice == 0.0) {
                                    financePrice = 0.0
                                    financePriceForTextFieldValue = ""
                                }
                            } else {
                                if (financePriceForTextFieldValue.isEmpty()) {
                                    financePrice = 0.0
                                    financePriceForTextFieldValue = "0"
                                }
                            }
                        }
                )
            }
            // Количество
            Text(
                text = "Количество",
                modifier = Modifier.padding(4.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                // Уменьшить количество на 1 кнопкой
                IconButton(
                    onClick = {
                        if (count > 1) {
                            count--
                            countForTextFieldValue = count.toString()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Уменьшить количество"
                    )
                }
                // Поле ввода количества
                BasicTextField(
                    value = countForTextFieldValue,
                    onValueChange = {
                        // Если пусто, то количество = 1
                        if (it == "") {
                            countForTextFieldValue = ""
                            count = 1
                        }
                        // Если пользователь удаляет символ
                        else if (it.length < countForTextFieldValue.length) {
                            countForTextFieldValue = it
                            count = it.toInt()
                        }
                        // Если 0 - ничего не делаем, т.к. 0 быть не может
                        else if (it == "0") { }
                        // Проверяем, что только цифры && Количество меньше константы
                        else if (it.isDigitsOnly() && it.toInt() < Constants.maxCount) {
                            countForTextFieldValue = it
                            count = it.toInt()
                        }
                    },
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .width(70.dp)
                        .onFocusChanged { focusState ->
                            // Если пользователь нажимает на поле ввода количества, то очищаем поле (при этом минималка будет = 1)
                            if (focusState.isFocused) {
                                count = 1
                                countForTextFieldValue = ""
                            // Если пользователь убирает фокус с поля ввода
                            } else {
                                // Поле пустое, то обязательно количество будет = 1
                                if (countForTextFieldValue.isEmpty()) {
                                    count = 1
                                    countForTextFieldValue = "1"
                                }
                            }
                        }
                )
                // Увеличить количество на 1 кнопкой
                IconButton(
                    onClick = {
                        count++
                        countForTextFieldValue = count.toString()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = "Увеличить количество"
                    )
                }
            }
            // Категория
            Text(
                text = "Категория",
                color = if (!isSelectedCategoryNotSelected) Color.Black else colorTextSelectedCategory.value,
                modifier = Modifier.padding(4.dp)
            )
            // Выводим все существующие категории
            LazyHorizontalGrid(
                rows = GridCells.Fixed(2),
                modifier = Modifier
                    .height(maxOf(200.dp))
                    .padding(bottom = 8.dp)
            ) {
                items(categories) { category ->
                    ChangeCategoryCard(
                        category = category,
                        currentCategoryId = selectedCategoryId,
                        changeSelectedCategory = changeSelectedCategory
                    )
                }
            }
            // Дата
            Text(
                text = "Дата",
                modifier = Modifier.padding(4.dp)
            )
            TextField(
                value = if (pickedDate == LocalDate.now()) {
                    "Сегодня"
                }
                else if (pickedDate == LocalDate.now().minusDays(1)) {
                    "Вчера"
                }
                else {
                    pickedDate.toString()
                },
                onValueChange = { },
                readOnly = true,
                trailingIcon = {
                    IconButton(
                        // Нажимаем на иконку и вызываем диалоговое окно с выбором даты
                        onClick = {
                            dateDialogState.show()
                        },
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
            // Описание
            Text(
                text = "Описание",
                modifier = Modifier.padding(4.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                TextField(
                    value = financeDescription,
                    onValueChange = { financeDescription = it },
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
                    modifier = Modifier.weight(4f)
                )
                // Очистить описание
                IconButton(
                    onClick = { financeDescription = "" },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Удалить описание",
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Добавляем финанс, при этом проверяем необходимые условия (необходимо: название, категория)
                Button(
                    onClick = {
                        // Нет названия
                        if (financeName.isEmpty()) {
                            scope.launch {
                                isFinanceNameNotSelected = true
                                snackbarHostState.showSnackbar("Укажите название траты")
                            }
                        }
                        // Изменяем финанс
                        else {
                            val newFinance = initialFinance.copy(
                                name = financeName,
                                categoryId = selectedCategoryId,
                                description = financeDescription,
                                price = financePrice,
                                date = pickedDate,
                                count = count
                            )
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    rewriteFinance(initialFinance, newFinance)
                                }
                            }
                            scope.launch {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                snackbarHostState.showSnackbar("Запись изменена")
                            }
                            removeSelectedCategoryId()
                            endOfScreen()
                        }
                    },
                    modifier = Modifier
                        .width(150.dp)
                        .padding(end = 8.dp)
                ) {
                    Text("Изменить")
                }
                // Удалить финанс
                Button(
                    onClick = {
                        scope.launch {
                            deleteFinance(initialFinance)
                            endOfScreen()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(Color.Red),
                    modifier = Modifier.width(150.dp)
                ) {
                    Text("Удалить")
                }
            }
        }
    }

    // Диалоговое окно с выбором даты
    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton(text = "ОК") {
                Toast.makeText(
                    context,
                    "Выбрана дата: $pickedDate",
                    Toast.LENGTH_LONG
                ).show()
            }
            negativeButton(text = "ОТМЕНА")
        }
    ) {
        datepicker(
            initialDate = pickedDate,
            title = "Pick a date"
        ) {
            pickedDate = it
        }
    }
}

// Карточки с категориями (для изменения категории финанса)
@Composable
fun ChangeCategoryCard(
    category: Category,
    currentCategoryId: Int,
    changeSelectedCategory: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
            .size(150.dp)
            .padding(4.dp)
            .clickable {
                changeSelectedCategory(category.id)
            }
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(10.dp),
                color = if (currentCategoryId == category.id) Color.Green else Color.Transparent,
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            // Рисуем кружочек с цветом категории
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                drawCircle(
                    color = Color(category.color),
                    radius = 10f,
                    center = Offset(5f, 5f)
                )
                drawCircle(
                    color = Color.Black,
                    radius = 10f,
                    center = Offset(5f, 5f),
                    style = Stroke(width = 1f)
                )
            }
            Text(text = category.name)
        }
    }
}

@Preview
@Composable
fun RewriteFinancePreview() {
    fun fakeRewriteFinance(finance1: Finance, finance2: Finance): Boolean {
        return false
    }

    RewriteFinance(
        selectedCategoryId = 1,
        initialFinance = FakeData.fakeFinances.first(),
        endOfScreen = { },
        categories = FakeData.fakeCategoriesList,
        changeSelectedCategory = { },
        rewriteFinance = ::fakeRewriteFinance,
        removeSelectedCategoryId = { },
        deleteFinance = { },
        context = LocalContext.current
    )
}