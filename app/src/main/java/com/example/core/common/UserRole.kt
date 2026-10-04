package com.example.core.common

/**
 * User roles supported across Smart Paperless University Management System.
 */
enum class UserRole(val roleName: String) {
    STUDENT("Student"),
    TEACHER("Teacher"),
    ADMIN("Administrator");

    companion object {
        fun fromString(value: String?): UserRole {
            return when (value?.uppercase()) {
                "STUDENT" -> STUDENT
                "TEACHER" -> TEACHER
                "ADMINISTRATOR", "ADMIN" -> ADMIN
                else -> STUDENT
            }
        }
    }
}
