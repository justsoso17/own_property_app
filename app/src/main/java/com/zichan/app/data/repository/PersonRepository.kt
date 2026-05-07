package com.zichan.app.data.repository

import com.zichan.app.data.dao.PersonDao
import com.zichan.app.data.entity.PersonEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersonRepository @Inject constructor(
    private val dao: PersonDao
) {
    fun getAll(): Flow<List<PersonEntity>> = dao.getAll()

    suspend fun getById(id: Long): PersonEntity? = dao.getById(id)

    suspend fun insert(person: PersonEntity): Long = dao.insert(person)

    suspend fun update(person: PersonEntity) = dao.update(person)

    suspend fun delete(person: PersonEntity) = dao.delete(person)
}
