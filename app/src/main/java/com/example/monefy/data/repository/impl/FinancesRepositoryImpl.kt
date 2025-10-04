package com.example.monefy.data.repository.impl

import com.example.monefy.data.local.dao.FinancesDao
import com.example.monefy.data.mappers.toDataLayer
import com.example.monefy.data.mappers.toDomainLayer
import com.example.monefy.domain.interfaces.local.FinancesRepository
import com.example.monefy.domain.model.Category
import com.example.monefy.domain.model.CategoryWithFinances
import com.example.monefy.domain.model.Finance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FinancesRepositoryImpl(
    private val financesDao: FinancesDao,
) : FinancesRepository {

    override suspend fun createCategory(category: Category) {
        financesDao.createCategory(category = category.toDataLayer())
    }

    override suspend fun deleteCategory(category: Category) {
        financesDao.deleteCategory(category = category.toDataLayer())
    }

    override suspend fun updateCategory(category: Category) {
        financesDao.updateCategory(category = category.toDataLayer())
    }

    override suspend fun getCategoriesId(): Flow<List<Int>> {
        return financesDao.getCategoriesId()
    }

    override suspend fun getCategoryById(categoryId: Int): Flow<Category> {
        return financesDao.getCategoryById(categoryId = categoryId).map { it.toDomainLayer() }
    }

    override suspend fun getCategoryWithFinances(categoryId: Int): Flow<CategoryWithFinances> {
        return financesDao.getCategoryWithFinances(categoryId = categoryId).map { it.toDomainLayer() }
    }

    override suspend fun getCategoriesByDateSortDesc(): Flow<List<Finance>> {
        return financesDao.getCategoriesByDateSortDesc().map { list ->
            list.map { it.toDomainLayer() }
        }
    }

    override suspend fun getAllCategories(): Flow<List<Category>> {
        return financesDao.getAllCategories().map { list ->
            list.map { it.toDomainLayer() }
        }
    }

    override suspend fun getCategoriesByType(categoryType: String): Flow<List<Category>> {
        return financesDao.getCategoriesByType(categoryType = categoryType).map { list ->
            list.map { it.toDomainLayer() }
        }
    }

    override suspend fun getFinancesByType(financeType: String): Flow<List<Finance>> {
        return financesDao.getFinancesByType(financeType = financeType).map { list ->
            list.map { it.toDomainLayer() }
        }
    }

    override suspend fun addFinance(finance: Finance) {
        return financesDao.addFinance(finance = finance.toDataLayer())
    }

    override suspend fun deleteFinance(finance: Finance) {
        return financesDao.deleteFinance(finance = finance.toDataLayer())
    }

    override suspend fun updateFinance(finance: Finance) {
        return financesDao.updateFinance(finance = finance.toDataLayer())
    }

    override suspend fun getAllFinances(): Flow<List<Finance>> {
        return financesDao.getAllFinances().map { list ->
            list.map { it.toDomainLayer() }
        }
    }
}