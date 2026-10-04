package com.example.core.common

/**
 * Result object returned by input field validation functions.
 */
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)
