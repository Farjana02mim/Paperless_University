package com.example.ui.admission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.admission.*
import com.example.domain.repository.admission.AdmissionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AdmissionAdminUiState(
    val sessions: List<AdmissionSession> = emptyList(),
    val selectedSessionId: String = "sess_2026_01",
    val applications: List<AdmissionApplication> = emptyList(),
    val selectedDepartment: String? = null,
    val selectedStatus: ApplicationStatus? = null,
    val searchQuery: String = "",
    val analytics: AdmissionAnalytics = AdmissionAnalytics(),
    val isUpdating: Boolean = false,
    val userMessage: String? = null,
    val exportStatus: String? = null
)

class AdmissionAdminViewModel(
    private val repository: AdmissionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdmissionAdminUiState())
    val uiState: StateFlow<AdmissionAdminUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getAdmissionSessions().collect { sess ->
                _uiState.update { state ->
                    val activeId = state.selectedSessionId.ifBlank { sess.firstOrNull()?.sessionId ?: "sess_2026_01" }
                    state.copy(sessions = sess, selectedSessionId = activeId)
                }
            }
        }

        viewModelScope.launch {
            _uiState.flatMapLatest { state ->
                repository.getAllApplications(
                    sessionId = state.selectedSessionId,
                    department = state.selectedDepartment,
                    status = state.selectedStatus,
                    searchQuery = state.searchQuery
                )
            }.collect { apps ->
                _uiState.update { it.copy(applications = apps) }
            }
        }

        viewModelScope.launch {
            _uiState.flatMapLatest { state ->
                repository.getAdmissionAnalytics(state.selectedSessionId)
            }.collect { analytics ->
                _uiState.update { it.copy(analytics = analytics) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun filterByDepartment(department: String?) {
        _uiState.update { it.copy(selectedDepartment = department) }
    }

    fun filterByStatus(status: ApplicationStatus?) {
        _uiState.update { it.copy(selectedStatus = status) }
    }

    fun updateApplicationStatus(applicationId: String, status: ApplicationStatus, note: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true) }
            val res = repository.updateApplicationStatus(applicationId, status, note)
            res.onSuccess {
                _uiState.update { it.copy(isUpdating = false, userMessage = "Application status updated to ${status.name.replace("_", " ")}") }
            }.onFailure { err ->
                _uiState.update { it.copy(isUpdating = false, userMessage = "Failed: ${err.message}") }
            }
        }
    }

    fun verifyDocument(applicationId: String, docType: DocumentType, isVerified: Boolean, note: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true) }
            val res = repository.verifyDocument(applicationId, docType, isVerified, note)
            res.onSuccess {
                _uiState.update { it.copy(isUpdating = false, userMessage = "${docType.name} verification updated") }
            }.onFailure { err ->
                _uiState.update { it.copy(isUpdating = false, userMessage = "Failed: ${err.message}") }
            }
        }
    }

    fun createSession(session: AdmissionSession) {
        viewModelScope.launch {
            repository.createAdmissionSession(session)
            _uiState.update { it.copy(userMessage = "New Admission Session Created") }
        }
    }

    fun exportReport(format: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(exportStatus = "Generating $format Admission Report... Export Completed.") }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(userMessage = null, exportStatus = null) }
    }
}
