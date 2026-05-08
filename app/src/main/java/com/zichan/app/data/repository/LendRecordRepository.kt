package com.zichan.app.data.repository

import com.zichan.app.data.dao.LendRecordDao
import com.zichan.app.data.entity.AssetEntity
import com.zichan.app.data.entity.AssetLogEntity
import com.zichan.app.data.entity.LendRecordEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LendRecordRepository @Inject constructor(
    private val dao: LendRecordDao,
    private val assetRepository: AssetRepository,
    private val logRepository: AssetLogRepository,
    private val personRepository: PersonRepository,
) {
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)

    fun getAll(): Flow<List<LendRecordEntity>> = dao.getAll()

    suspend fun getById(id: Long): LendRecordEntity? = dao.getById(id)

    suspend fun getActiveByAssetId(assetId: Long): LendRecordEntity? = dao.getActiveByAssetId(assetId)

    fun getByAssetId(assetId: Long): Flow<List<LendRecordEntity>> = dao.getByAssetId(assetId)

    fun getByStatus(status: String): Flow<List<LendRecordEntity>> = dao.getByStatus(status)

    suspend fun lend(record: LendRecordEntity, asset: AssetEntity) {
        assetRepository.update(asset.copy(status = "已借出"))
        dao.insert(record)
        val borrowerName = record.personId?.let { personRepository.getById(it)?.name } ?: "未知"
        val lendTime = sdf.format(Date(record.lendDate))
        val returnTime = record.expectedReturnDate?.let { sdf.format(Date(it)) } ?: "未设置"
        val days = record.expectedReturnDate?.let {
            ((it - record.lendDate) / (24 * 3600 * 1000)).toInt()
        } ?: 0
        logRepository.insert(
            AssetLogEntity(
                assetId = record.assetId, operation = "借出",
                detail = "物品: ${asset.name}\n借出给: $borrowerName\n借出时间: $lendTime\n预计归还: $returnTime\n借出天数: ${days}天"
            )
        )
    }

    suspend fun returnAsset(record: LendRecordEntity, asset: AssetEntity) {
        val now = System.currentTimeMillis()
        dao.update(record.copy(status = "已归还", actualReturnDate = now))
        assetRepository.update(asset.copy(status = "使用中"))
        val borrowerName = record.personId?.let { personRepository.getById(it)?.name } ?: "未知"
        val lendTime = sdf.format(Date(record.lendDate))
        val returnTime = sdf.format(Date(now))
        val daysLate = record.expectedReturnDate?.let {
            ((now - it).toDouble() / (24 * 3600 * 1000)).toInt()
        } ?: 0
        val overdue = if (daysLate > 0) " (逾期${daysLate}天)" else ""
        logRepository.insert(
            AssetLogEntity(
                assetId = record.assetId, operation = "归还",
                detail = "物品: ${asset.name}\n借出给: $borrowerName\n借出时间: $lendTime\n归还时间: $returnTime$overdue"
            )
        )
    }
}
