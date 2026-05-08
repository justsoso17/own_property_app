package com.zichan.app.util

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.zichan.app.data.database.AppDatabase
import com.google.gson.GsonBuilder
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltWorker
class BackupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val database: AppDatabase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val assets = database.assetDao().getAll().first()
            val persons = database.personDao().getAll().first()
            val lends = database.lendRecordDao().getAll().first()

            val data = mapOf(
                "date" to SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(Date()),
                "assets" to assets,
                "persons" to persons,
                "lendRecords" to lends
            )

            val dir = File(applicationContext.filesDir, "backups")
            if (!dir.exists()) dir.mkdirs()
            val name = "auto_${SimpleDateFormat("yyyyMMdd_HHmm", Locale.CHINA).format(Date())}.json"
            File(dir, name).writeText(GsonBuilder().setPrettyPrinting().create().toJson(data))
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
