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
import com.example.monefy.data.DateConverter
import com.example.monefy.data.Spend
import com.example.monefy.utils.Constants
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun RewriteSpendScreen(
    spendingViewModel: SpendingViewModel,
    endOfScreen: () -> Unit,
    context: Context,
    modifier: Modifier = Modifier
) {
    val uiState by spendingViewModel.uiState.collectAsState()

    RewriteSpend(
        selectedCategoryId = uiState.selectedCategoryId,
        initialSpend = uiState.selectedSpendToChange,
        endOfScreen = endOfScreen,
        getAllCategories = spendingViewModel::getAllCategories,
        changeSelectedCategory = spendingViewModel::changeSelectedCategory,
        rewriteSpend = spendingViewModel::rewriteSpend,
        removeSelectedCategoryId = spendingViewModel::removeSelectedCategoryId,
        deleteSpend = spendingViewModel::deleteSpend,
        context = context,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewriteSpend(
    selectedCategoryId: Int,
    initialSpend: Spend,
    endOfScreen: () -> Unit,
    getAllCategories: () -> Flow<List<Category>>,
    changeSelectedCategory: (Int) -> Unit,
    rewriteSpend: (Spend) -> Unit,
    removeSelectedCategoryId: () -> Unit,
    deleteSpend: (Spend) -> Unit,
    context: Context,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val categories by getAllCategories().collectAsState(emptyList())

    var spendName by rememberSaveable { mutableStateOf(initialSpend.name) }
    var spendPrice by rememberSaveable { mutableStateOf(initialSpend.price) }
    var spendPriceForTextFieldValue by rememberSaveable { mutableStateOf(String.format("%.2f", initialSpend.price)) }
    var spendDescription by rememberSaveable { mutableStateOf(initialSpend.description) }
    var count by rememberSaveable { mutableStateOf(initialSpend.count) }
    var countForTextFieldValue by rememberSaveable { mutableStateOf(initialSpend.count.toString()) }

    var isSpendNameNotSelected by rememberSaveable { mutableStateOf(false) }
    var isSelectedCategoryNotSelected by rememberSaveable { mutableStateOf(false) }
    val colorTextSpendName = remember { mutableStateOf(Color.Black) }
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

    LaunchedEffect(isSpendNameNotSelected) {
        for (i in 1..3) {
            colorTextSpendName.value = Color.Red
            delay(500)
            colorTextSpendName.value = Color.Black
            delay(500)
        }
        isSpendNameNotSelected = false
    }

    var pickedDate by rememberSaveable { mutableStateOf(initialSpend.date) }
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
                text = "Название траты",
                color = if (!isSpendNameNotSelected) Color.Black else colorTextSpendName.value,
                modifier = Modifier.padding(4.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                TextField(
                    value = spendName,
                    onValueChange = { spendName = it },
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
                    textStyle = TextStyle(fontSize = 20.sp),
                    modifier = Modifier.weight(4f)
                )
            }
            Text(
                text = "Цена",
                modifier = Modifier.padding(4.dp)
            )
            Row {
                TextField(
                    value = spendPriceForTextFieldValue,
                    onValueChange = {
                        if (it == "" || it == ".") {
                            spendPriceForTextFieldValue = ""
                            spendPrice = 0.0
                        }
                        else if (it.length < spendPriceForTextFieldValue.length) {
                            spendPriceForTextFieldValue = it
                            spendPrice = it.toDouble()
                        }
                        else if (it == "00") { }
                        else if (it.all { it.isDigit() || it == '.' } && it.count { it == '.' } <= 1 && it.toDouble() < Constants.maxPrice) {
                            spendPriceForTextFieldValue = it
                            spendPrice = it.toDouble()
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
                                if (spendPrice == 0.0) {
                                    spendPrice = 0.0
                                    spendPriceForTextFieldValue = ""
                                }
                            } else {
                                if (spendPriceForTextFieldValue.isEmpty()) {
                                    spendPrice = 0.0
                                    spendPriceForTextFieldValue = "0"
                                }
                            }
                        }
                )
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
                items(categories) { category ->
                    ChangeCategoryCard(
                        category = category,
                        currentCategoryId = selectedCategoryId,
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
                    value = spendDescription,
                    onValueChange = { spendDescription = it },
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
                    modifier = Modifier.weight(4f)
                )
                IconButton(
                    onClick = { spendDescription = "" },
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
                Button(
                    onClick = {
                        if (spendName.isEmpty()) {
                            scope.launch {
                                isSpendNameNotSelected = true
                                snackbarHostState.showSnackbar("Укажите название траты")
                            }
                        }
                        else {
                            val newSpend = initialSpend.copy(
                                name = spendName,
                                categoryId = selectedCategoryId,
                                description = spendDescription,
                                price = spendPrice,
                                date = pickedDate,
                                count = count
                            )
                            rewriteSpend(newSpend)
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
                Button(
                    onClick = {
                        deleteSpend(initialSpend)
                        endOfScreen()
                    },
                    colors = ButtonDefaults.buttonColors(Color.Red),
                    modifier = Modifier.width(150.dp)
                ) {
                    Text("Удалить")
                }
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

//@Preview(showBackground = true)
//@Composable
//fun RewriteSpendPreview() {
//    val _category = Category()
//    val _expense = Expense()
//    val _expense2 = Expense()
//    RewriteSpend(
//        initialSpend = Expense(),
//        rewriteExpense = { _expense, _expense2 -> },
//        categories = FakeData.fakeCategories,
//        changeSelectedCategory = { _ -> },
//        removeCategory = { },
//        deleteSpend = { _expense -> },
//        endOfScreen = { },
//        context = LocalContext.current,
//        selectedCategoryName = "",
//    )
//}