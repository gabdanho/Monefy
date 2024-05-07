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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.example.monefy.model.Category
import com.example.monefy.utils.Constants
import com.example.monefy.model.Expense
import com.example.monefy.model.fake.FakeData
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate

@Composable
fun AddSpendScreen(
    spendingViewModel: SpendingViewModel,
    context: Context,
    modifier: Modifier = Modifier
) {
    val spendingUiState by spendingViewModel.uiState.collectAsState()
    AddSpend(
        categories = spendingUiState.categories,
        selectedCategoryName = spendingUiState.selectedCategoryName,
        context = context,
        changeSelectedCategory = spendingViewModel::changeSelectedCategory,
        addExpense = spendingViewModel::addExpense,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSpend(
    categories: List<Category>,
    selectedCategoryName: String,
    context: Context,
    changeSelectedCategory: (String) -> Unit,
    addExpense: (Expense) -> Unit,
    modifier: Modifier = Modifier
) {
    var spendName by rememberSaveable { mutableStateOf("") }
    var spendPrice by rememberSaveable { mutableStateOf(0.0) }
    var spendPriceForTextFieldValue by rememberSaveable { mutableStateOf("") }
    var spendDescription by rememberSaveable { mutableStateOf("") }
    var count by rememberSaveable { mutableStateOf(1) }
    var countForTextFieldValue by rememberSaveable { mutableStateOf("1") }

    var pickedDate by rememberSaveable { mutableStateOf(LocalDate.now()) }
    val dateDialogState = rememberMaterialDialogState()

    Column(
        modifier = modifier.padding(8.dp)
    ) {
        Text(
            text = "Название траты",
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
            IconButton(
                onClick = { spendName = "" },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Удалить название",
                )
            }
        }
        Text(
            text = "Цена",
            modifier = Modifier.padding(4.dp)
        )
        Row {
            TextField(
                value = spendPriceForTextFieldValue,
                onValueChange = {
                    if (it == "") {
                        spendPriceForTextFieldValue = ""
                        spendPrice = 0.0
                    }
                    else if (it.length < spendPriceForTextFieldValue.length) {
                        spendPriceForTextFieldValue = it
                        spendPrice = it.toDouble()
                    }
                    else if (it == "0") { }
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
            IconButton(
                onClick = {
                    spendPrice = 0.0
                    spendPriceForTextFieldValue = "0"
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
                    else if (it.isDigitsOnly() && it.toInt() < 10000) {
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
            modifier = Modifier.padding(4.dp)
        )
        LazyHorizontalGrid(
            rows = GridCells.Fixed(2),
            modifier = Modifier
                .height(maxOf(200.dp))
                .padding(bottom = 8.dp)
        ) {
            items(categories) { category ->
                CategoryCard(
                    categoryName = category.name,
                    currentCategoryName = selectedCategoryName,
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
        Button(
            onClick = {
                addExpense(
                    Expense(
                        categoryName = selectedCategoryName,
                        name = spendName,
                        description = spendDescription,
                        count = count,
                        price = spendPrice,
                        date = pickedDate
                    )
                )
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Добавить")
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
    changeSelectedCategory: (String) -> Unit,
    categoryName: String,
    currentCategoryName: String,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
            .size(150.dp)
            .padding(4.dp)
            .clickable {
                changeSelectedCategory(categoryName)
            }
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(10.dp),
                color = if (currentCategoryName == categoryName) Color.Green else Color.Transparent,
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
                    color = Color.Red,
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
            Text(text = categoryName)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddSpendPreview() {
    val _expense = FakeData.fakeCategories[0].expenses[0]
    val _string = ""
    AddSpend(
        addExpense = { _expense -> },
        categories = FakeData.fakeCategories,
        changeSelectedCategory = { _string -> },
        context = LocalContext.current,
        selectedCategoryName = ""
    )
}