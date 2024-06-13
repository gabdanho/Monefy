package com.example.monefy.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.toArgb
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
fun AddFinanceScreen(
    financesViewModel: FinancesViewModel,
    context: Context,
    onAddCategoryScreenClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by financesViewModel.uiState.collectAsState()

    AddFinance(
        selectedCategoryId = uiState.selectedCategoryId,
        getAllCategories = financesViewModel::getAllCategories,
        onAddCategoryScreenClick = onAddCategoryScreenClick,
        changeSelectedCategory = financesViewModel::changeSelectedCategory,
        addFinance = financesViewModel::addFinance,
        removeSelectedCategoryId = financesViewModel::removeSelectedCategoryId,
        context = context,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFinance(
    selectedCategoryId: Int,
    getAllCategories: () -> Flow<List<Category>>,
    onAddCategoryScreenClick: () -> Unit,
    changeSelectedCategory: (Int) -> Unit,
    addFinance: suspend (Finance) -> Unit,
    removeSelectedCategoryId: () -> Unit,
    context: Context,
    modifier: Modifier = Modifier
) {
    val categories by getAllCategories().collectAsState(emptyList())
    val addCategory = Category(
        id = -1,
        name = "Добавить категорию (+)",
        color = Color.Transparent.toArgb()
    )

    val scrollState = rememberScrollState()

    var financeName by rememberSaveable { mutableStateOf("") }
    var financePrice by rememberSaveable { mutableStateOf(0.0) }
    var financePriceForTextFieldValue by rememberSaveable { mutableStateOf("") }
    var financeDescription by rememberSaveable { mutableStateOf("") }
    var count by rememberSaveable { mutableStateOf(1) }
    var countForTextFieldValue by rememberSaveable { mutableStateOf("1") }

    var isFinanceNameNotSelected by rememberSaveable { mutableStateOf(false) }
    var isSelectedCategoryNotSelected by rememberSaveable { mutableStateOf(false) }
    val colorTextFinanceName = remember { mutableStateOf(Color.Black) }
    val colorTextSelectedCategory = remember { mutableStateOf(Color.Black) }

    LaunchedEffect(isSelectedCategoryNotSelected) {
        for (i in 1..3) {
            colorTextSelectedCategory.value = Color.Red
            delay(500)
            colorTextSelectedCategory.value = Color.Black
            delay(500)
        }
        isSelectedCategoryNotSelected = false
    }

    LaunchedEffect(isFinanceNameNotSelected) {
        for (i in 1..3) {
            colorTextFinanceName.value = Color.Red
            delay(500)
            colorTextFinanceName.value = Color.Black
            delay(500)
        }
        isFinanceNameNotSelected = false
    }

    var pickedDate by rememberSaveable { mutableStateOf(LocalDate.now()) }
    val dateDialogState = rememberMaterialDialogState()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
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
            Text(
                text = "Название",
                color = if (!isFinanceNameNotSelected) Color.Black else colorTextFinanceName.value,
                modifier = Modifier.padding(4.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                TextField(
                    value = financeName,
                    onValueChange = { financeName = it },
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
                    textStyle = TextStyle(fontSize = 20.sp),
                    modifier = Modifier.weight(4f)
                )
                IconButton(
                    onClick = { financeName = "" },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Удалить название",
                    )
                }
            }
            Text(
                text = "Стоимость / доход",
                modifier = Modifier.padding(4.dp)
            )
            Row {
                TextField(
                    value = financePriceForTextFieldValue,
                    onValueChange = {
                        if (it == "" || it == ".") {
                            financePriceForTextFieldValue = ""
                            financePrice = 0.0
                        }
                        else if (it.length < financePriceForTextFieldValue.length) {
                            financePriceForTextFieldValue = it
                            financePrice = it.toDouble()
                        }
                        else if (it == "00") { }
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
                IconButton(
                    onClick = {
                        financePrice = 0.0
                        financePriceForTextFieldValue = "0"
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Удалить цену",
                    )
                }
            }
            Text(
                text = "Количество",
                modifier = Modifier.padding(4.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
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
                BasicTextField(
                    value = countForTextFieldValue,
                    onValueChange = {
                        if (it == "") {
                            countForTextFieldValue = ""
                            count = 1
                        }
                        else if (it.length < countForTextFieldValue.length) {
                            countForTextFieldValue = it
                            count = it.toInt()
                        }
                        else if (it == "0") { }
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
                            if (focusState.isFocused) {
                                count = 1
                                countForTextFieldValue = ""
                            } else {
                                if (countForTextFieldValue.isEmpty()) {
                                    count = 1
                                    countForTextFieldValue = "1"
                                }
                            }
                        }
                )
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
            Text(
                text = "Категория",
                color = if (!isSelectedCategoryNotSelected) Color.Black else colorTextSelectedCategory.value,
                modifier = Modifier.padding(4.dp)
            )
            LazyHorizontalGrid(
                rows = GridCells.Fixed(2),
                modifier = Modifier
                    .height(maxOf(200.dp))
                    .padding(bottom = 8.dp)
            ) {
                items(categories + addCategory) { category ->
                    CategoryCard(
                        categoryName = category.name,
                        categoryId = category.id,
                        categoryColor = Color(category.color),
                        currentCategoryId = selectedCategoryId,
                        onAddCategoryScreenClick = onAddCategoryScreenClick,
                        changeSelectedCategory = changeSelectedCategory
                    )
                }
            }
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
            Button(
                onClick = {
                    if (financeName.isEmpty() && selectedCategoryId == 0) {
                    scope.launch {
                        isFinanceNameNotSelected = true
                        isSelectedCategoryNotSelected = true
                        snackbarHostState.showSnackbar("Укажите название и категорию траты")
                        }
                    }
                    else if (financeName.isEmpty()) {
                        scope.launch {
                            isFinanceNameNotSelected = true
                            snackbarHostState.showSnackbar("Укажите название траты")
                        }
                    }
                    else if (selectedCategoryId == 0) {
                        scope.launch {
                            isSelectedCategoryNotSelected = true
                            snackbarHostState.showSnackbar("Укажите категорию")
                        }
                    }
                    else {
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                val newFinance = Finance(
                                    categoryId = selectedCategoryId,
                                    name = financeName,
                                    description = financeDescription,
                                    count = count,
                                    price = financePrice,
                                    date = pickedDate
                                )
                                addFinance(newFinance)

                                financeName = ""
                                financePrice = 0.0
                                financePriceForTextFieldValue = "0"
                                count = 1
                                countForTextFieldValue = "1"
                                pickedDate = LocalDate.now()
                                financeDescription = ""
                                removeSelectedCategoryId()

                                snackbarHostState.currentSnackbarData?.dismiss()
                                snackbarHostState.showSnackbar("Запись добавлена")
                            }
                        }
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Добавить")
            }
        }
    }

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

@Composable
fun CategoryCard(
    categoryName: String,
    categoryId: Int,
    categoryColor: Color,
    currentCategoryId: Int,
    changeSelectedCategory: (Int) -> Unit,
    onAddCategoryScreenClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
            .size(150.dp)
            .padding(4.dp)
            .clickable {
                if (categoryId == -1) onAddCategoryScreenClick()
                else changeSelectedCategory(categoryId)
            }
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(10.dp),
                color = if (currentCategoryId == categoryId) Color.Green else Color.Transparent,
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            if (categoryId != -1) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    drawCircle(
                        color = categoryColor,
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
            }
            Text(text = categoryName)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddFinancePreview() {
    AddFinance(
        selectedCategoryId = 0,
        getAllCategories = { flowOf(listOf()) },
        onAddCategoryScreenClick = { /*TODO*/ },
        changeSelectedCategory = { _int -> },
        addFinance = { _ -> },
        removeSelectedCategoryId = { /*TODO*/ },
        context = LocalContext.current
    )
}