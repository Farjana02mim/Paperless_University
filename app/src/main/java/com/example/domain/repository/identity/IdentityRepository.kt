package com.example.domain.repository.identity

import com.example.domain.model.identity.*
import kotlinx.coroutines.flow.Flow

interface IdentityRepository {
    fun getUserProfile(uid: String): Flow<ExpandedUserProfile>
    suspend fun updateProfile(profile: ExpandedUserProfile): Result<Boolean>
    suspend fun uploadProfilePhoto(uid: String, photoUriString: String): Result<String>
    
    fun getDigitalIdCard(uid: String): Flow<DigitalIdCard>
    suspend fun generateQrPassToken(uid: String): String
    suspend fun verifyScannedQrPass(qrPayload: String): QrPassVerificationResult

    fun getUserDevices(): Flow<List<UserDevice>>
    suspend fun revokeDevice(deviceId: String): Result<Boolean>
    suspend fun logoutAllOtherDevices(): Result<Boolean>

    fun getLoginHistory(): Flow<List<LoginHistoryItem>>

    fun getUserSettings(uid: String): Flow<UserSettings>
    suspend fun updateUserSettings(uid: String, settings: UserSettings): Result<Boolean>

    fun getPrivacySettings(): Flow<PrivacySettings>
    suspend fun updatePrivacySettings(settings: PrivacySettings): Result<Boolean>
    
    fun getAccountActivity(): Flow<List<AccountActivityItem>>
}
