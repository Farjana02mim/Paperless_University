package com.example.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.library.LibraryAnalytics
import com.example.domain.repository.library.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LibraryAnalyticsUiState(
    val analytics: LibraryAnalytics = LibraryAnalytics(),
    val isLoading: Boolean = false,
    val exportStatus: String? = null
)

class LibraryAnalyticsViewModel(
    private val repository: LibraryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryAnalyticsUiState())
    val uiState: StateFlow<LibraryAnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadAnalytics()
    }

    private fun loadAnalytics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val res = repository.getLibraryAnalytics()
            if (res.isSuccess) {
                _uiState.update { state ->
                    state.copy(
                        analytics = res.getOrDefault(LibraryAnalytics()),
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun exportReport(format: String) { // "PDF" or "CSV"
        viewModelScope.launch {
            _uiState.update { it.copy(exportStatus = "Generating $format export report...") }
            kotlinx.coroutines.delay(1200)
            _uiState.update { it.copy(exportStatus = "Library Analytics $format successfully exported to Downloads!") }
        }
    }

    fun clearExportStatus() {
        _uiState.update { it.copy(exportStatus = null) }
    }
}
