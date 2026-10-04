package com.example.ui.auth.emailverification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.Resource
import com.example.core.common.UserRole
import com.example.core.datastore.DataStoreManager
import com.example.domain.usecase.CheckEmailVerificationUseCase
import com.example.domain.usecase.LogoutUseCase
import com.example.domain.usecase.SendEmailVerificationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class EmailVerificationUiState(
    val email: String = "",
    val isVerified: Boolean = false,
    val isLoading: Boolean = false,
    val message: String? = null,
    val userRole: UserRole = UserRole.STUDENT
)

class EmailVerificationViewModel(
    private val checkVerificationUseCase: CheckEmailVerificationUseCase,
    private val sendVerificationUseCase: SendEmailVerificationUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmailVerificationUiState())
    val uiState: StateFlow<EmailVerificationUiState> = _uiState.asStateFlow()

    init {
        loadUserSession()
    }

    private fun loadUserSession() {
        viewModelScope.launch {
            val email = dataStoreManager.savedUserEmail.first() ?: "user@university.edu"
            val role = dataStoreManager.savedUserRole.first()
            _uiState.value = _uiState.value.copy(email = email, userRole = role)
        }
    }

    fun checkStatus() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, message = null)
            val result = checkVerificationUseCase()
            when (result) {
                is Resource.Success -> {
                    if (result.data) {
                        _uiState.value = _uiState.value.copy(isLoading = false, isVerified = true)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            message = "Email is not verified yet. Please check your inbox or spam folder."
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = result.message
                    )
                }
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun resendEmail() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, message = null)
            val result = sendVerificationUseCase()
            when (result) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = "Verification email resent successfully. Check your inbox!"
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = result.message
                    )
                }
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
            onLoggedOut()
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}
