package com.example.monefy.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.InternalCoroutinesApi

@TypeConverters(DateConverter::class)
@Database(entities = [Category::class, Finance::class], version = 6)
abstract class MonefyDatabase : RoomDatabase() {
    abstract fun categoryDao() : CategoryDao
    companion object {
        @Volatile
        private var INSTANCE: MonefyDatabase? = null

        fun getDatabase(context: Context): MonefyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    MonefyDatabase::class.java,
                    "app_database")
                    .createFromAsset("database/monefy_database.db")
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}
