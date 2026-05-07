package com.zichan.app.data.repository

import com.zichan.app.data.dao.AssetLogDao
import com.zichan.app.data.entity.AssetLogEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetLogRepository @Inject constructor(
    private val dao: AssetLogDao
) {
    fun getAll(): Flow<List<AssetLogEntity>> = dao.getAll()

    fun getByAssetId(assetId: Long): Flow<List<AssetLogEntity>> = dao.getByAssetId(assetId)

    suspend fun insert(log: AssetLogEntity) = dao.insert(log)

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    suspend fun deleteAll() = dao.deleteAll()
}
