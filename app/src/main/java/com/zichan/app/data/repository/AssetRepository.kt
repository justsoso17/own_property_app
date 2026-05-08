package com.zichan.app.data.repository

import com.zichan.app.data.dao.AssetDao
import com.zichan.app.data.entity.AssetEntity
import com.zichan.app.data.entity.AssetLogEntity
import kotlinx.coroutines.flow.Flow
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetRepository @Inject constructor(
    private val dao: AssetDao,
    private val logRepository: AssetLogRepository,
    private val categoryRepository: CategoryRepository,
    private val locationRepository: LocationRepository,
) {
    private val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    private val priceFmt = NumberFormat.getCurrencyInstance(Locale.CHINA)

    fun getAll(): Flow<List<AssetEntity>> = dao.getAll()

    suspend fun getById(id: Long): AssetEntity? = dao.getById(id)

    private suspend fun assetDetail(a: AssetEntity): String {
        val lines = mutableListOf<String>()
        lines.add("名称: ${a.name}")
        if (a.brand.isNotBlank() || a.model.isNotBlank()) {
            lines.add("品牌型号: ${listOf(a.brand, a.model).filter { it.isNotBlank() }.joinToString(" ")}")
        }
        val catName = a.categoryId?.let { categoryRepository.getById(it)?.name }
        if (catName != null) lines.add("分类: $catName")
        if (a.price > 0) lines.add("价格: ${priceFmt.format(a.price)}")
        lines.add("状态: ${a.status}")
        val locName = a.locationId?.let { locationRepository.getById(it)?.name }
        if (locName != null) lines.add("位置: $locName")
        if (a.purchaseDate != null) lines.add("购买日期: ${dateFmt.format(Date(a.purchaseDate))}")
        if (a.purchaseChannel.isNotBlank()) lines.add("购买渠道: ${a.purchaseChannel}")
        if (a.specs.isNotBlank()) lines.add("规格: ${a.specs}")
        if (a.serialNumber.isNotBlank()) lines.add("序列号: ${a.serialNumber}")
        if (a.isVirtual) lines.add("类型: 虚拟资产")
        if (a.notes.isNotBlank()) lines.add("备注: ${a.notes}")
        return lines.joinToString("\n")
    }

    suspend fun insert(asset: AssetEntity): Long {
        val id = dao.insert(asset)
        logRepository.insert(AssetLogEntity(assetId = id, operation = "添加", detail = assetDetail(asset)))
        return id
    }

    suspend fun update(asset: AssetEntity) {
        val old = dao.getById(asset.id)
        dao.update(asset)
        val changes = if (old != null && old.status != asset.status) {
            assetDetail(asset) + "\n[状态变更: ${old.status} → ${asset.status}]"
        } else {
            assetDetail(asset)
        }
        logRepository.insert(AssetLogEntity(assetId = asset.id, operation = "修改", detail = changes))
    }

    suspend fun delete(asset: AssetEntity) {
        dao.delete(asset)
        logRepository.insert(AssetLogEntity(assetId = asset.id, operation = "删除", detail = assetDetail(asset)))
    }

    suspend fun sell(asset: AssetEntity) {
        val old = dao.getById(asset.id)
        val updated = asset.copy(status = "已出售")
        dao.update(updated)
        val detail = assetDetail(updated) + if (old != null) "\n[状态变更: ${old.status} → 已出售]" else ""
        logRepository.insert(AssetLogEntity(assetId = asset.id, operation = "出售", detail = detail))
    }

    suspend fun discard(asset: AssetEntity) {
        val old = dao.getById(asset.id)
        val updated = asset.copy(status = "已丢弃")
        dao.update(updated)
        val detail = assetDetail(updated) + if (old != null) "\n[状态变更: ${old.status} → 已丢弃]" else ""
        logRepository.insert(AssetLogEntity(assetId = asset.id, operation = "丢弃", detail = detail))
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
