package com.zichan.app.ui.asset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zichan.app.data.entity.AssetEntity
import com.zichan.app.data.entity.CategoryEntity
import com.zichan.app.data.entity.LocationEntity
import com.zichan.app.data.repository.AssetRepository
import com.zichan.app.data.repository.CategoryRepository
import com.zichan.app.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AssetListUiState(
    val assets: List<AssetEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val locations: List<LocationEntity> = emptyList(),
    val searchKeyword: String = "",
    val filterCategoryId: Long? = null,
    val filterStatus: String? = null,
    val isLoading: Boolean = true
)

data class AssetEditUiState(
    val name: String = "",
    val brand: String = "",
    val model: String = "",
    val categoryId: Long? = null,
    val price: String = "",
    val purchaseDate: Long? = null,
    val purchaseChannel: String = "",
    val status: String = "使用中",
    val locationId: Long? = null,
    val specs: String = "",
    val serialNumber: String = "",
    val notes: String = "",
    val isVirtual: Boolean = false,
    val expiryDate: Long? = null,
    val photoPath: String? = null,
    val categories: List<CategoryEntity> = emptyList(),
    val locations: List<LocationEntity> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val saved: Boolean = false
)

@HiltViewModel
class AssetViewModel @Inject constructor(
    private val assetRepository: AssetRepository,
    private val categoryRepository: CategoryRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _listState = MutableStateFlow(AssetListUiState())
    val listState: StateFlow<AssetListUiState> = _listState.asStateFlow()

    private val _editState = MutableStateFlow(AssetEditUiState())
    val editState: StateFlow<AssetEditUiState> = _editState.asStateFlow()

    init {
        loadCategories()
        loadLocations()
        loadAssets()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getAll().collect { cats ->
                _listState.value = _listState.value.copy(categories = cats)
                _editState.value = _editState.value.copy(categories = cats)
            }
        }
    }

    private fun loadLocations() {
        viewModelScope.launch {
            locationRepository.getAll().collect { locs ->
                _listState.value = _listState.value.copy(locations = locs)
                _editState.value = _editState.value.copy(locations = locs)
            }
        }
    }

    fun loadAssets() {
        viewModelScope.launch {
            assetRepository.getAll().collect { assets ->
                _listState.value = _listState.value.copy(assets = assets, isLoading = false)
            }
        }
    }

    fun search(keyword: String) {
        _listState.value = _listState.value.copy(searchKeyword = keyword)
        viewModelScope.launch {
            val flow = if (keyword.isBlank()) assetRepository.getAll()
            else assetRepository.search(keyword)
            flow.collect { assets ->
                _listState.value = _listState.value.copy(assets = assets)
            }
        }
    }

    fun applyFilter(categoryId: Long?, status: String?) {
        _listState.value = _listState.value.copy(filterCategoryId = categoryId, filterStatus = status)
        viewModelScope.launch {
            assetRepository.filter(
                keyword = _listState.value.searchKeyword.ifBlank { null },
                categoryId = categoryId,
                status = status,
                minPrice = null,
                maxPrice = null
            ).collect { assets ->
                _listState.value = _listState.value.copy(assets = assets)
            }
        }
    }

    fun loadAssetForEdit(assetId: Long) {
        viewModelScope.launch {
            if (assetId == 0L) {
                _editState.value = AssetEditUiState(
                    categories = _editState.value.categories,
                    locations = _editState.value.locations,
                    isLoading = false
                )
            } else {
                assetRepository.getById(assetId)?.let { asset ->
                    _editState.value = AssetEditUiState(
                        name = asset.name,
                        brand = asset.brand,
                        model = asset.model,
                        categoryId = asset.categoryId,
                        price = if (asset.price > 0) asset.price.toString() else "",
                        purchaseDate = asset.purchaseDate,
                        purchaseChannel = asset.purchaseChannel,
                        status = asset.status,
                        locationId = asset.locationId,
                        specs = asset.specs,
                        serialNumber = asset.serialNumber,
                        notes = asset.notes,
                        isVirtual = asset.isVirtual,
                        expiryDate = asset.expiryDate,
                        photoPath = asset.photoPath,
                        categories = _editState.value.categories,
                        locations = _editState.value.locations,
                        isLoading = false
                    )
                } ?: run {
                    _editState.value = _editState.value.copy(isLoading = false)
                }
            }
        }
    }

    fun updateEditField(update: AssetEditUiState.() -> AssetEditUiState) {
        _editState.value = _editState.value.update()
    }

    fun saveAsset(assetId: Long) {
        val state = _editState.value
        if (state.name.isBlank()) return

        viewModelScope.launch {
            _editState.value = _editState.value.copy(isSaving = true)
            val price = state.price.toDoubleOrNull() ?: 0.0

            val asset = AssetEntity(
                id = if (assetId == 0L) 0 else assetId,
                name = state.name,
                brand = state.brand,
                model = state.model,
                categoryId = state.categoryId,
                price = price,
                purchaseDate = state.purchaseDate,
                purchaseChannel = state.purchaseChannel,
                status = state.status,
                locationId = state.locationId,
                specs = state.specs,
                serialNumber = state.serialNumber,
                notes = state.notes,
                isVirtual = state.isVirtual,
                expiryDate = state.expiryDate,
                photoPath = state.photoPath
            )

            if (assetId == 0L) {
                assetRepository.insert(asset)
            } else {
                assetRepository.update(asset)
            }
            _editState.value = _editState.value.copy(isSaving = false, saved = true)
        }
    }

    fun deleteAsset(asset: AssetEntity) {
        viewModelScope.launch {
            assetRepository.delete(asset)
        }
    }

    fun sellAsset(asset: AssetEntity) {
        viewModelScope.launch {
            assetRepository.sell(asset)
        }
    }

    fun discardAsset(asset: AssetEntity) {
        viewModelScope.launch {
            assetRepository.discard(asset)
        }
    }

    fun reloadList() {
        loadAssets()
    }
}
