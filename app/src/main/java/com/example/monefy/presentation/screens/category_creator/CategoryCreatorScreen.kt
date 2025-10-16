package com.example.monefy.presentation.screens.category_creator

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.monefy.R
import com.example.monefy.presentation.mappers.resources.StringToResourceIdMapperImpl
import com.example.monefy.presentation.components.CategoryType
import com.example.monefy.presentation.components.ColorPicker
import com.example.monefy.presentation.theme.blackColor
import com.example.monefy.presentation.theme.defaultDimensions
import com.example.monefy.presentation.theme.whiteColor

@Composable
fun CategoryCreatorScreen(
    modifier: Modifier = Modifier,
    viewModel: CategoryCreatorScreenViewModel = hiltViewModel<CategoryCreatorScreenViewModel>(),
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.isCategoryNameError, uiState.isCategoryColorError) {
        if (uiState.isCategoryNameError) viewModel.blinkingCategoryName()
        if (uiState.isCategoryColorError) viewModel.blinkingColorCategory()
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
                    containerColor = whiteColor,
                    contentColor = blackColor
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(defaultDimensions.small)
        ) {
            // Название категории
            Text(
                text = stringResource(R.string.text_category_name),
                color = Color(uiState.textColorCategoryName),
                modifier = Modifier.padding(defaultDimensions.verySmall)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = defaultDimensions.small)
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
                        contentDescription = stringResource(R.string.content_delete_name),
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
                text = stringResource(R.string.text_category_color),
                color = Color(uiState.textColorCategoryColor),
                modifier = Modifier.padding(defaultDimensions.verySmall)
            )
            Box(
                modifier = Modifier
                    .padding(start = defaultDimensions.verySmall)
                    .size(defaultDimensions.iconSize)
                    .background(
                        color = uiState.colorCategory?.let { Color(it) }
                            ?: Color.Transparent,
                        shape = RoundedCornerShape(defaultDimensions.smallCornerShape)
                    )
                    .border(
                        color = MaterialTheme.colorScheme.onSurface,
                        width = defaultDimensions.borderWidth,
                        shape = RoundedCornerShape(defaultDimensions.smallCornerShape)
                    )
                    .clickable { viewModel.changeIsShowColorPicker(true) }
            )

            // Создаём категорию, проверяя при этом необходимые условия для этого (обязательные параметры: название, цвет, тип)
            Button(
                onClick = {
                    viewModel.createCategory()
                },
                colors = ButtonDefaults.buttonColors(Color.White),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = stringResource(R.string.button_add))
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