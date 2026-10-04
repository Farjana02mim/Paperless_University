package com.example.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.core.common.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "smart_campus_preferences")

/**
 * DataStore repository managing session persistence, auto-login state, and onboarding progress.
 */
class DataStoreManager(private val context: Context) {

    companion object {
        private val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("key_onboarding_completed")
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("key_is_logged_in")
        private val KEY_REMEMBER_ME = booleanPreferencesKey("key_remember_me")
        private val KEY_USER_UID = stringPreferencesKey("key_user_uid")
        private val KEY_USER_EMAIL = stringPreferencesKey("key_user_email")
        private val KEY_USER_ROLE = stringPreferencesKey("key_user_role")
        private val KEY_USER_NAME = stringPreferencesKey("key_user_name")
        private val KEY_EMAIL_VERIFIED = booleanPreferencesKey("key_email_verified")
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_ONBOARDING_COMPLETED] ?: false
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_IS_LOGGED_IN] ?: false
    }

    val rememberMe: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_REMEMBER_ME] ?: true
    }

    val savedUserUid: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_UID]
    }

    val savedUserRole: Flow<UserRole> = context.dataStore.data.map { prefs ->
        UserRole.fromString(prefs[KEY_USER_ROLE])
    }

    val savedUserName: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_NAME]
    }

    val savedUserEmail: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_EMAIL]
    }

    val isEmailVerified: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_EMAIL_VERIFIED] ?: false
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun saveUserSession(
        uid: String,
        email: String,
        name: String,
        role: UserRole,
        rememberMe: Boolean,
        emailVerified: Boolean
    ) {
        context.dataStore.edit { prefs ->
            prefs[KEY_IS_LOGGED_IN] = rememberMe
            prefs[KEY_REMEMBER_ME] = rememberMe
            prefs[KEY_USER_UID] = uid
            prefs[KEY_USER_EMAIL] = email
            prefs[KEY_USER_NAME] = name
            prefs[KEY_USER_ROLE] = role.name
            prefs[KEY_EMAIL_VERIFIED] = emailVerified
        }
    }

    suspend fun updateEmailVerified(verified: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_EMAIL_VERIFIED] = verified
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs[KEY_IS_LOGGED_IN] = false
            prefs[KEY_USER_UID] = ""
            prefs[KEY_USER_EMAIL] = ""
            prefs[KEY_USER_NAME] = ""
            prefs[KEY_USER_ROLE] = ""
            prefs[KEY_EMAIL_VERIFIED] = false
        }
    }
}
