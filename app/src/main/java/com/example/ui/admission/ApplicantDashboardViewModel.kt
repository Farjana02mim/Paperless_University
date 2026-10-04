package com.example.ui.admission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.admission.*
import com.example.domain.repository.admission.AdmissionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ApplicantDashboardUiState(
    val userApplications: List<AdmissionApplication> = emptyList(),
    val selectedApplication: AdmissionApplication? = null,
    val admitCard: AdmitCard? = null,
    val activeSession: AdmissionSession? = null,
    val isLoading: Boolean = false,
    val userMessage: String? = null
)

class ApplicantDashboardViewModel(
    private val repository: AdmissionRepository,
    private val applicantId: String = "user_applicant_01"
) : ViewModel() {

    private val _uiState = MutableStateFlow(ApplicantDashboardUiState())
    val uiState: StateFlow<ApplicantDashboardUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getUserApplications(applicantId).collect { apps ->
                _uiState.update { state ->
                    val selected = state.selectedApplication ?: apps.firstOrNull()
                    state.copy(userApplications = apps, selectedApplication = selected)
                }
                if (apps.isNotEmpty()) {
                    val targetApp = apps.first()
                    loadAdmitCard(targetApp.applicationId)
                }
            }
        }

        viewModelScope.launch {
            repository.getActiveSession().collect { session ->
                _uiState.update { it.copy(activeSession = session) }
            }
        }
    }

    fun selectApplication(app: AdmissionApplication) {
        _uiState.update { it.copy(selectedApplication = app) }
        loadAdmitCard(app.applicationId)
    }

    private fun loadAdmitCard(applicationId: String) {
        viewModelScope.launch {
            repository.generateAdmitCard(applicationId)
            repository.getAdmitCard(applicationId).collect { card ->
                _uiState.update { it.copy(admitCard = card) }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
}
