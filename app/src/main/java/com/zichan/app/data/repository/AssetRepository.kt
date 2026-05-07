package com.zichan.app.data.repository

import com.zichan.app.data.dao.AssetDao
import com.zichan.app.data.entity.AssetEntity
import com.zichan.app.data.entity.AssetLogEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetRepository @Inject constructor(
    private val dao: AssetDao,
    private val logRepository: AssetLogRepository
) {
    fun getAll(): Flow<List<AssetEntity>> = dao.getAll()

    suspend fun getById(id: Long): AssetEntity? = dao.getById(id)

    suspend fun insert(asset: AssetEntity): Long {
        val id = dao.insert(asset)
        logRepository.insert(AssetLogEntity(assetId = id, operation = "添加", detail = asset.name))
        return id
    }

    suspend fun update(asset: AssetEntity) {
        dao.update(asset)
        logRepository.insert(AssetLogEntity(assetId = asset.id, operation = "修改", detail = asset.name))
    }

    suspend fun delete(asset: AssetEntity) {
        dao.delete(asset)
        logRepository.insert(AssetLogEntity(assetId = asset.id, operation = "删除", detail = asset.name))
    }

    suspend fun sell(asset: AssetEntity) {
        dao.update(asset.copy(status = "已出售"))
        logRepository.insert(AssetLogEntity(assetId = asset.id, operation = "出售", detail = asset.name))
    }

    suspend fun discard(asset: AssetEntity) {
        dao.update(asset.copy(status = "已丢弃"))
        logRepository.insert(AssetLogEntity(assetId = asset.id, operation = "丢弃", detail = asset.name))
    }

    fun getByStatus(status: String): Flow<List<AssetEntity>> = dao.getByStatus(status)

    fun search(keyword: String): Flow<List<AssetEntity>> = dao.search(keyword)

    fun filter(
        keyword: String? = null,
        categoryId: Long? = null,
        status: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null
    ): Flow<List<AssetEntity>> = dao.filter(keyword, categoryId, status, minPrice, maxPrice)

    fun countByStatus(status: String): Flow<Int> = dao.countByStatus(status)

    fun totalValue(): Flow<Double?> = dao.totalValue()

    fun getExpiringSoon(now: Long, sevenDaysLater: Long): Flow<List<AssetEntity>> =
        dao.getExpiringSoon(now, sevenDaysLater)
}
