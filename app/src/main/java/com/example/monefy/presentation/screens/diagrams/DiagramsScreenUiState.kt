package com.example.monefy.presentation.screens.diagrams

import com.example.monefy.presentation.model.DiagramInfo

data class DiagramsScreenUiState(
    val selectedTabIndex: Int = 0,
    val diagramsInfo: List<DiagramInfo> = emptyList(),
)