package com.example.monefy.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.compose.AppTheme
import com.example.monefy.presentation.notification.NotificationModule
import com.example.monefy.presentation.screens.MainScreen
import dagger.hilt.android.AndroidEntryPoint

/**
 * Главная Activity приложения Monefy.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val notificationModule = NotificationModule(this)
        notificationModule.createNotificationChannel()
        notificationModule.scheduleDailyNotification()

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}