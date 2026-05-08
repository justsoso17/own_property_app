package com.zichan.app.data.repository

import com.zichan.app.data.dao.LocationDao
import com.zichan.app.data.entity.LocationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    private val dao: LocationDao
) {
    fun getAll(): Flow<List<LocationEntity>> = dao.getAll()

    suspend fun getById(id: Long): LocationEntity? = dao.getById(id)

    suspend fun insertOne(location: LocationEntity): Long = dao.insertOne(location)
}
