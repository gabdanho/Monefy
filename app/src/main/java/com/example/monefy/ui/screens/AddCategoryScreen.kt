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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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

// Создать категорию
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

    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val errorColor = MaterialTheme.colorScheme.onError

    // Показываем пользователю миганием, что не выбрана категория
    LaunchedEffect(isCategoryNameNotSelected) {
        for (i in 1..3) {
            colorTextCategoryName.value = errorColor
            delay(500)
            colorTextCategoryName.value = onSurfaceColor
            delay(500)
        }
        isCategoryNameNotSelected = false
    }

    // Показываем пользователю миганием, что не выбран цвет категории
    LaunchedEffect(isColorCategoryNotSelected) {
        for (i in 1..3) {
            colorTextCategoryColor.value = errorColor
            delay(500)
            colorTextCategoryColor.value = onSurfaceColor
            delay(500)
        }
        isColorCategoryNotSelected = false
    }

    // Показываем пользователю миганием, что не выбран тип категории
    LaunchedEffect(isTypeCategoryNotSelected) {
        for (i in 1..3) {
            colorTextCategoryType.value = errorColor
            delay(500)
            colorTextCategoryType.value = onSurfaceColor
            delay(500)
        }
        isTypeCategoryNotSelected = false
    }

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
        ) {
            // Название категории
            Text(
                text = "Название категории",
                color = if (!isCategoryNameNotSelected) onSurfaceColor else colorTextCategoryName.value,
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
                // Кнопка очистки названия
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
            // Тип категории
            Text(
                text = "Тип категории",
                color = if (!isTypeCategoryNotSelected) onSurfaceColor else colorTextCategoryType.value,
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
            // Цвет категории
            Text(
                text = "Цвет категории",
                color = if (!isColorCategoryNotSelected) onSurfaceColor else colorTextCategoryColor.value,
                modifier = Modifier.padding(4.dp)
            )
            Box(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(30.dp)
                    .background(color = currentCategoryColor, shape = RoundedCornerShape(2.dp))
                    .border(color = MaterialTheme.colorScheme.onSurface, width = 1.dp, shape = RoundedCornerShape(2.dp))
                    .clickable { changeColorDialogShow(true) }
            )

            // Если пользователь хочет выбрать цвет категории, вызываем ColorPicker
            if (isColorDialogShow) {
                Dialog(onDismissRequest = { changeColorDialogShow(false) }) {
                    ColorPicker(
                        changeColorCategory = changeColorCategory,
                        changeColorDialogShow = changeColorDialogShow
                    )
                }
            }
            // Создаём категорию, проверяя при этом необходимые условия для этого (обязательные параметры: название, цвет, тип)
            Button(
                onClick = {
                    scope.launch {
                        // Нет названия, цвета, типа
                        if (categoryName.isEmpty() && currentCategoryColor == Color.Transparent && selectedType == "") {
                            scope.launch {
                                isCategoryNameNotSelected = true
                                isColorCategoryNotSelected = true
                                isTypeCategoryNotSelected = true
                                snackbarHostState.showSnackbar("Укажите название, цвет категории и тип категории")
                            }
                        }
                        // Нет названия и цвета
                        else if (categoryName.isEmpty() && currentCategoryColor == Color.Transparent) {
                            scope.launch {
                                isCategoryNameNotSelected = true
                                isColorCategoryNotSelected = true
                                snackbarHostState.showSnackbar("Укажите название и цвет категории")
                            }
                        }
                        // Нет типа и цвета
                        else if (selectedType == "" && currentCategoryColor == Color.Transparent) {
                            scope.launch {
                                isColorCategoryNotSelected = true
                                isTypeCategoryNotSelected = true
                                snackbarHostState.showSnackbar("Укажите тип и цвет категории")
                            }
                        }
                        // Нет типа и названия
                        else if (selectedType == "" && categoryName.isEmpty()) {
                            scope.launch {
                                isCategoryNameNotSelected = true
                                isTypeCategoryNotSelected = true
                                snackbarHostState.showSnackbar("Укажите тип и название категории")
                            }
                        }
                        // Нет названия
                        else if (categoryName.isEmpty()) {
                            scope.launch {
                                isCategoryNameNotSelected = true
                                snackbarHostState.showSnackbar("Укажите название категории")
                            }
                        }
                        // Нет цвета
                        else if (currentCategoryColor == Color.Transparent) {
                            scope.launch {
                                isColorCategoryNotSelected = true
                                snackbarHostState.showSnackbar("Укажите цвет категории")
                            }
                        }
                        // Нет типа
                        else if (selectedType == "") {
                            scope.launch {
                                isTypeCategoryNotSelected = true
                                snackbarHostState.showSnackbar("Укажите тип категории")
                            }
                        }
                        // Если условия соблюдены, добавляем категорию в БД. При этом проверив еще одни условия
                        else {
                            val newCategory = Category(name = categoryName, color = currentCategoryColor.toArgb(), type = selectedType)
                            val addCategoryResult = addCategory(newCategory)
                            // Если категории с таким названием не существует - добавляем. Возвращаем пользователя к предыдущему экрану
                            if (addCategoryResult) {
                                scope.launch {
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    snackbarHostState.showSnackbar("Категория создана")
                                }
                                removeSelectedCategoryColor()
                                categoryName = ""
                                endOfScreen()
                            }
                            // Показываем через снэкбар, что категорию невозможно создать, т.к. категория с таким названием существует
                            else {
                                scope.launch {
                                    isCategoryNameNotSelected = true
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    snackbarHostState.showSnackbar("Категория с таким названием уже существует")
                                }
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(Color.White),
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