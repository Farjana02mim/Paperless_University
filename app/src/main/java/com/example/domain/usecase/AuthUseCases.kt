package com.example.domain.usecase

import com.example.core.common.AuthValidator
import com.example.core.common.Resource
import com.example.domain.repository.AuthRepository

class ForgotPasswordUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String): Resource<Unit> {
        val validation = AuthValidator.validateEmail(email)
        if (!validation.isValid) {
            return Resource.Error(validation.errorMessage ?: "Invalid email format")
        }
        return repository.sendPasswordResetEmail(email.trim())
    }
}

class SendEmailVerificationUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Resource<Unit> {
        return repository.sendEmailVerification()
    }
}

class CheckEmailVerificationUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Resource<Boolean> {
        return repository.checkEmailVerificationStatus()
    }
}

class LogoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Resource<Unit> {
        return repository.logout()
    }
}
