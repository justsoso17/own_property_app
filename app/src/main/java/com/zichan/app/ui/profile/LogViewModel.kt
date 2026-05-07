package com.zichan.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zichan.app.data.entity.AssetLogEntity
import com.zichan.app.data.repository.AssetLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LogUiState(
    val logs: List<AssetLogEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class LogViewModel @Inject constructor(
    private val repository: AssetLogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LogUiState())
    val uiState: StateFlow<LogUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAll().collect { logs ->
                _uiState.value = LogUiState(logs = logs, isLoading = false)
            }
        }
    }
}
