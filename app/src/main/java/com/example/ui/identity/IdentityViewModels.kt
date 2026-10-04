package com.example.ui.identity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.identity.*
import com.example.domain.repository.identity.IdentityRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for Profile viewing and editing.
 */
class UserProfileViewModel(
    private val identityRepository: IdentityRepository,
    private val userUid: String = "STD-2024-8842"
) : ViewModel() {

    val profile: StateFlow<ExpandedUserProfile> = identityRepository.getUserProfile(userUid)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ExpandedUserProfile())

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _updateMessage = MutableStateFlow<String?>(null)
    val updateMessage: StateFlow<String?> = _updateMessage.asStateFlow()

    fun toggleEditMode(editing: Boolean) {
        _isEditing.value = editing
    }

    fun saveProfile(updated: ExpandedUserProfile) {
        viewModelScope.launch {
            val result = identityRepository.updateProfile(updated)
            if (result.isSuccess) {
                _isEditing.value = false
                _updateMessage.value = "Profile updated successfully!"
            } else {
                _updateMessage.value = "Failed to save profile changes."
            }
        }
    }

    fun updatePhotoUrl(newPhotoUrl: String) {
        viewModelScope.launch {
            val current = profile.value
            val updated = current.copy(photoUrl = newPhotoUrl)
            identityRepository.updateProfile(updated)
            _updateMessage.value = "Profile picture updated!"
        }
    }

    fun clearMessage() {
        _updateMessage.value = null
    }
}

/**
 * ViewModel for Digital University ID Card & Staff QR Verification.
 */
class DigitalIdViewModel(
    private val identityRepository: IdentityRepository,
    private val userUid: String = "STD-2024-8842"
) : ViewModel() {

    val idCard: StateFlow<DigitalIdCard> = identityRepository.getDigitalIdCard(userUid)
        .stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000),
            DigitalIdCard(
                uid = "2024-3-60-042",
                fullName = "Tanvir Ahmed",
                universityId = "2024-3-60-042",
                department = "Computer Science & Engineering",
                faculty = "Faculty of Science & Engineering",
                role = com.example.core.common.UserRole.STUDENT,
                photoUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500",
                bloodGroup = "O+",
                emergencyContact = "+880 1819-000111",
                issueDate = "01 Jan 2024",
                expiryDate = "31 Dec 2025",
                digitalSignatureToken = "SIG_PU_9921_X78A"
            )
        )

    private val _isFlipped = MutableStateFlow(false)
    val isFlipped: StateFlow<Boolean> = _isFlipped.asStateFlow()

    private val _verificationResult = MutableStateFlow<QrPassVerificationResult?>(null)
    val verificationResult: StateFlow<QrPassVerificationResult?> = _verificationResult.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    fun flipCard() {
        _isFlipped.value = !_isFlipped.value
    }

    fun refreshQrPass() {
        viewModelScope.launch {
            val newToken = identityRepository.generateQrPassToken(userUid)
            val current = idCard.value
            // Update QR payload locally
            val updated = current.copy(qrTokenPayload = newToken)
            identityRepository.updateProfile(
                ExpandedUserProfile(uid = userUid, fullName = updated.fullName)
            )
        }
    }

    fun verifyQrCode(scannedPayload: String) {
        viewModelScope.launch {
            _isScanning.value = true
            val result = identityRepository.verifyScannedQrPass(scannedPayload)
            _verificationResult.value = result
            _isScanning.value = false
        }
    }

    fun clearVerificationResult() {
        _verificationResult.value = null
    }
}

/**
 * ViewModel for Security, Devices, & Login History.
 */
class SecurityAndDeviceViewModel(
    private val identityRepository: IdentityRepository
) : ViewModel() {

    val devices: StateFlow<List<UserDevice>> = identityRepository.getUserDevices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val loginHistory: StateFlow<List<LoginHistoryItem>> = identityRepository.getLoginHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val accountActivities: StateFlow<List<AccountActivityItem>> = identityRepository.getAccountActivity()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _securityNotice = MutableStateFlow<String?>(null)
    val securityNotice: StateFlow<String?> = _securityNotice.asStateFlow()

    fun revokeDevice(deviceId: String) {
        viewModelScope.launch {
            identityRepository.revokeDevice(deviceId)
            _securityNotice.value = "Device session revoked successfully."
        }
    }

    fun logoutOtherDevices() {
        viewModelScope.launch {
            identityRepository.logoutAllOtherDevices()
            _securityNotice.value = "All other sessions have been logged out."
        }
    }

    fun clearNotice() {
        _securityNotice.value = null
    }
}

/**
 * ViewModel for App & Privacy Settings.
 */
class SettingsViewModel(
    private val identityRepository: IdentityRepository,
    private val userUid: String = "STD-2024-8842"
) : ViewModel() {

    val userSettings: StateFlow<UserSettings> = identityRepository.getUserSettings(userUid)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

    val privacySettings: StateFlow<PrivacySettings> = identityRepository.getPrivacySettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PrivacySettings())

    fun updateThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            val current = userSettings.value
            identityRepository.updateUserSettings(userUid, current.copy(themeMode = mode))
        }
    }

    fun updateLanguage(lang: AppLanguage) {
        viewModelScope.launch {
            val current = userSettings.value
            identityRepository.updateUserSettings(userUid, current.copy(language = lang))
        }
    }

    fun toggleBiometric(enabled: Boolean) {
        viewModelScope.launch {
            val current = userSettings.value
            identityRepository.updateUserSettings(userUid, current.copy(biometricEnabled = enabled))
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            val current = userSettings.value
            identityRepository.updateUserSettings(userUid, current.copy(notificationsEnabled = enabled))
        }
    }

    fun updatePrivacy(newPrivacy: PrivacySettings) {
        viewModelScope.launch {
            identityRepository.updatePrivacySettings(newPrivacy)
        }
    }
}
