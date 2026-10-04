package com.example.ui.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.Resource
import com.example.core.common.UserRole
import com.example.domain.model.UserProfile
import com.example.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val fullName: String = "",
    val universityId: String = "",
    val email: String = "",
    val phone: String = "",
    val department: String = "Computer Science & Engineering",
    val faculty: String = "Faculty of Engineering & Tech",
    val semester: String = "1st Semester",
    val designation: String = "Assistant Professor",
    val password: String = "",
    val confirmPassword: String = "",
    val role: UserRole = UserRole.STUDENT,
    val photoUrl: String = "",
    val acceptedTerms: Boolean = false,
    val isLoading: Boolean = false,
    val generalError: String? = null,
    val isSuccess: Boolean = false
)

class RegisterViewModel(private val registerUseCase: RegisterUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onFullNameChanged(value: String) { _uiState.value = _uiState.value.copy(fullName = value, generalError = null) }
    fun onUniversityIdChanged(value: String) { _uiState.value = _uiState.value.copy(universityId = value, generalError = null) }
    fun onEmailChanged(value: String) { _uiState.value = _uiState.value.copy(email = value, generalError = null) }
    fun onPhoneChanged(value: String) { _uiState.value = _uiState.value.copy(phone = value, generalError = null) }
    fun onDepartmentChanged(value: String) { _uiState.value = _uiState.value.copy(department = value, generalError = null) }
    fun onFacultyChanged(value: String) { _uiState.value = _uiState.value.copy(faculty = value, generalError = null) }
    fun onSemesterChanged(value: String) { _uiState.value = _uiState.value.copy(semester = value, generalError = null) }
    fun onDesignationChanged(value: String) { _uiState.value = _uiState.value.copy(designation = value, generalError = null) }
    fun onPasswordChanged(value: String) { _uiState.value = _uiState.value.copy(password = value, generalError = null) }
    fun onConfirmPasswordChanged(value: String) { _uiState.value = _uiState.value.copy(confirmPassword = value, generalError = null) }
    fun onRoleSelected(role: UserRole) {
        if (role != UserRole.ADMIN) {
            _uiState.value = _uiState.value.copy(role = role, generalError = null)
        }
    }
    fun onPhotoUrlChanged(url: String) { _uiState.value = _uiState.value.copy(photoUrl = url) }
    fun onAcceptedTermsToggled(accepted: Boolean) { _uiState.value = _uiState.value.copy(acceptedTerms = accepted, generalError = null) }

    fun register() {
        val s = _uiState.value
        val profile = UserProfile(
            fullName = s.fullName.trim(),
            universityId = s.universityId.trim(),
            email = s.email.trim(),
            phone = s.phone.trim(),
            department = s.department,
            faculty = s.faculty,
            semester = if (s.role == UserRole.STUDENT) s.semester else "",
            designation = if (s.role == UserRole.TEACHER) s.designation else "",
            role = s.role,
            photoUrl = s.photoUrl
        )

        viewModelScope.launch {
            _uiState.value = s.copy(isLoading = true, generalError = null)

            val result = registerUseCase(
                userProfile = profile,
                password = s.password,
                confirmPassword = s.confirmPassword,
                acceptedTerms = s.acceptedTerms
            )

            when (result) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, generalError = result.message)
                }
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(generalError = null)
    }
}
