package com.zichan.app.util

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.zichan.app.data.database.AppDatabase
import com.zichan.app.data.entity.AssetEntity
import com.zichan.app.data.entity.CategoryEntity
import com.zichan.app.data.entity.LendRecordEntity
import com.zichan.app.data.entity.LocationEntity
import com.zichan.app.data.entity.PersonEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

data class AssetExport(
    val name: String, val brand: String, val model: String, val categoryId: Long?,
    val price: Double, val purchaseDate: Long?, val purchaseChannel: String, val status: String,
    val locationId: Long?, val specs: String, val serialNumber: String, val notes: String,
    val isVirtual: Boolean, val expiryDate: Long?, val photoBase64: String?, val createdAt: Long
)

data class BackupData(
    val exportDate: String = "",
    val categories: List<CategoryEntity> = emptyList(),
    val locations: List<LocationEntity> = emptyList(),
    val assets: List<AssetExport> = emptyList(),
    val persons: List<PersonEntity> = emptyList(),
    val lendRecords: List<LendRecordEntity> = emptyList(),
)

@Singleton
class BackupManager @Inject constructor(
    private val database: AppDatabase,
    @ApplicationContext private val appContext: Context
) {
    private val gson = GsonBuilder().setPrettyPrinting().create()

    suspend fun exportToJson() = withContext(Dispatchers.IO) {
        val data = collectData()
        val json = gson.toJson(data)
        val dateStr = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(Date())
        val fileName = "zichan_backup_$dateStr.json"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/json")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = appContext.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            uri?.let {
                appContext.contentResolver.openOutputStream(it)?.use { os -> os.write(json.toByteArray()) }
            }
        } else {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(dir, fileName)
            file.writeText(json)
        }
    }

    suspend fun importFromJson(uri: android.net.Uri, replaceExisting: Boolean = false): String = withContext(Dispatchers.IO) {
        val json = appContext.contentResolver.openInputStream(uri)?.use { it.bufferedReader().readText() }
            ?: throw Exception("无法读取文件")
        val type = object : TypeToken<BackupData>() {}.type
        val data: BackupData = gson.fromJson(json, type)

        if (replaceExisting) {
            database.assetLogDao().deleteAll()
            database.lendRecordDao().let { dao ->
                dao.getAll().first().forEach { dao.update(it.copy(status = "已删除")) }
            }
            database.assetDao().let { dao ->
                dao.getAll().first().forEach { dao.delete(it) }
            }
        }

        val existingAssets = database.assetDao().getAll().first()
        val photoDir = File(appContext.filesDir, "photos")
        if (!photoDir.exists()) photoDir.mkdirs()

        var importedAssets = 0
        var skippedAssets = 0

        data.categories.forEach { database.categoryDao().insertAll(listOf(it)) }
        data.locations.forEach { database.locationDao().insertAll(listOf(it)) }
        data.persons.forEach { database.personDao().insert(it) }

        data.assets.forEach { export ->
            val duplicate = existingAssets.any {
                it.name == export.name && it.brand == export.brand && it.model == export.model && it.price == export.price
            }
            if (!duplicate || replaceExisting) {
                var photoPath: String? = null
                if (!export.photoBase64.isNullOrBlank()) {
                    val fname = "imported_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(Date())}_${System.nanoTime()}.jpg"
                    val file = File(photoDir, fname)
                    file.writeBytes(Base64.decode(export.photoBase64, Base64.DEFAULT))
                    photoPath = file.absolutePath
                }
                val entity = AssetEntity(
                    name = export.name, brand = export.brand, model = export.model,
                    categoryId = export.categoryId, price = export.price,
                    purchaseDate = export.purchaseDate, purchaseChannel = export.purchaseChannel,
                    status = export.status, locationId = export.locationId,
                    specs = export.specs, serialNumber = export.serialNumber,
                    notes = export.notes, isVirtual = export.isVirtual,
                    expiryDate = export.expiryDate, photoPath = photoPath,
                    createdAt = export.createdAt
                )
                database.assetDao().insert(entity)
                importedAssets++
            } else {
                skippedAssets++
            }
        }
        data.lendRecords.forEach { database.lendRecordDao().insert(it) }
        val skipMsg = if (skippedAssets > 0) "\n跳过${skippedAssets}件已存在的资产" else ""
        "导入完成: ${importedAssets}件资产, ${data.persons.size}个联系人${skipMsg}"
    }

    private suspend fun collectData(): BackupData {
        val assets = database.assetDao().getAll().first()
        val persons = database.personDao().getAll().first()
        val lendRecords = database.lendRecordDao().getAll().first()
        val categories = database.categoryDao().getAll().first()
        val locations = database.locationDao().getAll().first()

        val assetExports = assets.map { a ->
            val photoBase64 = a.photoPath?.let { path ->
                runCatching { File(path).readBytes() }.getOrNull()?.let { bytes ->
                    Base64.encodeToString(bytes, Base64.NO_WRAP)
                }
            }
            AssetExport(
                name = a.name, brand = a.brand, model = a.model, categoryId = a.categoryId,
                price = a.price, purchaseDate = a.purchaseDate, purchaseChannel = a.purchaseChannel,
                status = a.status, locationId = a.locationId, specs = a.specs,
                serialNumber = a.serialNumber, notes = a.notes, isVirtual = a.isVirtual,
                expiryDate = a.expiryDate, photoBase64 = photoBase64, createdAt = a.createdAt
            )
        }

        return BackupData(
            exportDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(Date()),
            categories = categories,
            locations = locations,
            assets = assetExports,
            persons = persons,
            lendRecords = lendRecords,
        )
    }
}
