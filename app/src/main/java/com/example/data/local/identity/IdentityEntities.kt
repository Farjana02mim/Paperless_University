package com.example.data.local.identity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_user_profiles")
data class UserProfileEntity(
    @PrimaryKey val uid: String,
    val fullName: String,
    val universityId: String,
    val employeeId: String,
    val email: String,
    val phone: String,
    val department: String,
    val faculty: String,
    val semester: String,
    val section: String,
    val designation: String,
    val role: String,
    val photoUrl: String,
    val bloodGroup: String,
    val emergencyContact: String,
    val address: String,
    val gender: String,
    val dateOfBirth: String,
    val nationality: String,
    val enrollmentYear: String,
    val biography: String,
    val githubUrl: String,
    val linkedinUrl: String,
    val status: String,
    val isEmailVerified: Boolean,
    val isPhoneVerified: Boolean,
    val updatedAt: Long
)

@Entity(tableName = "cached_digital_id_cards")
data class DigitalIdCardEntity(
    @PrimaryKey val uid: String,
    val fullName: String,
    val universityId: String,
    val employeeId: String,
    val department: String,
    val faculty: String,
    val role: String,
    val designation: String,
    val photoUrl: String,
    val bloodGroup: String,
    val emergencyContact: String,
    val issueDate: String,
    val expiryDate: String,
    val digitalSignatureToken: String,
    val status: String,
    val qrTokenPayload: String
)

@Entity(tableName = "cached_user_devices")
data class UserDeviceEntity(
    @PrimaryKey val deviceId: String,
    val deviceName: String,
    val androidVersion: String,
    val ipAddress: String,
    val lastLoginTime: Long,
    val location: String,
    val isCurrentDevice: Boolean
)

@Entity(tableName = "user_login_history")
data class LoginHistoryEntity(
    @PrimaryKey val logId: String,
    val loginTime: Long,
    val logoutTime: Long?,
    val deviceName: String,
    val ipAddress: String,
    val platform: String,
    val status: String
)

@Entity(tableName = "cached_user_settings")
data class UserSettingsEntity(
    @PrimaryKey val uid: String,
    val themeMode: String,
    val dynamicColorsEnabled: Boolean,
    val language: String,
    val notificationsEnabled: Boolean,
    val biometricEnabled: Boolean,
    val pinLockEnabled: Boolean,
    val pinCode: String,
    val defaultDashboard: String,
    val autoDownloadMediaOnWifi: Boolean,
    val fontSizeScale: Float
)
