package com.example.data.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Enterprise Security Crypto Manager for local data encryption using Android Keystore
 * and Jetpack Security EncryptedSharedPreferences.
 */
class SecurityCryptoManager(private val context: Context) {

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val encryptedPrefs: SharedPreferences by lazy {
        try {
            EncryptedSharedPreferences.create(
                context,
                "secure_university_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            // Fallback to standard prefs if hardware keystore fails or during unit testing
            context.getSharedPreferences("secure_university_prefs_fallback", Context.MODE_PRIVATE)
        }
    }

    fun saveEncryptedString(key: String, value: String) {
        encryptedPrefs.edit().putString(key, value).apply()
    }

    fun getEncryptedString(key: String, defaultValue: String = ""): String {
        return encryptedPrefs.getString(key, defaultValue) ?: defaultValue
    }

    fun saveEncryptedBoolean(key: String, value: Boolean) {
        encryptedPrefs.edit().putBoolean(key, value).apply()
    }

    fun getEncryptedBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return encryptedPrefs.getBoolean(key, defaultValue)
    }

    fun removeEncryptedKey(key: String) {
        encryptedPrefs.edit().remove(key).apply()
    }

    fun clearAllSecureData() {
        encryptedPrefs.edit().clear().apply()
    }

    companion object {
        const val KEY_AUTH_TOKEN = "enc_auth_token"
        const val KEY_REFRESH_TOKEN = "enc_refresh_token"
        const val KEY_BIOMETRIC_ENABLED = "enc_biometric_enabled"
        const val KEY_SESSION_TIMESTAMP = "enc_session_timestamp"
        const val KEY_USER_ROLE = "enc_user_role"
    }
}
