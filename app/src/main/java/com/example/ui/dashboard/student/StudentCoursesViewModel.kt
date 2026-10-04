package com.example.ui.dashboard.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.Resource
import com.example.domain.model.CourseItem
import com.example.domain.usecase.GetStudentCoursesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudentCoursesUiState(
    val isLoading: Boolean = true,
    val courses: List<CourseItem> = emptyList(),
    val error: String? = null
)

class StudentCoursesViewModel(
    private val getStudentCoursesUseCase: GetStudentCoursesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentCoursesUiState())
    val uiState: StateFlow<StudentCoursesUiState> = _uiState.asStateFlow()

    init {
        loadCourses()
    }

    fun loadCourses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = getStudentCoursesUseCase()) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false, courses = result.data ?: emptyList()) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }
}
