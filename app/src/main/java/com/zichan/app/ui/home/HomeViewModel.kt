package com.zichan.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zichan.app.data.entity.AssetEntity
import com.zichan.app.data.repository.AssetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val totalValue: Double = 0.0,
    val inUseCount: Int = 0,
    val idleCount: Int = 0,
    val lentCount: Int = 0,
    val expiringAssets: List<AssetEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AssetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.totalValue(),
                repository.countByStatus("使用中"),
                repository.countByStatus("闲置"),
                repository.countByStatus("已借出"),
                repository.getExpiringSoon(
                    System.currentTimeMillis(),
                    System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000
                )
            ) { value, inUse, idle, lent, expiring ->
                HomeUiState(
                    totalValue = value ?: 0.0,
                    inUseCount = inUse,
                    idleCount = idle,
                    lentCount = lent,
                    expiringAssets = expiring,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
