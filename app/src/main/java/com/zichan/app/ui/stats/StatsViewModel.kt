package com.zichan.app.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zichan.app.data.entity.AssetEntity
import com.zichan.app.data.repository.AssetRepository
import com.zichan.app.data.repository.CategoryRepository
import com.zichan.app.data.strategy.ValueDepreciationStrategyFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class CategoryStat(
    val name: String,
    val total: Double,
    val count: Int
)

data class DepreciationInfo(
    val assetName: String,
    val originalPrice: Double,
    val currentValue: Double,
    val yearsOwned: Int,
    val categoryName: String
)

data class StatsUiState(
    val totalValue: Double = 0.0,
    val depreciatedValue: Double = 0.0,
    val depreciationLoss: Double = 0.0,
    val categoryStats: List<CategoryStat> = emptyList(),
    val topDepreciated: List<DepreciationInfo> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val assetRepository: AssetRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()
    private val now = System.currentTimeMillis()
    private val currentYear = Calendar.getInstance().get(Calendar.YEAR)

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

                // Calculate depreciation for assets with purchase dates
                val deprList = activeAssets
                    .filter { it.purchaseDate != null && it.price > 0 }
                    .map { asset ->
                        val catName = categories.find { it.id == asset.categoryId }?.name ?: "其他"
                        val yearsOwned = ((now - asset.purchaseDate!!) / (365.25 * 24 * 3600 * 1000)).toInt()
                        val strategy = ValueDepreciationStrategyFactory.getStrategy(catName)
                        val currentVal = strategy.calculate(asset.price, asset.price, yearsOwned)
                        DepreciationInfo(
                            assetName = asset.name,
                            originalPrice = asset.price,
                            currentValue = currentVal,
                            yearsOwned = yearsOwned,
                            categoryName = catName
                        )
                    }
                    .sortedByDescending { it.originalPrice - it.currentValue }
                    .take(10)

                val depreciatedTotal = deprList.sumOf { it.currentValue } +
                    activeAssets.filter { it.purchaseDate == null }.sumOf { it.price }

                StatsUiState(
                    totalValue = totalValue,
                    depreciatedValue = depreciatedTotal,
                    depreciationLoss = totalValue - depreciatedTotal,
                    categoryStats = categoryStats,
                    topDepreciated = deprList,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
