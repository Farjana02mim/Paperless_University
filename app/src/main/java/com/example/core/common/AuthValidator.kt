package com.example.core.common

/**
 * Strict authentication & registration input validator enforcing security rules.
 */
object AuthValidator {

    private val EMAIL_REGEX = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")

    fun validateFullName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult(false, "Full name cannot be empty")
            name.trim().length < 3 -> ValidationResult(false, "Full name must be at least 3 characters")
            else -> ValidationResult(true)
        }
    }

    fun validateUniversityId(universityId: String): ValidationResult {
        return when {
            universityId.isBlank() -> ValidationResult(false, "University ID is required")
            universityId.trim().length < 4 -> ValidationResult(false, "University ID must be at least 4 characters")
            !universityId.matches(Regex("^[a-zA-Z0-9\\-_]+$")) -> ValidationResult(false, "University ID contains invalid characters")
            else -> ValidationResult(true)
        }
    }

    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult(false, "Email address is required")
            !EMAIL_REGEX.matches(email.trim()) -> ValidationResult(false, "Please enter a valid email address")
            else -> ValidationResult(true)
        }
    }

    fun validatePassword(password: String): ValidationResult {
        val hasMinLength = password.length >= 8
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }

        return when {
            password.isBlank() -> ValidationResult(false, "Password is required")
            !hasMinLength -> ValidationResult(false, "Password must be at least 8 characters")
            !hasUppercase -> ValidationResult(false, "Password must contain at least one uppercase letter")
            !hasLowercase -> ValidationResult(false, "Password must contain at least one lowercase letter")
            !hasDigit -> ValidationResult(false, "Password must contain at least one number")
            !hasSpecialChar -> ValidationResult(false, "Password must contain at least one special character")
            else -> ValidationResult(true)
        }
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): ValidationResult {
        return when {
            confirmPassword.isBlank() -> ValidationResult(false, "Please confirm your password")
            password != confirmPassword -> ValidationResult(false, "Passwords do not match")
            else -> ValidationResult(true)
        }
    }

    fun validatePhone(phone: String): ValidationResult {
        val digitsOnly = phone.filter { it.isDigit() }
        return when {
            phone.isBlank() -> ValidationResult(false, "Phone number is required")
            digitsOnly.length < 10 -> ValidationResult(false, "Phone number must be at least 10 digits")
            else -> ValidationResult(true)
        }
    }

    fun validateDepartment(department: String): ValidationResult {
        return when {
            department.isBlank() -> ValidationResult(false, "Please select your department")
            else -> ValidationResult(true)
        }
    }

    fun validateFaculty(faculty: String): ValidationResult {
        return when {
            faculty.isBlank() -> ValidationResult(false, "Please select your faculty")
            else -> ValidationResult(true)
        }
    }

    fun validateSemesterOrDesignation(value: String, role: UserRole): ValidationResult {
        return when {
            value.isBlank() -> {
                val label = if (role == UserRole.STUDENT) "Semester" else "Designation"
                ValidationResult(false, "Please specify your $label")
            }
            else -> ValidationResult(true)
        }
    }

    fun validateTermsAccepted(accepted: Boolean): ValidationResult {
        return when {
            !accepted -> ValidationResult(false, "You must accept terms and privacy policy to continue")
            else -> ValidationResult(true)
        }
    }
}
