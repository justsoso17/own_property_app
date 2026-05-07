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
    val biometricEnabled: Boolean = false,
    val isExporting: Boolean = false,
    val exportMessage: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    application: Application,
    private val backupManager: BackupManager
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun toggleBiometric(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(biometricEnabled = enabled)
    }

    fun exportData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true)
            try {
                backupManager.exportToJson()
                _uiState.value = _uiState.value.copy(
                    isExporting = false,
                    exportMessage = "导出成功"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isExporting = false,
                    exportMessage = "导出失败: ${e.message}"
                )
            }
        }
    }

    fun clearExportMessage() {
        _uiState.value = _uiState.value.copy(exportMessage = null)
    }
}
