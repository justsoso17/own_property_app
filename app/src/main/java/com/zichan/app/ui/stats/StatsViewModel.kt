package com.zichan.app.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zichan.app.data.entity.AssetEntity
import com.zichan.app.data.repository.AssetRepository
import com.zichan.app.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryStat(
    val name: String,
    val total: Double,
    val count: Int
)

data class StatsUiState(
    val totalValue: Double = 0.0,
    val categoryStats: List<CategoryStat> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val assetRepository: AssetRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                assetRepository.getAll(),
                categoryRepository.getAll()
            ) { assets, categories ->
                val activeAssets = assets.filter { it.status !in listOf("已出售", "已丢弃") }
                val totalValue = activeAssets.sumOf { it.price }
                val categoryStats = categories.map { cat ->
                    val catAssets = activeAssets.filter { it.categoryId == cat.id }
                    CategoryStat(
                        name = cat.name,
                        total = catAssets.sumOf { it.price },
                        count = catAssets.size
                    )
                }.filter { it.count > 0 }.sortedByDescending { it.total }

                StatsUiState(
                    totalValue = totalValue,
                    categoryStats = categoryStats,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
