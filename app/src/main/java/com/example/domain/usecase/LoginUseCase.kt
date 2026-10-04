package com.example.domain.usecase

import com.example.core.common.AuthValidator
import com.example.core.common.Resource
import com.example.domain.model.UserProfile
import com.example.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        email: String,
        password: String,
        rememberMe: Boolean
    ): Resource<UserProfile> {
        val emailValidation = AuthValidator.validateEmail(email)
        if (!emailValidation.isValid) {
            return Resource.Error(emailValidation.errorMessage ?: "Invalid email")
        }

        if (password.isBlank()) {
            return Resource.Error("Password cannot be empty")
        }

        return repository.loginWithEmail(email.trim(), password, rememberMe)
    }
}
