package com.example.monefy.presentation.screens.rewrite_category

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.monefy.data.mappers.resources.StringToResourceIdMapperImpl
import com.example.monefy.presentation.components.CategoryType
import com.example.monefy.presentation.utils.ColorPicker

@Composable
fun CategoryEditorScreen(
    modifier: Modifier = Modifier,
    viewModel: CategoryEditorScreenViewModel = hiltViewModel<CategoryEditorScreenViewModel>(),
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.isCategoryNameError) {
        viewModel.blinkingCategoryName()
    }

    LaunchedEffect(uiState.isCategoryColorError) {
        viewModel.blinkingColorCategory()
    }

    LaunchedEffect(uiState.messageResName != null) {
        uiState.messageResName?.let {
            snackBarHostState.showSnackbar(
                message = context.getString(StringToResourceIdMapperImpl().map(it))
            )
        }
        viewModel.clearMessage()
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
                .padding(innerPadding)
                .padding(8.dp)
        ) {
            // Название категории
            Text(
                text = "Название категории",
                color = Color.fromColorLong(uiState.textColorCategoryColor),
                modifier = Modifier.padding(4.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                TextField(
                    value = uiState.categoryName,
                    onValueChange = { viewModel.changeCategoryName(it) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    textStyle = TextStyle(fontSize = 20.sp),
                    modifier = Modifier.weight(4f)
                )
                // Кнопка очистки названия
                IconButton(
                    onClick = { viewModel.changeCategoryName("") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Удалить название",
                    )
                }
            }
            // Тип категории
            CategoryType(
                selectedFinanceType = uiState.selectedFinanceType,
                changeSelectedFinanceType = { viewModel.changeSelectedFinanceType(it) }
            )
            // Цвет категории
            Text(
                text = "Цвет категории",
                modifier = Modifier.padding(4.dp)
            )
            Box(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(30.dp)
                    .background(
                        color = uiState.colorCategory?.let { Color.fromColorLong(it) }
                            ?: Color.Transparent,
                        shape = RoundedCornerShape(2.dp)
                    )
                    .border(
                        color = MaterialTheme.colorScheme.onSurface,
                        width = 1.dp,
                        shape = RoundedCornerShape(2.dp)
                    )
                    .clickable { viewModel.changeIsShowColorPicker(true) }
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Принимаем изменения
                Button(
                    onClick = { viewModel.editCategory() },
                    colors = ButtonDefaults.buttonColors(Color.White),
                    modifier = Modifier
                        .width(150.dp)
                        .padding(end = 8.dp)
                ) {
                    Text("Изменить")
                }
                // Кнопка удаления категории
                Button(
                    onClick = { viewModel.deleteCategory() },
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

    // Если пользователь хочет выбрать цвет категории, вызываем ColorPicker
    if (uiState.isShowColorPicker) {
        Dialog(onDismissRequest = { viewModel.changeIsShowColorPicker(false) }) {
            ColorPicker(changeColorCategory = { viewModel.changeColorCategory(it) })
        }
    }
}