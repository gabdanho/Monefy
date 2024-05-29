package com.example.monefy.ui.screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.monefy.data.Category
import com.example.monefy.utils.ColorPicker
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RewriteCategoryScreen(
    spendingViewModel: SpendingViewModel,
    endOfScreen: () -> Unit
) {
    val uiState by spendingViewModel.uiState.collectAsState()

    RewriteCategory(
        initialCategory = uiState.categoryToRewrite,
        colorToChange = uiState.colorToChange,
        isColorDialogShow = uiState.isColorDialogShow,
        changeColorDialogShow = spendingViewModel::changeColorDialogShow,
        changeColorToChange = spendingViewModel::changeColorToChange,
        removeColorToChange = spendingViewModel::removeColorToChange,
        rewriteCategory = spendingViewModel::rewriteCategory,
        deleteCategory = spendingViewModel::deleteCategory,
        endOfScreen = endOfScreen
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewriteCategory(
    isColorDialogShow: Boolean,
    colorToChange: Color,
    initialCategory: Category,
    endOfScreen: () -> Unit,
    removeColorToChange: () -> Unit,
    changeColorDialogShow: (Boolean) -> Unit,
    changeColorToChange: (Color) -> Unit,
    rewriteCategory: suspend (Category, Category) -> Boolean,
    deleteCategory: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    val backPressHandled = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    BackHandler(enabled = !backPressHandled.value) {
        backPressHandled.value = true
        scope.launch {
            awaitFrame()
            removeColorToChange()
            onBackPressedDispatcher?.onBackPressed()
            backPressHandled.value = false
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    var categoryName by rememberSaveable { mutableStateOf(initialCategory.name) }

    val colorTextCategoryName = remember { mutableStateOf(Color.Black) }
    var isCategoryNameWrong by rememberSaveable { mutableStateOf(false) }

    var color by remember { mutableStateOf(Color.Transparent) }

    LaunchedEffect(isCategoryNameWrong) {
        for (i in 1..3) {
            colorTextCategoryName.value = Color.Red
            delay(500)
            colorTextCategoryName.value = Color.Black
            delay(500)
        }
        isCategoryNameWrong = false
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
                color = if (!isCategoryNameWrong) Color.Black else colorTextCategoryName.value,
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
            }
            Text(
                text = "Цвет категории",
                color = Color.Black,
                modifier = Modifier.padding(4.dp)
            )
            Box(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(30.dp)
                    .background(
                        color = if (colorToChange == Color.Transparent) Color(initialCategory.color)
                        else colorToChange
                    )
                    .border(color = Color.Black, width = 1.dp, shape = RoundedCornerShape(2.dp))
                    .clickable { changeColorDialogShow(true) }
            )

            if (isColorDialogShow) {
                Dialog(onDismissRequest = { changeColorDialogShow(false) }) {
                    ColorPicker(
                        changeColorCategory = changeColorToChange,
                        changeColorDialogShow = changeColorDialogShow
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            if (categoryName.isEmpty()) categoryName = initialCategory.name
                            if (colorToChange == Color.Transparent) changeColorToChange(Color(initialCategory.color))

                            val newCategory = initialCategory.copy(name = categoryName, color = colorToChange.toArgb())
                            val rewriteCategoryResult = rewriteCategory(initialCategory, newCategory)

                            if (rewriteCategoryResult) {
                                scope.launch {
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    snackbarHostState.showSnackbar("Категория изменена")
                                }
                                removeColorToChange()
                                endOfScreen()
                            }
                            else {
                                scope.launch {
                                    isCategoryNameWrong = true
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    snackbarHostState.showSnackbar("Категория с таким именем существует")
                                }
                            }
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
                        deleteCategory(initialCategory)
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
}

//@Preview(showBackground = true)
//@Composable
//fun RewriteCategoryPreview() {
//    RewriteCategory(
//        initialName = FakeData.fakeCategories.first().name,
//        initialColor = FakeData.fakeCategories.first().color,
//        changeColorDialogShow = { _ -> },
//        changeColorCategory = { _ -> },
//        rewriteCategory = { _, _, _ -> true },
//        removeSelectedCategoryColor = { },
//        endOfScreen = { },
//        deleteCategory = { },
//        categoryColor = Color.Transparent,
//        isColorDialogShow = false
//    )
//}