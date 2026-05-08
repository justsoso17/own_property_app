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
    val soldCount: Int = 0,
    val discardedCount: Int = 0,
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
                combine(
                    repository.totalValue(),
                    repository.countByStatus("使用中"),
                    repository.countByStatus("闲置"),
                    repository.countByStatus("已借出"),
                    repository.countByStatus("已出售"),
                ) { value, inUse, idle, lent, sold ->
                    arrayOf(value, inUse, idle, lent, sold)
                },
                combine(
                    repository.countByStatus("已丢弃"),
                    repository.getExpiringSoon(
                        System.currentTimeMillis(),
                        System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000
                    ),
                ) { discarded, expiring ->
                    arrayOf(discarded, expiring)
                },
            ) { arr1, arr2 ->
                @Suppress("UNCHECKED_CAST")
                val value = arr1[0] as Double?
                val inUse = arr1[1] as Int
                val idle = arr1[2] as Int
                val lent = arr1[3] as Int
                val sold = arr1[4] as Int
                val discarded = arr2[0] as Int
                @Suppress("UNCHECKED_CAST")
                val expiring = arr2[1] as List<AssetEntity>

                HomeUiState(
                    totalValue = value ?: 0.0,
                    inUseCount = inUse,
                    idleCount = idle,
                    lentCount = lent,
                    soldCount = sold,
                    discardedCount = discarded,
                    expiringAssets = expiring,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
