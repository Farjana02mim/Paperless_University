package com.example.data.repository.identity

import com.example.core.common.UserRole
import com.example.core.common.UserStatus
import com.example.data.local.identity.IdentityDao
import com.example.data.local.identity.*
import com.example.domain.model.identity.*
import com.example.domain.repository.identity.IdentityRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class IdentityRepositoryImpl(
    private val identityDao: IdentityDao,
    private val firestore: FirebaseFirestore? = null
) : IdentityRepository {

    override fun getUserProfile(uid: String): Flow<ExpandedUserProfile> {
        return identityDao.getUserProfile(uid).map { entity ->
            if (entity != null) {
                entity.toDomain()
            } else {
                // Fallback / Initial seed user profile
                val initial = ExpandedUserProfile(
                    uid = uid.ifEmpty { "STD-2024-8842" },
                    fullName = "Tanvir Ahmed",
                    universityId = "2024-3-60-042",
                    employeeId = "",
                    email = "tanvir.ahmed@student.university.edu.bd",
                    phone = "+880 1712-345678",
                    department = "Computer Science & Engineering",
                    faculty = "Faculty of Science & Engineering",
                    semester = "7th Semester",
                    section = "A",
                    designation = "Undergraduate Student",
                    role = UserRole.STUDENT,
                    photoUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500",
                    bloodGroup = "O+",
                    emergencyContact = "+880 1819-000111 (Father)",
                    address = "Plot 12, Road 5, Block B, Bashundhara R/A, Dhaka",
                    gender = "Male",
                    dateOfBirth = "15 August 2002",
                    nationality = "Bangladeshi",
                    enrollmentYear = "2021",
                    biography = "Passionately building smart campus solutions, AI integration, and paperless systems.",
                    githubUrl = "https://github.com/tanvir-ahmed",
                    linkedinUrl = "https://linkedin.com/in/tanvir-ahmed",
                    status = UserStatus.ACTIVE,
                    isEmailVerified = true,
                    isPhoneVerified = true
                )
                // Save to local cache
                identityDao.insertUserProfile(initial.toEntity())
                initial
            }
        }
    }

    override suspend fun updateProfile(profile: ExpandedUserProfile): Result<Boolean> {
        return try {
            identityDao.insertUserProfile(profile.toEntity())
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadProfilePhoto(uid: String, photoUriString: String): Result<String> {
        return try {
            // In local/offline mode, store photoUriString directly
            Result.success(photoUriString)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getDigitalIdCard(uid: String): Flow<DigitalIdCard> {
        return identityDao.getDigitalIdCard(uid).map { entity ->
            if (entity != null) {
                entity.toDomain()
            } else {
                val card = DigitalIdCard(
                    uid = uid.ifEmpty { "STD-2024-8842" },
                    fullName = "Tanvir Ahmed",
                    universityId = "2024-3-60-042",
                    employeeId = "",
                    department = "Computer Science & Engineering",
                    faculty = "Faculty of Science & Engineering",
                    role = UserRole.STUDENT,
                    designation = "B.Sc. in CSE",
                    photoUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500",
                    bloodGroup = "O+",
                    emergencyContact = "+880 1819-000111",
                    issueDate = "01 Jan 2024",
                    expiryDate = "31 Dec 2025",
                    digitalSignatureToken = "SIG_PU_9921_X78A",
                    status = DigitalIdStatus.VERIFIED_ACTIVE,
                    qrTokenPayload = "SMART_PASS|UID:2024-3-60-042|ROLE:STUDENT|EXPIRE:2025-12-31|SIG:VERIFIED"
                )
                identityDao.insertDigitalIdCard(card.toEntity())
                card
            }
        }
    }

    override suspend fun generateQrPassToken(uid: String): String {
        val timestamp = System.currentTimeMillis()
        return "SMART_PASS|UID:$uid|STAMP:$timestamp|SIG:SHA256_ACTIVE"
    }

    override suspend fun verifyScannedQrPass(qrPayload: String): QrPassVerificationResult {
        return if (qrPayload.contains("EXPIRED")) {
            QrPassVerificationResult(
                status = DigitalIdStatus.EXPIRED,
                scannedUid = "UNKNOWN",
                scannedName = "Expired Student Card",
                scannedUniversityId = "2021-1-10-001",
                scannedRole = "STUDENT",
                scannedDepartment = "Electrical & Electronic Engineering",
                scannedPhotoUrl = "",
                verificationMessage = "Card validity period expired on 31 Dec 2023."
            )
        } else if (qrPayload.contains("SUSPENDED")) {
            QrPassVerificationResult(
                status = DigitalIdStatus.SUSPENDED,
                scannedUid = "SUSPENDED_USER",
                scannedName = "Rafiqul Islam",
                scannedUniversityId = "2022-2-20-099",
                scannedRole = "STUDENT",
                scannedDepartment = "Business Administration",
                scannedPhotoUrl = "",
                verificationMessage = "Access restricted due to administrative disciplinary hold."
            )
        } else {
            QrPassVerificationResult(
                status = DigitalIdStatus.VERIFIED_ACTIVE,
                scannedUid = "2024-3-60-042",
                scannedName = "Tanvir Ahmed",
                scannedUniversityId = "2024-3-60-042",
                scannedRole = "STUDENT",
                scannedDepartment = "Computer Science & Engineering",
                scannedPhotoUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500",
                verificationMessage = "Identity verified. Campus Access Granted."
            )
        }
    }

    override fun getUserDevices(): Flow<List<UserDevice>> {
        return identityDao.getUserDevices().map { list ->
            if (list.isNotEmpty()) {
                list.map { it.toDomain() }
            } else {
                val seedDevices = listOf(
                    UserDevice("DEV_01", "Google Pixel 8 Pro", "Android 14 (API 34)", "103.120.201.44", System.currentTimeMillis(), "Dhaka Campus", isCurrentDevice = true),
                    UserDevice("DEV_02", "Samsung Galaxy Tab S9", "Android 13 (API 33)", "103.120.201.50", System.currentTimeMillis() - 86400000L, "Central Library", isCurrentDevice = false),
                    UserDevice("DEV_03", "ChromeOS Laptop", "Android 12 (DeX)", "192.168.1.102", System.currentTimeMillis() - 172800000L, "Dhanmondi, Dhaka", isCurrentDevice = false)
                )
                identityDao.insertUserDevices(seedDevices.map { it.toEntity() })
                seedDevices
            }
        }
    }

    override suspend fun revokeDevice(deviceId: String): Result<Boolean> {
        return try {
            identityDao.deleteDevice(deviceId)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logoutAllOtherDevices(): Result<Boolean> {
        return try {
            identityDao.deleteAllOtherDevices()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getLoginHistory(): Flow<List<LoginHistoryItem>> {
        return identityDao.getLoginHistory().map { list ->
            if (list.isNotEmpty()) {
                list.map { it.toDomain() }
            } else {
                val seed = listOf(
                    LoginHistoryItem("LOG_1", System.currentTimeMillis() - 1800000L, null, "Google Pixel 8 Pro", "103.120.201.44", "Android App", "SUCCESS"),
                    LoginHistoryItem("LOG_2", System.currentTimeMillis() - 86400000L, System.currentTimeMillis() - 80000000L, "Samsung Galaxy Tab S9", "103.120.201.50", "Android App", "SUCCESS"),
                    LoginHistoryItem("LOG_3", System.currentTimeMillis() - 259200000L, System.currentTimeMillis() - 250000000L, "Windows Web Portal", "118.179.42.12", "Web Browser", "SUCCESS")
                )
                identityDao.insertLoginHistory(seed.map { it.toEntity() })
                seed
            }
        }
    }

    override fun getUserSettings(uid: String): Flow<UserSettings> {
        return identityDao.getUserSettings(uid).map { entity ->
            if (entity != null) {
                entity.toDomain()
            } else {
                val defaultSettings = UserSettings()
                identityDao.insertUserSettings(defaultSettings.toEntity(uid.ifEmpty { "DEFAULT" }))
                defaultSettings
            }
        }
    }

    override suspend fun updateUserSettings(uid: String, settings: UserSettings): Result<Boolean> {
        return try {
            identityDao.insertUserSettings(settings.toEntity(uid.ifEmpty { "DEFAULT" }))
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getPrivacySettings(): Flow<PrivacySettings> {
        return flow {
            emit(PrivacySettings())
        }
    }

    override suspend fun updatePrivacySettings(settings: PrivacySettings): Result<Boolean> {
        return Result.success(true)
    }

    override fun getAccountActivity(): Flow<List<AccountActivityItem>> {
        return flow {
            emit(
                listOf(
                    AccountActivityItem("ACT_1", "Biometric Authentication Enabled", "Fingerprint login was activated for this device.", System.currentTimeMillis() - 3600000L, "SECURITY"),
                    AccountActivityItem("ACT_2", "Profile Photo Updated", "New digital avatar photo saved successfully.", System.currentTimeMillis() - 86400000L, "PROFILE"),
                    AccountActivityItem("ACT_3", "New Device Registered", "Google Pixel 8 Pro logged into campus account.", System.currentTimeMillis() - 172800000L, "DEVICE")
                )
            )
        }
    }

    // Mapping Extension Functions
    private fun UserProfileEntity.toDomain() = ExpandedUserProfile(
        uid = uid, fullName = fullName, universityId = universityId, employeeId = employeeId,
        email = email, phone = phone, department = department, faculty = faculty,
        semester = semester, section = section, designation = designation,
        role = UserRole.fromString(role), photoUrl = photoUrl, bloodGroup = bloodGroup,
        emergencyContact = emergencyContact, address = address, gender = gender,
        dateOfBirth = dateOfBirth, nationality = nationality, enrollmentYear = enrollmentYear,
        biography = biography, githubUrl = githubUrl, linkedinUrl = linkedinUrl,
        status = UserStatus.fromString(status), isEmailVerified = isEmailVerified,
        isPhoneVerified = isPhoneVerified, updatedAt = updatedAt
    )

    private fun ExpandedUserProfile.toEntity() = UserProfileEntity(
        uid = uid, fullName = fullName, universityId = universityId, employeeId = employeeId,
        email = email, phone = phone, department = department, faculty = faculty,
        semester = semester, section = section, designation = designation,
        role = role.name, photoUrl = photoUrl, bloodGroup = bloodGroup,
        emergencyContact = emergencyContact, address = address, gender = gender,
        dateOfBirth = dateOfBirth, nationality = nationality, enrollmentYear = enrollmentYear,
        biography = biography, githubUrl = githubUrl, linkedinUrl = linkedinUrl,
        status = status.name, isEmailVerified = isEmailVerified,
        isPhoneVerified = isPhoneVerified, updatedAt = System.currentTimeMillis()
    )

    private fun DigitalIdCardEntity.toDomain() = DigitalIdCard(
        uid = uid, fullName = fullName, universityId = universityId, employeeId = employeeId,
        department = department, faculty = faculty, role = UserRole.fromString(role),
        designation = designation, photoUrl = photoUrl, bloodGroup = bloodGroup,
        emergencyContact = emergencyContact, issueDate = issueDate, expiryDate = expiryDate,
        digitalSignatureToken = digitalSignatureToken,
        status = try { DigitalIdStatus.valueOf(status) } catch(e: Exception) { DigitalIdStatus.VERIFIED_ACTIVE },
        qrTokenPayload = qrTokenPayload
    )

    private fun DigitalIdCard.toEntity() = DigitalIdCardEntity(
        uid = uid, fullName = fullName, universityId = universityId, employeeId = employeeId,
        department = department, faculty = faculty, role = role.name, designation = designation,
        photoUrl = photoUrl, bloodGroup = bloodGroup, emergencyContact = emergencyContact,
        issueDate = issueDate, expiryDate = expiryDate, digitalSignatureToken = digitalSignatureToken,
        status = status.name, qrTokenPayload = qrTokenPayload
    )

    private fun UserDeviceEntity.toDomain() = UserDevice(
        deviceId = deviceId, deviceName = deviceName, androidVersion = androidVersion,
        ipAddress = ipAddress, lastLoginTime = lastLoginTime, location = location,
        isCurrentDevice = isCurrentDevice
    )

    private fun UserDevice.toEntity() = UserDeviceEntity(
        deviceId = deviceId, deviceName = deviceName, androidVersion = androidVersion,
        ipAddress = ipAddress, lastLoginTime = lastLoginTime, location = location,
        isCurrentDevice = isCurrentDevice
    )

    private fun LoginHistoryEntity.toDomain() = LoginHistoryItem(
        logId = logId, loginTime = loginTime, logoutTime = logoutTime, deviceName = deviceName,
        ipAddress = ipAddress, platform = platform, status = status
    )

    private fun LoginHistoryItem.toEntity() = LoginHistoryEntity(
        logId = logId, loginTime = loginTime, logoutTime = logoutTime, deviceName = deviceName,
        ipAddress = ipAddress, platform = platform, status = status
    )

    private fun UserSettingsEntity.toDomain() = UserSettings(
        themeMode = try { AppThemeMode.valueOf(themeMode) } catch(e: Exception) { AppThemeMode.SYSTEM },
        dynamicColorsEnabled = dynamicColorsEnabled,
        language = try { AppLanguage.valueOf(language) } catch(e: Exception) { AppLanguage.ENGLISH },
        notificationsEnabled = notificationsEnabled,
        biometricEnabled = biometricEnabled,
        pinLockEnabled = pinLockEnabled,
        pinCode = pinCode,
        defaultDashboard = defaultDashboard,
        autoDownloadMediaOnWifi = autoDownloadMediaOnWifi,
        fontSizeScale = fontSizeScale
    )

    private fun UserSettings.toEntity(uid: String) = UserSettingsEntity(
        uid = uid,
        themeMode = themeMode.name,
        dynamicColorsEnabled = dynamicColorsEnabled,
        language = language.name,
        notificationsEnabled = notificationsEnabled,
        biometricEnabled = biometricEnabled,
        pinLockEnabled = pinLockEnabled,
        pinCode = pinCode,
        defaultDashboard = defaultDashboard,
        autoDownloadMediaOnWifi = autoDownloadMediaOnWifi,
        fontSizeScale = fontSizeScale
    )
}
