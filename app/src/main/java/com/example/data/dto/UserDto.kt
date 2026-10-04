package com.example.data.dto

import com.example.core.common.UserRole
import com.example.core.common.UserStatus
import com.example.domain.model.UserProfile
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

/**
 * Data Transfer Object for Firestore 'users' collection documents.
 */
@IgnoreExtraProperties
data class UserDto(
    @get:PropertyName("uid") @set:PropertyName("uid") var uid: String = "",
    @get:PropertyName("fullName") @set:PropertyName("fullName") var fullName: String = "",
    @get:PropertyName("email") @set:PropertyName("email") var email: String = "",
    @get:PropertyName("phone") @set:PropertyName("phone") var phone: String = "",
    @get:PropertyName("universityId") @set:PropertyName("universityId") var universityId: String = "",
    @get:PropertyName("department") @set:PropertyName("department") var department: String = "",
    @get:PropertyName("faculty") @set:PropertyName("faculty") var faculty: String = "",
    @get:PropertyName("semester") @set:PropertyName("semester") var semester: String = "",
    @get:PropertyName("designation") @set:PropertyName("designation") var designation: String = "",
    @get:PropertyName("role") @set:PropertyName("role") var role: String = UserRole.STUDENT.name,
    @get:PropertyName("photoUrl") @set:PropertyName("photoUrl") var photoUrl: String = "",
    @get:PropertyName("status") @set:PropertyName("status") var status: String = UserStatus.PENDING_VERIFICATION.name,
    @get:PropertyName("createdAt") @set:PropertyName("createdAt") var createdAt: Long = System.currentTimeMillis(),
    @get:PropertyName("updatedAt") @set:PropertyName("updatedAt") var updatedAt: Long = System.currentTimeMillis(),
    @get:PropertyName("lastLogin") @set:PropertyName("lastLogin") var lastLogin: Long = System.currentTimeMillis(),
    @get:PropertyName("emailVerified") @set:PropertyName("emailVerified") var emailVerified: Boolean = false,
    @get:PropertyName("deviceToken") @set:PropertyName("deviceToken") var deviceToken: String = ""
) {
    fun toDomain(): UserProfile {
        return UserProfile(
            uid = uid,
            fullName = fullName,
            email = email,
            phone = phone,
            universityId = universityId,
            department = department,
            faculty = faculty,
            semester = semester,
            designation = designation,
            role = UserRole.fromString(role),
            photoUrl = photoUrl,
            status = UserStatus.fromString(status),
            createdAt = createdAt,
            updatedAt = updatedAt,
            lastLogin = lastLogin,
            emailVerified = emailVerified,
            deviceToken = deviceToken
        )
    }

    companion object {
        fun fromDomain(userProfile: UserProfile): UserDto {
            return UserDto(
                uid = userProfile.uid,
                fullName = userProfile.fullName,
                email = userProfile.email,
                phone = userProfile.phone,
                universityId = userProfile.universityId,
                department = userProfile.department,
                faculty = userProfile.faculty,
                semester = userProfile.semester,
                designation = userProfile.designation,
                role = userProfile.role.name,
                photoUrl = userProfile.photoUrl,
                status = userProfile.status.name,
                createdAt = userProfile.createdAt,
                updatedAt = userProfile.updatedAt,
                lastLogin = userProfile.lastLogin,
                emailVerified = userProfile.emailVerified,
                deviceToken = userProfile.deviceToken
            )
        }
    }
}
