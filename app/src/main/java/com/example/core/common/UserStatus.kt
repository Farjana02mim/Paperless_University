package com.example.core.common

/**
 * Account statuses in the Smart Campus platform.
 */
enum class UserStatus {
    ACTIVE,
    PENDING_VERIFICATION,
    SUSPENDED;

    companion object {
        fun fromString(value: String?): UserStatus {
            return when (value?.uppercase()) {
                "ACTIVE" -> ACTIVE
                "SUSPENDED" -> SUSPENDED
                else -> PENDING_VERIFICATION
            }
        }
    }
}
