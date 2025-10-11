package com.example.monefy.presentation.screens

import androidx.lifecycle.ViewModel
import com.example.monefy.presentation.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    val navigator: Navigator
) : ViewModel()