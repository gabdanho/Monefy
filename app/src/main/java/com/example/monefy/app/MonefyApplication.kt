package com.example.monefy.app

import android.app.Application
import com.example.monefy.data.local.datasource.MonefyDatabase

class MonefyApplication : Application() {
    val database: MonefyDatabase by lazy { MonefyDatabase.Companion.getDatabase(this) }
}