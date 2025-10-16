package com.example.monefy.data.local.datasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.monefy.data.local.dao.FinancesDao
import com.example.monefy.data.local.entity.Category
import com.example.monefy.data.local.entity.Finance

/**
 * Главная база данных приложения.
 *
 * @property categoryDao DAO для работы с категориями и финансами.
 */
@Database(entities = [Category::class, Finance::class], version = 9)
abstract class MonefyDatabase : RoomDatabase() {
    abstract fun categoryDao() : FinancesDao
    companion object {
        @Volatile
        private var INSTANCE: MonefyDatabase? = null

        fun getDatabase(context: Context): MonefyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MonefyDatabase::class.java,
                    "app_database.db")
                    .createFromAsset("database/monefy_database.db")
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}