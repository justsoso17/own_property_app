package com.zichan.app.data.repository

import com.zichan.app.data.dao.CategoryDao
import com.zichan.app.data.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val dao: CategoryDao
) {
    fun getAll(): Flow<List<CategoryEntity>> = dao.getAll()

    suspend fun getById(id: Long): CategoryEntity? = dao.getById(id)
}
