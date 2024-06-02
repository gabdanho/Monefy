package com.example.monefy.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.InternalCoroutinesApi

@TypeConverters(DateConverter::class)
@Database(entities = [Category::class, Spend::class], version = 2, exportSchema = false)
abstract class MonefyDatabase : RoomDatabase() {
    abstract fun categoryDao() : CategoryDao
    companion object {
        @Volatile
        private var Instance: MonefyDatabase ?= null

        fun getDatabase(context: Context): MonefyDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, MonefyDatabase::class.java, "monefy_database")
                    .fallbackToDestructiveMigration()
                    .build().also { Instance = it }
            }
        }
    }
}
