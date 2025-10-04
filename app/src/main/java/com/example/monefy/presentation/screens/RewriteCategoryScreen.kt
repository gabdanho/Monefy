package com.example.monefy.presentation.screens

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
import androidx.compose.material3.MaterialTheme
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
import com.example.monefy.data.local.entity.Category
import com.example.monefy.presentation.utils.ColorPicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun RewriteCategoryScreen(
    financesViewModel: FinancesViewModel,
    endOfScreen: () -> Unit
) {
    val uiState by financesViewModel.uiState.collectAsState()

    RewriteCategory(
        initialCategory = uiState.categoryToRewrite,
        colorToChange = uiState.colorToChange,
        isColorDialogShow = uiState.isColorDialogShow,
        changeColorDialogShow = financesViewModel::changeColorDialogShow,
        changeColorToChange = financesViewModel::changeColorToChange,
        removeColorToChange = financesViewModel::removeColorToChange,
        rewriteCategory = financesViewModel::rewriteCategory,
        deleteCategory = financesViewModel::deleteCategory,
        endOfScreen = endOfScreen
    )
}

// Переписать категорию
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
    deleteCategory: suspend (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    val backPressHandled = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    // При нажатии назад, обнуляем параметры
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

    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onErrorColor = MaterialTheme.colorScheme.onError

    // Если не выбрано название категория, то уведомляем пользователя миганием цветом
    LaunchedEffect(isCategoryNameWrong) {
        for (i in 1..3) {
            colorTextCategoryName.value = onErrorColor
            delay(500)
            colorTextCategoryName.value = onSurfaceColor
            delay(500)
        }
        isCategoryNameWrong = false
    }

    Scaffold(
        // Создаём снэкбар
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
                color = if (!isCategoryNameWrong) onSurfaceColor else colorTextCategoryName.value,
                modifier = Modifier.padding(4.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                // Поле ввода названия
                TextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
                    textStyle = TextStyle(fontSize = 20.sp),
                    modifier = Modifier.weight(4f)
                )
            }
            // Цвет категории
            Text(
                text = "Цвет категории",
                color = onSurfaceColor,
                modifier = Modifier.padding(4.dp)
            )
            Box(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(30.dp)
                    .background(
                        color = if (colorToChange == Color.Transparent) Color(initialCategory.colorLong)
                        else colorToChange,
                        shape = RoundedCornerShape(2.dp)
                    )
                    .border(color = onSurfaceColor, width = 1.dp, shape = RoundedCornerShape(2.dp))
                    .clickable { changeColorDialogShow(true) }
            )

            // Вызываем, если пользователю нужно выбрать цвет
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
                // Принимаем изменения
                Button(
                    onClick = {
                        scope.launch {
                            val newCategory = initialCategory.copy(
                                name = if (categoryName.isEmpty()) initialCategory.name else categoryName,
                                colorLong = if (colorToChange.toArgb() == 0) initialCategory.colorLong else colorToChange.toArgb()
                            )
                            val rewriteCategoryResult = rewriteCategory(initialCategory, newCategory)

                            // Проверяем, можно создать категорию (не существует ли категория с таким названием)
                            if (rewriteCategoryResult) {
                                scope.launch {
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    snackbarHostState.showSnackbar("Категория изменена")
                                }
                                removeColorToChange()
                                endOfScreen()
                            }
                            // Если существует категория с таким названием, говорим пользователю об этом
                            else {
                                scope.launch {
                                    isCategoryNameWrong = true
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    snackbarHostState.showSnackbar("Категория с таким именем существует")
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(Color.White),
                    modifier = Modifier
                        .width(150.dp)
                        .padding(end = 8.dp)
                ) {
                    Text("Изменить")
                }
                // Кнопка удаления категории
                Button(
                    onClick = {
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                deleteCategory(initialCategory)
                            }
                            withContext(Dispatchers.Main) {
                                endOfScreen()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onError),
                    modifier = Modifier.width(150.dp)
                ) {
                   Text(
                       text = "Удалить",
                       color = MaterialTheme.colorScheme.onSurface
                   )
                }
            }
        }
    }
}