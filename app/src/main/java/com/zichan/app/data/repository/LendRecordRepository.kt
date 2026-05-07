package com.zichan.app.data.repository

import com.zichan.app.data.dao.LendRecordDao
import com.zichan.app.data.entity.AssetEntity
import com.zichan.app.data.entity.AssetLogEntity
import com.zichan.app.data.entity.LendRecordEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LendRecordRepository @Inject constructor(
    private val dao: LendRecordDao,
    private val assetRepository: AssetRepository,
    private val logRepository: AssetLogRepository
) {
    fun getAll(): Flow<List<LendRecordEntity>> = dao.getAll()

    suspend fun getById(id: Long): LendRecordEntity? = dao.getById(id)

    fun getByAssetId(assetId: Long): Flow<List<LendRecordEntity>> = dao.getByAssetId(assetId)

    fun getByStatus(status: String): Flow<List<LendRecordEntity>> = dao.getByStatus(status)

    suspend fun lend(record: LendRecordEntity, asset: AssetEntity) {
        assetRepository.update(asset.copy(status = "已借出"))
        dao.insert(record)
        logRepository.insert(
            AssetLogEntity(
                assetId = record.assetId, operation = "借出",
                detail = "借出 ${asset.name}"
            )
        )
    }

    suspend fun returnAsset(record: LendRecordEntity, asset: AssetEntity) {
        val now = System.currentTimeMillis()
        dao.update(record.copy(status = "已归还", actualReturnDate = now))
        assetRepository.update(asset.copy(status = "使用中"))
        logRepository.insert(
            AssetLogEntity(
                assetId = record.assetId, operation = "归还",
                detail = "归还 ${asset.name}"
            )
        )
    }
}
