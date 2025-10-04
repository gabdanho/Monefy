package com.example.monefy.data.local.datasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.monefy.data.local.converters.DateConverter
import com.example.monefy.data.local.dao.FinancesDao
import com.example.monefy.data.local.entity.Category
import com.example.monefy.data.local.entity.Finance

@TypeConverters(DateConverter::class)
@Database(entities = [Category::class, Finance::class], version = 6)
abstract class MonefyDatabase : RoomDatabase() {
    abstract fun categoryDao() : FinancesDao
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