package com.example.monefy.data.repository.impl

import com.example.monefy.data.local.dao.FinancesDao
import com.example.monefy.data.mappers.toDataLayer
import com.example.monefy.data.mappers.toDomainLayer
import com.example.monefy.domain.interfaces.local.FinancesRepository
import com.example.monefy.domain.model.Category
import com.example.monefy.domain.model.CategoryWithFinances
import com.example.monefy.domain.model.Finance

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

    override suspend fun getCategoriesId(): List<Int> {
        return financesDao.getCategoriesId()
    }

    override suspend fun getCategoryById(categoryId: Int): Category {
        return financesDao.getCategoryById(categoryId = categoryId).toDomainLayer()
    }

    override suspend fun getCategoryWithFinances(categoryId: Int): CategoryWithFinances? {
        return financesDao.getCategoryWithFinances(categoryId = categoryId)?.toDomainLayer()
    }

    override suspend fun getFinancesByDateSortDesc(): List<Finance> {
        return financesDao.getFinancesByDateSortDesc().map { it.toDomainLayer() }
    }

    override suspend fun getAllCategories(): List<Category> {
        return financesDao.getAllCategories().map { it.toDomainLayer() }
    }

    override suspend fun getCategoriesByType(categoryType: String): List<Category> {
        return financesDao.getCategoriesByType(categoryType = categoryType)
            .map { it.toDomainLayer() }
    }

    override suspend fun getFinancesByType(financeType: String): List<Finance> {
        return financesDao.getFinancesByType(financeType = financeType).map { it.toDomainLayer() }
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

    override suspend fun getAllFinances(): List<Finance> {
        return financesDao.getAllFinances().map { it.toDomainLayer() }
    }
}