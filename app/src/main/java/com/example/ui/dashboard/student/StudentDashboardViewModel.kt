package com.example.ui.dashboard.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.Resource
import com.example.domain.model.NoticeItem
import com.example.domain.model.StudentDashboardData
import com.example.domain.usecase.GetStudentDashboardUseCase
import com.example.domain.usecase.ScanAttendanceQrUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudentDashboardUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val data: StudentDashboardData = StudentDashboardData(),
    val searchQuery: String = "",
    val isSearchOpen: Boolean = false,
    val selectedNotice: NoticeItem? = null,
    val showDigitalIdModal: Boolean = false,
    val showQrScannerModal: Boolean = false,
    val activeQuickActionDialog: String? = null,
    val qrScanMessage: String? = null
)

class StudentDashboardViewModel(
    private val getStudentDashboardUseCase: GetStudentDashboardUseCase,
    private val scanAttendanceQrUseCase: ScanAttendanceQrUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentDashboardUiState())
    val uiState: StateFlow<StudentDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = getStudentDashboardUseCase()) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            data = result.data ?: StudentDashboardData()
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = result.message ?: "Failed to load dashboard data"
                        )
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun refreshDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            loadDashboardData()
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleSearchSheet(open: Boolean) {
        _uiState.update { it.copy(isSearchOpen = open) }
    }

    fun selectNotice(notice: NoticeItem?) {
        _uiState.update { it.copy(selectedNotice = notice) }
    }

    fun toggleDigitalIdModal(show: Boolean) {
        _uiState.update { it.copy(showDigitalIdModal = show) }
    }

    fun toggleQrScannerModal(show: Boolean) {
        _uiState.update { it.copy(showQrScannerModal = show, qrScanMessage = null) }
    }

    fun showQuickActionDialog(actionTitle: String?) {
        _uiState.update { it.copy(activeQuickActionDialog = actionTitle) }
    }

    fun scanQrCode(codePayload: String) {
        viewModelScope.launch {
            when (val result = scanAttendanceQrUseCase(codePayload)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(qrScanMessage = result.data)
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(qrScanMessage = "Error: " + result.message)
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }
}
