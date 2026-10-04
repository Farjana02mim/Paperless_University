package com.example

import com.example.core.common.AuthValidator
import com.example.core.common.UserRole
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthValidatorTest {

    @Test
    fun valid_email_returns_true() {
        val result = AuthValidator.validateEmail("student@university.edu")
        assertTrue(result.isValid)
    }

    @Test
    fun invalid_email_returns_false() {
        val result = AuthValidator.validateEmail("invalid-email-address")
        assertFalse(result.isValid)
    }

    @Test
    fun strong_password_returns_true() {
        val result = AuthValidator.validatePassword("StrongPass@123")
        assertTrue(result.isValid)
    }

    @Test
    fun weak_password_without_special_char_returns_false() {
        val result = AuthValidator.validatePassword("WeakPass123")
        assertFalse(result.isValid)
    }

    @Test
    fun short_password_returns_false() {
        val result = AuthValidator.validatePassword("Short1!")
        assertFalse(result.isValid)
    }

    @Test
    fun matching_passwords_return_true() {
        val result = AuthValidator.validateConfirmPassword("Password@123", "Password@123")
        assertTrue(result.isValid)
    }

    @Test
    fun mismatched_passwords_return_false() {
        val result = AuthValidator.validateConfirmPassword("Password@123", "DifferentPass@123")
        assertFalse(result.isValid)
    }

    @Test
    fun valid_phone_returns_true() {
        val result = AuthValidator.validatePhone("+15550192831")
        assertTrue(result.isValid)
    }

    @Test
    fun valid_university_id_returns_true() {
        val result = AuthValidator.validateUniversityId("S-2026-9041")
        assertTrue(result.isValid)
    }
}
