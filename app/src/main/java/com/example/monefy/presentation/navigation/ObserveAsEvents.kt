package com.example.monefy.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Подписка на [Flow] в Compose и обработка событий через [onEvent].
 *
 * Использует [Lifecycle.State.STARTED], чтобы не собирать события вне активного состояния.
 *
 * @param T Тип события
 * @param flow Поток событий [Flow]
 * @param key1 Ключ для LaunchedEffect (опционально)
 * @param key2 Ключ для LaunchedEffect (опционально)
 * @param onEvent Lambda для обработки каждого события
 */
@Composable
fun <T> ObserveAsEvents(
    flow: Flow<T>,
    key1: Any,
    key2: Any,
    onEvent: (T) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner.lifecycle, key1, key2) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                flow.collect(onEvent)
            }
        }
    }
}