package com.zichan.app.ui.asset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zichan.app.data.entity.AssetEntity
import com.zichan.app.data.entity.CategoryEntity
import com.zichan.app.data.entity.LocationEntity
import com.zichan.app.data.entity.PersonEntity
import com.zichan.app.data.repository.AssetRepository
import com.zichan.app.data.repository.CategoryRepository
import com.zichan.app.data.repository.LendRecordRepository
import com.zichan.app.data.repository.LocationRepository
import com.zichan.app.data.repository.PersonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AssetListUiState(
    val assets: List<AssetEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val locations: List<LocationEntity> = emptyList(),
    val deadlines: Map<Long, Long> = emptyMap(), // assetId -> expectedReturnDate
    val searchKeyword: String = "",
    val filterCategoryId: Long? = null,
    val filterStatuses: Set<String> = emptySet(),
    val isLoading: Boolean = true
)

data class AssetEditUiState(
    val name: String = "",
    val brand: String = "",
    val model: String = "",
    val categoryId: Long? = null,
    val customCategory: String = "",
    val price: String = "",
    val purchaseDate: Long? = null,
    val purchaseChannel: String = "",
    val status: String = "使用中",
    val lenderId: Long? = null,
    val locationId: Long? = null,
    val customLocation: String = "",
    val specs: String = "",
    val serialNumber: String = "",
    val notes: String = "",
    val isVirtual: Boolean = false,
    val expiryDate: Long? = null,
    val photoPath: String? = null,
    val categories: List<CategoryEntity> = emptyList(),
    val locations: List<LocationEntity> = emptyList(),
    val persons: List<PersonEntity> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val saved: Boolean = false
)

@HiltViewModel
class AssetViewModel @Inject constructor(
    private val assetRepository: AssetRepository,
    private val categoryRepository: CategoryRepository,
    private val locationRepository: LocationRepository,
    private val personRepository: PersonRepository,
    private val lendRecordRepository: LendRecordRepository
) : ViewModel() {

    private val _listState = MutableStateFlow(AssetListUiState())
    val listState: StateFlow<AssetListUiState> = _listState.asStateFlow()

    companion object {
        private const val OTHER_CATEGORY_ID = 10L
        private const val OTHER_LOCATION_ID = 6L
    }

    private val _editState = MutableStateFlow(AssetEditUiState())
    val editState: StateFlow<AssetEditUiState> = _editState.asStateFlow()

    init {
        loadCategories()
        loadLocations()
        loadPersons()
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

    private fun loadPersons() {
        viewModelScope.launch {
            personRepository.getAll().collect { persons ->
                _editState.value = _editState.value.copy(persons = persons)
            }
        }
    }

    fun loadAssets() {
        viewModelScope.launch {
            assetRepository.getAll().collect { assets ->
                val records = lendRecordRepository.getByStatus("借用中").first()
                val deadlines = records.associate { it.assetId to (it.expectedReturnDate ?: 0L) }
                _listState.value = _listState.value.copy(
                    assets = assets,
                    deadlines = deadlines,
                    isLoading = false
                )
            }
        }
    }

    suspend fun getBorrowerName(assetId: Long): String? {
        val record = lendRecordRepository.getActiveByAssetId(assetId) ?: return null
        val personId = record.personId ?: return null
        return personRepository.getById(personId)?.name
    }

    suspend fun getReturnDeadline(assetId: Long): Long? {
        return lendRecordRepository.getActiveByAssetId(assetId)?.expectedReturnDate
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

    fun toggleFilterStatus(status: String) {
        val current = _listState.value.filterStatuses
        val updated = if (status in current) current - status else current + status
        applyFilter(_listState.value.filterCategoryId, updated)
    }

    fun clearFilters() {
        applyFilter(null, emptySet())
    }

    private fun applyFilter(categoryId: Long?, statuses: Set<String>) {
        _listState.value = _listState.value.copy(filterCategoryId = categoryId, filterStatuses = statuses)
        viewModelScope.launch {
            assetRepository.filter(
                keyword = _listState.value.searchKeyword.ifBlank { null },
                categoryId = categoryId,
                status = if (statuses.size == 1) statuses.first() else null,
                minPrice = null,
                maxPrice = null
            ).collect { assets ->
                val filtered = if (statuses.size > 1) {
                    assets.filter { it.status in statuses }
                } else assets
                _listState.value = _listState.value.copy(assets = filtered)
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
                    val lenderId = if (asset.status == "已借出") {
                        lendRecordRepository.getActiveByAssetId(assetId)?.personId
                    } else null

                    _editState.value = AssetEditUiState(
                        name = asset.name,
                        brand = asset.brand,
                        model = asset.model,
                        categoryId = asset.categoryId,
                        price = if (asset.price > 0) asset.price.toString() else "",
                        purchaseDate = asset.purchaseDate,
                        purchaseChannel = asset.purchaseChannel,
                        status = asset.status,
                        lenderId = lenderId,
                        locationId = asset.locationId,
                        specs = asset.specs,
                        serialNumber = asset.serialNumber,
                        notes = asset.notes,
                        isVirtual = asset.isVirtual,
                        expiryDate = asset.expiryDate,
                        photoPath = asset.photoPath,
                        categories = _editState.value.categories,
                        locations = _editState.value.locations,
                        persons = _editState.value.persons,
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

            // Handle custom category
            val finalCategoryId: Long? = if (state.categoryId == OTHER_CATEGORY_ID &&
                state.customCategory.isNotBlank()
            ) {
                val newId = categoryRepository.insertOne(
                    CategoryEntity(name = state.customCategory, icon = "more_horiz")
                )
                newId
            } else state.categoryId

            // Handle custom location
            val finalLocationId: Long? = if (state.locationId == OTHER_LOCATION_ID &&
                state.customLocation.isNotBlank()
            ) {
                val newId = locationRepository.insertOne(
                    LocationEntity(name = state.customLocation)
                )
                newId
            } else state.locationId

            val asset = AssetEntity(
                id = if (assetId == 0L) 0 else assetId,
                name = state.name,
                brand = state.brand,
                model = state.model,
                categoryId = finalCategoryId,
                price = price,
                purchaseDate = state.purchaseDate,
                purchaseChannel = state.purchaseChannel,
                status = state.status,
                locationId = finalLocationId,
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

    fun deleteAssets(ids: Set<Long>) {
        viewModelScope.launch {
            ids.forEach { id ->
                assetRepository.getById(id)?.let { assetRepository.delete(it) }
            }
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

    fun updatePhoto(asset: AssetEntity, path: String) {
        viewModelScope.launch {
            assetRepository.update(asset.copy(photoPath = path))
            loadAssets()
        }
    }

    fun deletePhoto(asset: AssetEntity) {
        viewModelScope.launch {
            assetRepository.update(asset.copy(photoPath = null))
            loadAssets()
        }
    }

    fun updateStatus(asset: AssetEntity, status: String) {
        viewModelScope.launch {
            assetRepository.update(asset.copy(status = status))
            loadAssets()
        }
    }

    fun addPerson(person: PersonEntity) {
        viewModelScope.launch {
            val id = personRepository.insert(person)
            _editState.value = _editState.value.copy(lenderId = id)
        }
    }
}
