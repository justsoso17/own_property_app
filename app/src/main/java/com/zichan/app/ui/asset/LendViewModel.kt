package com.zichan.app.ui.asset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zichan.app.data.entity.AssetEntity
import com.zichan.app.data.entity.LendRecordEntity
import com.zichan.app.data.entity.PersonEntity
import com.zichan.app.data.repository.AssetRepository
import com.zichan.app.data.repository.LendRecordRepository
import com.zichan.app.data.repository.PersonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LendUiState(
    val asset: AssetEntity? = null,
    val persons: List<PersonEntity> = emptyList(),
    val selectedPersonId: Long? = null,
    val returnDays: String = "7",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val saved: Boolean = false
)

@HiltViewModel
class LendViewModel @Inject constructor(
    private val assetRepository: AssetRepository,
    private val personRepository: PersonRepository,
    private val lendRepository: LendRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LendUiState())
    val uiState: StateFlow<LendUiState> = _uiState.asStateFlow()

    fun load(assetId: Long) {
        viewModelScope.launch {
            val asset = assetRepository.getById(assetId)
            _uiState.value = _uiState.value.copy(asset = asset, isLoading = false)
        }
        viewModelScope.launch {
            personRepository.getAll().collect { persons ->
                _uiState.value = _uiState.value.copy(persons = persons)
            }
        }
    }

    fun selectPerson(id: Long) {
        _uiState.value = _uiState.value.copy(selectedPersonId = id)
    }

    fun addPerson(person: PersonEntity) {
        viewModelScope.launch {
            val id = personRepository.insert(person)
            _uiState.value = _uiState.value.copy(selectedPersonId = id)
        }
    }

    fun setReturnDays(days: String) {
        _uiState.value = _uiState.value.copy(returnDays = days)
    }

    fun lend() {
        val state = _uiState.value
        val asset = state.asset ?: return
        val personId = state.selectedPersonId ?: return
        val days = state.returnDays.toIntOrNull() ?: 7

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            val now = System.currentTimeMillis()
            val record = LendRecordEntity(
                assetId = asset.id,
                personId = personId,
                lendDate = now,
                expectedReturnDate = now + days * 24 * 60 * 60 * 1000
            )
            lendRepository.lend(record, asset)
            _uiState.value = _uiState.value.copy(isSaving = false, saved = true)
        }
    }
}
