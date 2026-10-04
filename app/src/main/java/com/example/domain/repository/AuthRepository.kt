package com.example.domain.repository

import com.example.core.common.Resource
import com.example.core.common.UserRole
import com.example.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Clean domain repository contract for authentication and user management.
 */
interface AuthRepository {
    suspend fun loginWithEmail(email: String, password: String, rememberMe: Boolean): Resource<UserProfile>
    suspend fun registerWithEmail(userProfile: UserProfile, password: String): Resource<UserProfile>
    suspend fun loginWithGoogle(idToken: String, rememberMe: Boolean): Resource<UserProfile>
    suspend fun sendPasswordResetEmail(email: String): Resource<Unit>
    suspend fun sendEmailVerification(): Resource<Unit>
    suspend fun checkEmailVerificationStatus(): Resource<Boolean>
    suspend fun getCurrentUserProfile(): Resource<UserProfile?>
    suspend fun logout(): Resource<Unit>
    fun observeSavedUserRole(): Flow<UserRole>
    fun observeIsLoggedIn(): Flow<Boolean>
    fun observeIsOnboardingCompleted(): Flow<Boolean>
    suspend fun setOnboardingCompleted(completed: Boolean)
}
