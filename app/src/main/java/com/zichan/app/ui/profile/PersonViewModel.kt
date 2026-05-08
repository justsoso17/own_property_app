package com.zichan.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zichan.app.data.entity.PersonEntity
import com.zichan.app.data.repository.AssetRepository
import com.zichan.app.data.repository.LendRecordRepository
import com.zichan.app.data.repository.PersonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PersonWithAssets(
    val person: PersonEntity,
    val borrowedAssets: List<Pair<Long, String>> = emptyList() // assetId to name
)

data class PersonListUiState(
    val persons: List<PersonEntity> = emptyList(),
    val borrowedMap: Map<Long, List<Pair<Long, String>>> = emptyMap(),
    val isLoading: Boolean = true
)

data class PersonEditUiState(
    val name: String = "",
    val relationship: String = "",
    val phone: String = "",
    val wechat: String = "",
    val notes: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val saved: Boolean = false
)

@HiltViewModel
class PersonViewModel @Inject constructor(
    private val repository: PersonRepository,
    private val lendRepository: LendRecordRepository,
    private val assetRepository: AssetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PersonListUiState())
    val uiState: StateFlow<PersonListUiState> = _uiState.asStateFlow()

    private val _editState = MutableStateFlow(PersonEditUiState())
    val editState: StateFlow<PersonEditUiState> = _editState.asStateFlow()

    init {
        loadPersons()
    }

    fun loadPersons() {
        viewModelScope.launch {
            repository.getAll().collect { persons ->
                val records = lendRepository.getByStatus("借用中").first()
                val allAssets = assetRepository.getAll().first()
                val nameMap = allAssets.associate { it.id to it.name }
                val map = mutableMapOf<Long, MutableList<Pair<Long, String>>>()
                records.forEach { r ->
                    r.personId?.let { pid ->
                        val assetName = nameMap[r.assetId] ?: "资产"
                        val list = map.getOrPut(pid) { mutableListOf() }
                        if (list.none { it.first == r.assetId }) {
                            list.add(r.assetId to assetName)
                        }
                    }
                }
                _uiState.value = PersonListUiState(
                    persons = persons,
                    borrowedMap = map,
                    isLoading = false
                )
            }
        }
    }

    fun loadPersonForEdit(personId: Long) {
        viewModelScope.launch {
            if (personId == 0L) {
                _editState.value = PersonEditUiState(isLoading = false)
            } else {
                repository.getById(personId)?.let { person ->
                    _editState.value = PersonEditUiState(
                        name = person.name,
                        relationship = person.relationship,
                        phone = person.phone,
                        wechat = person.wechat,
                        notes = person.notes,
                        isLoading = false
                    )
                } ?: run {
                    _editState.value = _editState.value.copy(isLoading = false)
                }
            }
        }
    }

    fun updateEditField(update: PersonEditUiState.() -> PersonEditUiState) {
        _editState.value = _editState.value.update()
    }

    fun savePerson(personId: Long) {
        val s = _editState.value
        if (s.name.isBlank()) return

        viewModelScope.launch {
            _editState.value = _editState.value.copy(isSaving = true)
            val person = PersonEntity(
                id = if (personId == 0L) 0 else personId,
                name = s.name,
                relationship = s.relationship,
                phone = s.phone,
                wechat = s.wechat,
                notes = s.notes
            )
            if (personId == 0L) {
                repository.insert(person)
            } else {
                repository.update(person)
            }
            _editState.value = _editState.value.copy(isSaving = false, saved = true)
        }
    }

    fun deletePerson(person: PersonEntity) {
        viewModelScope.launch { repository.delete(person) }
    }
}
