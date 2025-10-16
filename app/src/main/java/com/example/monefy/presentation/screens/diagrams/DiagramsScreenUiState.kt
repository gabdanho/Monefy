package com.example.monefy.presentation.screens.diagrams

import com.example.monefy.presentation.model.DiagramInfo

/**
 * UI-состояние экрана диаграмм.
 *
 * @property selectedTabIndex Индекс выбранной вкладки диаграмм.
 * @property diagramsInfo Список информации для построения диаграмм.
 */
data class DiagramsScreenUiState(
    val selectedTabIndex: Int = 0,
    val diagramsInfo: List<DiagramInfo> = emptyList(),
)