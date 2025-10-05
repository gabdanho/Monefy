package com.example.monefy.di

import android.content.Context
import com.example.monefy.data.local.dao.FinancesDao
import com.example.monefy.data.local.datasource.MonefyDatabase
import com.example.monefy.data.repository.impl.FinancesRepositoryImpl
import com.example.monefy.domain.interfaces.local.FinancesRepository
import com.example.monefy.presentation.navigation.Navigator
import com.example.monefy.presentation.navigation.NavigatorImpl
import com.example.monefy.presentation.navigation.model.MonefyGraph
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MonefyModule {

    @Provides
    @Singleton
    fun provideMonefyDatabase(@ApplicationContext context: Context): MonefyDatabase {
        return MonefyDatabase.getDatabase(context = context)
    }

    @Provides
    @Singleton
    fun provideFinancesDao(appDatabase: MonefyDatabase): FinancesDao {
        return appDatabase.categoryDao()
    }

    @Provides
    @Singleton
    fun provideFinancesRepository(financesDao: FinancesDao): FinancesRepository {
        return FinancesRepositoryImpl(financesDao = financesDao)
    }

    @Provides
    @Singleton
    fun provideNavigator(): Navigator {
        return NavigatorImpl(startDestination = MonefyGraph.MainMonefyScreen)
    }
}