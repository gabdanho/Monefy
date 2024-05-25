package com.example.monefy

import android.app.Application
import com.example.monefy.data.MonefyDatabase

class MonefyApplication : Application() {
    val database: MonefyDatabase by lazy { MonefyDatabase.getDatabase(this) }
}