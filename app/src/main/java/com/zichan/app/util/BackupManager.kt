package com.zichan.app.util

import android.content.Context
import com.google.gson.GsonBuilder
import com.zichan.app.data.database.AppDatabase
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

@Singleton
class BackupManager @Inject constructor(
    private val database: AppDatabase,
    @ApplicationContext private val appContext: Context
) {
    private val gson = GsonBuilder().setPrettyPrinting().create()

    suspend fun exportToJson() = withContext(Dispatchers.IO) {
        val assets = database.assetDao().getAll().first()
        val persons = database.personDao().getAll().first()
        val lendRecords = database.lendRecordDao().getAll().first()
        val logs = database.assetLogDao().getAll().first()
        val categories = database.categoryDao().getAll().first()
        val locations = database.locationDao().getAll().first()

        val data = mapOf(
            "exportDate" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(Date()),
            "categories" to categories,
            "locations" to locations,
            "assets" to assets,
            "persons" to persons,
            "lendRecords" to lendRecords,
            "logs" to logs
        )

        val dateStr = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(Date())
        val dir = appContext.getExternalFilesDir(null) ?: appContext.filesDir
        val file = File(dir, "zichan_backup_$dateStr.json")
        file.writeText(gson.toJson(data))
    }
}
