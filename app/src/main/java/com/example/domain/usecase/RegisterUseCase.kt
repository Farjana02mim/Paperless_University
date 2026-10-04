package com.example.domain.usecase

import com.example.core.common.AuthValidator
import com.example.core.common.Resource
import com.example.core.common.UserRole
import com.example.domain.model.UserProfile
import com.example.domain.repository.AuthRepository

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        userProfile: UserProfile,
        password: String,
        confirmPassword: String,
        acceptedTerms: Boolean
    ): Resource<UserProfile> {
        if (userProfile.role == UserRole.ADMIN) {
            return Resource.Error("Administrator accounts cannot be self-registered.")
        }

        val nameVal = AuthValidator.validateFullName(userProfile.fullName)
        if (!nameVal.isValid) return Resource.Error(nameVal.errorMessage!!)

        val uniIdVal = AuthValidator.validateUniversityId(userProfile.universityId)
        if (!uniIdVal.isValid) return Resource.Error(uniIdVal.errorMessage!!)

        val emailVal = AuthValidator.validateEmail(userProfile.email)
        if (!emailVal.isValid) return Resource.Error(emailVal.errorMessage!!)

        val passVal = AuthValidator.validatePassword(password)
        if (!passVal.isValid) return Resource.Error(passVal.errorMessage!!)

        val confirmVal = AuthValidator.validateConfirmPassword(password, confirmPassword)
        if (!confirmVal.isValid) return Resource.Error(confirmVal.errorMessage!!)

        val phoneVal = AuthValidator.validatePhone(userProfile.phone)
        if (!phoneVal.isValid) return Resource.Error(phoneVal.errorMessage!!)

        val deptVal = AuthValidator.validateDepartment(userProfile.department)
        if (!deptVal.isValid) return Resource.Error(deptVal.errorMessage!!)

        val facVal = AuthValidator.validateFaculty(userProfile.faculty)
        if (!facVal.isValid) return Resource.Error(facVal.errorMessage!!)

        val semOrDesValue = if (userProfile.role == UserRole.STUDENT) userProfile.semester else userProfile.designation
        val semVal = AuthValidator.validateSemesterOrDesignation(semOrDesValue, userProfile.role)
        if (!semVal.isValid) return Resource.Error(semVal.errorMessage!!)

        val termsVal = AuthValidator.validateTermsAccepted(acceptedTerms)
        if (!termsVal.isValid) return Resource.Error(termsVal.errorMessage!!)

        return repository.registerWithEmail(userProfile, password)
    }
}
