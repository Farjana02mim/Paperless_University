package com.example.ui.admission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.admission.*
import com.example.domain.repository.admission.AdmissionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MeritListUiState(
    val selectedSessionId: String = "sess_2026_01",
    val selectedDepartment: String = "Computer Science & Engineering",
    val sscWeight: Double = 0.3,
    val hscWeight: Double = 0.4,
    val testScoreWeight: Double = 0.3,
    val quotaBonus: Double = 2.0,
    val meritList: MeritList? = null,
    val isCalculating: Boolean = false,
    val isPublishing: Boolean = false,
    val filterQuota: QuotaType? = null,
    val userMessage: String? = null
)

class MeritListViewModel(
    private val repository: AdmissionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MeritListUiState())
    val uiState: StateFlow<MeritListUiState> = _uiState.asStateFlow()

    init {
        loadMeritList()
    }

    fun loadMeritList() {
        viewModelScope.launch {
            repository.getMeritList(_uiState.value.selectedSessionId, _uiState.value.selectedDepartment).collect { list ->
                if (list != null) {
                    _uiState.update { it.copy(meritList = list) }
                } else {
                    calculateMeritList()
                }
            }
        }
    }

    fun calculateMeritList() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCalculating = true) }
            val res = repository.calculateMeritList(
                sessionId = _uiState.value.selectedSessionId,
                department = _uiState.value.selectedDepartment,
                sscWeight = _uiState.value.sscWeight,
                hscWeight = _uiState.value.hscWeight,
                testScoreWeight = _uiState.value.testScoreWeight,
                quotaBonus = _uiState.value.quotaBonus
            )
            res.onSuccess { list ->
                _uiState.update { it.copy(meritList = list, isCalculating = false, userMessage = "Merit scores generated using custom weights") }
            }.onFailure { err ->
                _uiState.update { it.copy(isCalculating = false, userMessage = "Calculation error: ${err.message}") }
            }
        }
    }

    fun updateWeights(ssc: Double, hsc: Double, test: Double, bonus: Double) {
        _uiState.update { it.copy(sscWeight = ssc, hscWeight = hsc, testScoreWeight = test, quotaBonus = bonus) }
        calculateMeritList()
    }

    fun selectDepartment(dept: String) {
        _uiState.update { it.copy(selectedDepartment = dept) }
        loadMeritList()
    }

    fun publishMeritList() {
        viewModelScope.launch {
            val list = _uiState.value.meritList ?: return@launch
            _uiState.update { it.copy(isPublishing = true) }
            val res = repository.publishMeritList(list)
            res.onSuccess {
                _uiState.update { it.copy(isPublishing = false, userMessage = "Merit list published! Applicant statuses updated.") }
            }.onFailure { err ->
                _uiState.update { it.copy(isPublishing = false, userMessage = "Publishing failed: ${err.message}") }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
}
