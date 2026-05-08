package com.zichan.app.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zichan.app.util.BackupManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isExporting: Boolean = false,
    val isImporting: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    application: Application,
    private val backupManager: BackupManager
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun exportData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true)
            try {
                backupManager.exportToJson()
                _uiState.value = _uiState.value.copy(isExporting = false, message = "已导出到下载目录")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isExporting = false, message = "导出失败: ${e.message}")
            }
        }
    }

    fun importData(uri: android.net.Uri, replace: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isImporting = true)
            try {
                val msg = backupManager.importFromJson(uri, replace)
                _uiState.value = _uiState.value.copy(isImporting = false, message = msg)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isImporting = false, message = "导入失败: ${e.message}")
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}
