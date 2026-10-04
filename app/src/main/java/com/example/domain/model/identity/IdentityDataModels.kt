package com.example.domain.model.identity

import com.example.core.common.UserRole
import com.example.core.common.UserStatus

/**
 * Enterprise Digital Identity & Account Management Models.
 */

data class ExpandedUserProfile(
    val uid: String = "",
    val fullName: String = "",
    val universityId: String = "",
    val employeeId: String = "",
    val email: String = "",
    val phone: String = "",
    val department: String = "",
    val faculty: String = "",
    val semester: String = "",
    val section: String = "",
    val designation: String = "",
    val role: UserRole = UserRole.STUDENT,
    val photoUrl: String = "",
    val bloodGroup: String = "O+",
    val emergencyContact: String = "",
    val address: String = "",
    val gender: String = "Unspecified",
    val dateOfBirth: String = "",
    val nationality: String = "Bangladeshi",
    val enrollmentYear: String = "2024",
    val biography: String = "",
    val githubUrl: String = "",
    val linkedinUrl: String = "",
    val status: UserStatus = UserStatus.ACTIVE,
    val isEmailVerified: Boolean = true,
    val isPhoneVerified: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class DigitalIdStatus {
    VERIFIED_ACTIVE, EXPIRED, SUSPENDED, PENDING_APPROVAL
}

data class DigitalIdCard(
    val uid: String,
    val fullName: String,
    val universityId: String,
    val employeeId: String = "",
    val department: String,
    val faculty: String,
    val role: UserRole,
    val designation: String = "",
    val photoUrl: String,
    val bloodGroup: String,
    val emergencyContact: String,
    val issueDate: String,
    val expiryDate: String,
    val digitalSignatureToken: String,
    val status: DigitalIdStatus = DigitalIdStatus.VERIFIED_ACTIVE,
    val qrTokenPayload: String = ""
)

data class QrPassVerificationResult(
    val status: DigitalIdStatus,
    val scannedUid: String,
    val scannedName: String,
    val scannedUniversityId: String,
    val scannedRole: String,
    val scannedDepartment: String,
    val scannedPhotoUrl: String,
    val verificationMessage: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class UserDevice(
    val deviceId: String,
    val deviceName: String,
    val androidVersion: String,
    val ipAddress: String,
    val lastLoginTime: Long,
    val location: String = "Dhaka Campus, Bangladesh",
    val isCurrentDevice: Boolean = false
)

data class LoginHistoryItem(
    val logId: String,
    val loginTime: Long,
    val logoutTime: Long? = null,
    val deviceName: String,
    val ipAddress: String,
    val platform: String = "Android App",
    val status: String = "SUCCESS"
)

enum class VisibilityOption {
    PUBLIC, CAMPUS_ONLY, PRIVATE
}

data class PrivacySettings(
    val profileVisibility: VisibilityOption = VisibilityOption.CAMPUS_ONLY,
    val phoneVisibility: VisibilityOption = VisibilityOption.PRIVATE,
    val emailVisibility: VisibilityOption = VisibilityOption.CAMPUS_ONLY,
    val searchVisibility: VisibilityOption = VisibilityOption.PUBLIC
)

enum class AppThemeMode {
    SYSTEM, LIGHT, DARK
}

enum class AppLanguage {
    ENGLISH, BANGLA
}

data class UserSettings(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val dynamicColorsEnabled: Boolean = true,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val notificationsEnabled: Boolean = true,
    val biometricEnabled: Boolean = true,
    val pinLockEnabled: Boolean = false,
    val pinCode: String = "",
    val defaultDashboard: String = "Student Dashboard",
    val autoDownloadMediaOnWifi: Boolean = true,
    val fontSizeScale: Float = 1.0f
)

data class AccountActivityItem(
    val activityId: String,
    val title: String,
    val description: String,
    val timestamp: Long,
    val category: String // SECURITY, PROFILE, DEVICE
)
