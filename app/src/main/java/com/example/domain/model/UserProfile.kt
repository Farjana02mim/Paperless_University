package com.example.domain.model

import com.example.core.common.UserRole
import com.example.core.common.UserStatus

/**
 * Domain model representing a university user (Student, Teacher, or Administrator).
 */
data class UserProfile(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val universityId: String = "",
    val department: String = "",
    val faculty: String = "",
    val semester: String = "",        // Filled for Students
    val designation: String = "",     // Filled for Teachers/Admins
    val role: UserRole = UserRole.STUDENT,
    val photoUrl: String = "",
    val status: UserStatus = UserStatus.PENDING_VERIFICATION,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastLogin: Long = System.currentTimeMillis(),
    val emailVerified: Boolean = false,
    val deviceToken: String = ""
)
