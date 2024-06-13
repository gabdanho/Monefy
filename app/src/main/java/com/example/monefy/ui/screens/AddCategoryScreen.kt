package com.example.monefy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.monefy.data.Category
import com.example.monefy.utils.ColorPicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AddCategoryScreen(
    financesViewModel: FinancesViewModel,
    endOfScreen: () -> Unit
) {
    val uiState by financesViewModel.uiState.collectAsState()

    AddCategory(
        currentCategoryColor = financesViewModel.uiState.value.selectedCategoryColor,
        isColorDialogShow = uiState.isColorDialogShow,
        changeColorDialogShow = financesViewModel::changeColorDialogShow,
        changeColorCategory = financesViewModel::changeColorCategory,
        addCategory = financesViewModel::addCategory,
        removeSelectedCategoryColor = financesViewModel::removeSelectedCategoryColor,
        endOfScreen = endOfScreen
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategory(
    currentCategoryColor: Color,
    isColorDialogShow: Boolean,
    changeColorDialogShow: (Boolean) -> Unit,
    changeColorCategory: (Color) -> Unit,
    addCategory: suspend (Category) -> Boolean,
    removeSelectedCategoryColor: () -> Unit,
    endOfScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var categoryName by rememberSaveable { mutableStateOf("") }
    var selectedType by rememberSaveable { mutableStateOf("Расходы") }
    var selectType by rememberSaveable { mutableStateOf(true) }

    val colorTextCategoryName = remember { mutableStateOf(Color.Black) }
    var isCategoryNameNotSelected by rememberSaveable { mutableStateOf(false) }
    val colorTextCategoryColor = remember { mutableStateOf(Color.Black) }
    var isColorCategoryNotSelected by rememberSaveable { mutableStateOf(false) }
    val colorTextCategoryType = remember { mutableStateOf(Color.Black) }
    var isTypeCategoryNotSelected by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(isCategoryNameNotSelected) {
        for (i in 1..3) {
            colorTextCategoryName.value = Color.Red
            delay(500)
            colorTextCategoryName.value = Color.Black
            delay(500)
        }
        isCategoryNameNotSelected = false
    }

    LaunchedEffect(isColorCategoryNotSelected) {
        for (i in 1..3) {
            colorTextCategoryColor.value = Color.Red
            delay(500)
            colorTextCategoryColor.value = Color.Black
            delay(500)
        }
        isColorCategoryNotSelected = false
    }

    LaunchedEffect(isTypeCategoryNotSelected) {
        for (i in 1..3) {
            colorTextCategoryType.value = Color.Red
            delay(500)
            colorTextCategoryType.value = Color.Black
            delay(500)
        }
        isTypeCategoryNotSelected = false
    }

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
        ) {
            Text(
                text = "Название категории",
                color = if (!isCategoryNameNotSelected) Color.Black else colorTextCategoryName.value,
                modifier = Modifier.padding(4.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                TextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
                    textStyle = TextStyle(fontSize = 20.sp),
                    modifier = Modifier.weight(4f)
                )
                IconButton(
                    onClick = { categoryName = "" },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Удалить название",
                    )
                }
            }
            Text(
                text = "Тип категории",
                color = if (!isTypeCategoryNotSelected) Color.Black else colorTextCategoryType.value,
                modifier = Modifier.padding(4.dp)
            )
            Row {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectType,
                        onClick = {
                            selectedType = "Расходы"
                            selectType = true
                        },
                    )
                    Text(
                        text = "Расходы",
                        modifier = Modifier.clickable {
                            selectedType = "Расходы"
                            selectType = true
                        }
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = !selectType,
                        onClick = {
                            selectedType = "Доходы"
                            selectType = false
                        },
                    )
                    Text(
                        text = "Доходы",
                        modifier = Modifier.clickable {
                            selectedType = "Доходы"
                            selectType = false
                        }
                    )
                }
            }
            Text(
                text = "Цвет категории",
                color = if (!isColorCategoryNotSelected) Color.Black else colorTextCategoryColor.value,
                modifier = Modifier.padding(4.dp)
            )
            Box(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(30.dp)
                    .background(color = currentCategoryColor)
                    .border(color = Color.Black, width = 1.dp, shape = RoundedCornerShape(2.dp))
                    .clickable { changeColorDialogShow(true) }
            )

            if (isColorDialogShow) {
                Dialog(onDismissRequest = { changeColorDialogShow(false) }) {
                    ColorPicker(
                        changeColorCategory = changeColorCategory,
                        changeColorDialogShow = changeColorDialogShow
                    )
                }
            }
            Button(
                onClick = {
                    scope.launch {
                        if (categoryName.isEmpty() && currentCategoryColor == Color.Transparent && selectedType == "") {
                            scope.launch {
                                isCategoryNameNotSelected = true
                                isColorCategoryNotSelected = true
                                isTypeCategoryNotSelected = true
                                snackbarHostState.showSnackbar("Укажите название, цвет категории и тип категории")
                            }
                        }
                        else if (categoryName.isEmpty() && currentCategoryColor == Color.Transparent) {
                            scope.launch {
                                isCategoryNameNotSelected = true
                                isColorCategoryNotSelected = true
                                snackbarHostState.showSnackbar("Укажите название и цвет категории")
                            }
                        }
                        else if (selectedType == "" && currentCategoryColor == Color.Transparent) {
                            scope.launch {
                                isColorCategoryNotSelected = true
                                isTypeCategoryNotSelected = true
                                snackbarHostState.showSnackbar("Укажите тип и цвет категории")
                            }
                        }
                        else if (selectedType == "" && categoryName.isEmpty()) {
                            scope.launch {
                                isCategoryNameNotSelected = true
                                isTypeCategoryNotSelected = true
                                snackbarHostState.showSnackbar("Укажите тип и название категории")
                            }
                        }
                        else if (categoryName.isEmpty()) {
                            scope.launch {
                                isCategoryNameNotSelected = true
                                snackbarHostState.showSnackbar("Укажите название категории")
                            }
                        }
                        else if (currentCategoryColor == Color.Transparent) {
                            scope.launch {
                                isColorCategoryNotSelected = true
                                snackbarHostState.showSnackbar("Укажите цвет категории")
                            }
                        }
                        else if (selectedType == "") {
                            scope.launch {
                                isTypeCategoryNotSelected = true
                                snackbarHostState.showSnackbar("Укажите тип категории")
                            }
                        }
                        else {
                            val newCategory = Category(name = categoryName, color = currentCategoryColor.toArgb(), type = selectedType)
                            val addCategoryResult = addCategory(newCategory)
                            if (addCategoryResult) {
                                scope.launch {
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    snackbarHostState.showSnackbar("Категория создана")
                                }
                                removeSelectedCategoryColor()
                                categoryName = ""
                                endOfScreen()
                            }
                            else {
                                scope.launch {
                                    isCategoryNameNotSelected = true
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    snackbarHostState.showSnackbar("Категория с таким именем уже существует")
                                }
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
}

@Preview(showBackground = true)
@Composable
fun AddCategoryPreview() {
    AddCategory(
        currentCategoryColor = Color.Transparent,
        isColorDialogShow = false,
        changeColorDialogShow = { _boolean ->  },
        changeColorCategory = { _color ->  },
        addCategory = { _category -> false },
        removeSelectedCategoryColor = { },
        endOfScreen = { })
}