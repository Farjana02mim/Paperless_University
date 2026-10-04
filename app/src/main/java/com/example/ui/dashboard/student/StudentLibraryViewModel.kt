package com.example.ui.dashboard.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.Resource
import com.example.domain.model.LibraryBook
import com.example.domain.usecase.GetLibraryBooksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudentLibraryUiState(
    val isLoading: Boolean = true,
    val books: List<LibraryBook> = emptyList(),
    val error: String? = null,
    val searchQuery: String = ""
)

class StudentLibraryViewModel(
    private val getLibraryBooksUseCase: GetLibraryBooksUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentLibraryUiState())
    val uiState: StateFlow<StudentLibraryUiState> = _uiState.asStateFlow()

    init {
        loadLibraryBooks()
    }

    fun loadLibraryBooks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = getLibraryBooksUseCase()) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false, books = result.data ?: emptyList()) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
}
