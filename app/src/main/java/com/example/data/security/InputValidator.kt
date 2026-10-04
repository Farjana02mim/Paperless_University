package com.example.data.security

import java.util.regex.Pattern

/**
 * Enterprise Input Validator following OWASP Mobile Security guidelines.
 * Strict validation for all user inputs across Smart Paperless University System.
 */
object InputValidator {

    private val EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    )

    private val BANGLADESH_PHONE_PATTERN = Pattern.compile(
        "^(\\+88)?01[3-9]\\d{8}$"
    )

    private val STUDENT_ID_PATTERN = Pattern.compile(
        "^[0-9]{2,4}-[0-9]{3,5}-[1-3]$" // e.g. 20-42101-1 or 2021-001-1
    )

    private val TEACHER_ID_PATTERN = Pattern.compile(
        "^FAC-[0-9]{4,6}$" // e.g. FAC-202301
    )

    private val QR_JWT_PATTERN = Pattern.compile(
        "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$"
    )

    fun validateEmail(email: String): ValidationResult {
        val trimmed = email.trim()
        return when {
            trimmed.isEmpty() -> ValidationResult.Invalid("Email address cannot be empty")
            !EMAIL_PATTERN.matcher(trimmed).matches() -> ValidationResult.Invalid("Invalid email format")
            else -> ValidationResult.Valid
        }
    }

    fun validatePhone(phone: String): ValidationResult {
        val trimmed = phone.trim()
        return when {
            trimmed.isEmpty() -> ValidationResult.Invalid("Phone number cannot be empty")
            !BANGLADESH_PHONE_PATTERN.matcher(trimmed).matches() -> ValidationResult.Invalid("Enter a valid phone number (e.g. +8801712345678)")
            else -> ValidationResult.Valid
        }
    }

    fun validatePassword(password: String): ValidationResult {
        return when {
            password.length < 8 -> ValidationResult.Invalid("Password must be at least 8 characters long")
            !password.any { it.isUpperCase() } -> ValidationResult.Invalid("Password must contain at least one uppercase letter")
            !password.any { it.isLowerCase() } -> ValidationResult.Invalid("Password must contain at least one lowercase letter")
            !password.any { it.isDigit() } -> ValidationResult.Invalid("Password must contain at least one digit")
            !password.any { "!@#$%^&*()_+-=[]{}|;:,.<>?".contains(it) } -> ValidationResult.Invalid("Password must contain at least one special character")
            else -> ValidationResult.Valid
        }
    }

    fun validateStudentId(id: String): ValidationResult {
        val trimmed = id.trim()
        return when {
            trimmed.isEmpty() -> ValidationResult.Invalid("Student ID cannot be empty")
            !STUDENT_ID_PATTERN.matcher(trimmed).matches() && trimmed.length < 5 -> ValidationResult.Invalid("Invalid Student ID format (e.g. 20-42101-1)")
            else -> ValidationResult.Valid
        }
    }

    fun validateTeacherId(id: String): ValidationResult {
        val trimmed = id.trim()
        return when {
            trimmed.isEmpty() -> ValidationResult.Invalid("Faculty ID cannot be empty")
            !TEACHER_ID_PATTERN.matcher(trimmed).matches() && trimmed.length < 4 -> ValidationResult.Invalid("Invalid Faculty ID format (e.g. FAC-202301)")
            else -> ValidationResult.Valid
        }
    }

    fun validatePaymentAmount(amount: Double): ValidationResult {
        return when {
            amount <= 0.0 -> ValidationResult.Invalid("Payment amount must be greater than zero")
            amount > 1_000_000.0 -> ValidationResult.Invalid("Payment amount exceeds maximum transaction limit (1,000,000 BDT)")
            else -> ValidationResult.Valid
        }
    }

    fun validateMarks(marks: Double, maxMarks: Double = 100.0): ValidationResult {
        return when {
            marks < 0.0 -> ValidationResult.Invalid("Marks cannot be negative")
            marks > maxMarks -> ValidationResult.Invalid("Marks cannot exceed maximum mark of $maxMarks")
            else -> ValidationResult.Valid
        }
    }

    fun validateFileUpload(fileName: String, sizeBytes: Long, mimeType: String): ValidationResult {
        val allowedExtensions = listOf("pdf", "jpg", "jpeg", "png", "docx", "pptx", "xlsx", "zip")
        val ext = fileName.substringAfterLast('.', "").lowercase()
        val maxSizeBytes = 25 * 1024 * 1024L // 25 MB max

        return when {
            sizeBytes > maxSizeBytes -> ValidationResult.Invalid("File size exceeds 25 MB limit")
            ext !in allowedExtensions -> ValidationResult.Invalid("Unsupported file format ($ext)")
            else -> ValidationResult.Valid
        }
    }

    fun validateQrToken(token: String): ValidationResult {
        return when {
            token.isBlank() -> ValidationResult.Invalid("QR token is empty")
            token.length < 10 -> ValidationResult.Invalid("Malformed QR code token")
            else -> ValidationResult.Valid
        }
    }

    sealed class ValidationResult {
        object Valid : ValidationResult()
        data class Invalid(val reason: String) : ValidationResult()

        val isValid: Boolean get() = this is Valid
    }
}
