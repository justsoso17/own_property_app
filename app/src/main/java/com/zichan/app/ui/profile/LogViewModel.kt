package com.zichan.app.ui.profile

import android.app.Application
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zichan.app.data.entity.AssetLogEntity
import com.zichan.app.data.repository.AssetLogRepository
import com.google.gson.GsonBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class LogUiState(
    val logs: List<AssetLogEntity> = emptyList(),
    val isLoading: Boolean = true,
    val message: String? = null
)

@HiltViewModel
class LogViewModel @Inject constructor(
    application: Application,
    private val repository: AssetLogRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(LogUiState())
    val uiState: StateFlow<LogUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAll().collect { logs ->
                _uiState.value = _uiState.value.copy(logs = logs, isLoading = false)
            }
        }
    }

    fun deleteLog(log: AssetLogEntity) {
        viewModelScope.launch {
            repository.deleteById(log.id)
            _uiState.value = _uiState.value.copy(message = "已删除")
        }
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            repository.deleteAll()
            _uiState.value = _uiState.value.copy(message = "已清空")
        }
    }

    fun exportAndShare() {
        viewModelScope.launch {
            try {
                val logs = withContext(Dispatchers.IO) { repository.getAllOnce() }
                val json = GsonBuilder().setPrettyPrinting().create().toJson(logs)
                val dir = File(getApplication<Application>().filesDir, "exports")
                if (!dir.exists()) dir.mkdirs()
                val name = "logs_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(Date())}.json"
                val file = File(dir, name)
                withContext(Dispatchers.IO) { file.writeText(json) }

                val uri = FileProvider.getUriForFile(
                    getApplication(),
                    "${getApplication<Application>().packageName}.fileprovider",
                    file
                )
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/json"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    clipData = android.content.ClipData.newRawUri("", uri)
                }
                getApplication<Application>().startActivity(
                    Intent.createChooser(shareIntent, "分享日志").apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                )
                _uiState.value = _uiState.value.copy(message = "导出成功")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(message = "导出失败: ${e.message}")
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}
