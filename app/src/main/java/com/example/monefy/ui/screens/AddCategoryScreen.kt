package com.example.monefy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.monefy.model.Category
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AddCategoryScreen(
    spendingViewModel: SpendingViewModel
) {
    val spendingUiState by spendingViewModel.uiState.collectAsState()
    AddCategory(
        categoryColor = spendingUiState.selectedColorCategory,
        isColorDialogShow = spendingUiState.isColorDialogShow,
        addCategory = spendingViewModel::addNewCategory,
        changeColorDialogShow = spendingViewModel::changeColorDialogShow,
        changeColorCategory = spendingViewModel::changeColorCategory,
        removeSelectedCategoryColor = spendingViewModel::removeSelectedCategoryColor
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategory(
    isColorDialogShow: Boolean,
    categoryColor: Color,
    addCategory: (Category) -> Boolean,
    changeColorDialogShow: (Boolean) -> Unit,
    changeColorCategory: (Color) -> Unit,
    removeSelectedCategoryColor: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var categoryName by rememberSaveable { mutableStateOf("") }

    val colorTextCategoryName = remember { mutableStateOf(Color.Black) }
    var isCategoryNameNotSelected by rememberSaveable { mutableStateOf(false) }
    val colorTextCategoryColor = remember { mutableStateOf(Color.Black) }
    var isColorCategoryNotSelected by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(isCategoryNameNotSelected) {
        for (i in 1..5) {
            colorTextCategoryName.value = Color.Red
            delay(500)
            colorTextCategoryName.value = Color.Black
            delay(500)
        }
        isCategoryNameNotSelected = false
    }

    LaunchedEffect(isColorCategoryNotSelected) {
        for (i in 1..5) {
            colorTextCategoryColor.value = Color.Red
            delay(500)
            colorTextCategoryColor.value = Color.Black
            delay(500)
        }
        isColorCategoryNotSelected = false
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
                text = "Цвет категории",
                color = if (!isColorCategoryNotSelected) Color.Black else colorTextCategoryName.value,
                modifier = Modifier.padding(4.dp)
            )
            Box(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(30.dp)
                    .background(color = categoryColor)
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
                    if (categoryName.isEmpty() && categoryColor == Color.Transparent) {
                        scope.launch {
                            isCategoryNameNotSelected = true
                            isColorCategoryNotSelected = true
                            snackbarHostState.showSnackbar("Укажите название и цвет категории")
                        }
                    }
                    else if (categoryName.isEmpty()) {
                        scope.launch {
                            isCategoryNameNotSelected = true
                            snackbarHostState.showSnackbar("Укажите название категории")
                        }
                    }
                    else if (categoryColor == Color.Transparent) {
                        scope.launch {
                            isColorCategoryNotSelected = true
                            snackbarHostState.showSnackbar("Укажите цвет категории")
                        }
                    }
                    else {
                        val newCategory = Category(name = categoryName, color = categoryColor)
                        if (addCategory(newCategory)) {
                            scope.launch {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                snackbarHostState.showSnackbar("Категория создана")
                            }
                            removeSelectedCategoryColor()
                            categoryName = ""
                        }
                        else {
                            scope.launch {
                                isCategoryNameNotSelected = true
                                snackbarHostState.currentSnackbarData?.dismiss()
                                snackbarHostState.showSnackbar("Категория с таким именем уже существует")
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

@Composable
fun ColorPicker(
    changeColorCategory: (Color) -> Unit,
    changeColorDialogShow: (Boolean) -> Unit
) {
    val colorController = rememberColorPickerController()

    Card {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(all = 30.dp)
                .size(width = 300.dp, height = 450.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp)
                    .background(
                        color = colorController.selectedColor.value.copy(alpha = 1f),
                        shape = RoundedCornerShape(4.dp)
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

            }
            HsvColorPicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(10.dp),
                controller = colorController
            )
            BrightnessSlider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .height(35.dp),
                controller = colorController,
                initialColor = Color.White
            )
            Button(
                onClick = {
                    changeColorCategory(colorController.selectedColor.value.copy(alpha = 1f))
                    changeColorDialogShow(false)
                }
            ) {
                Text("Выбрать")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddCategoryPreview() {
    AddCategory(
        changeColorDialogShow = { _boolean -> },
        changeColorCategory = { _color -> },
        addCategory = { _category -> true},
        removeSelectedCategoryColor = { },
        categoryColor = Color.Transparent,
        isColorDialogShow = false
    )
}

@Preview
@Composable
fun ColorPickerPreview() {
    ColorPicker(
        changeColorCategory = { _color -> },
        changeColorDialogShow = { _boolean -> }
    )
}