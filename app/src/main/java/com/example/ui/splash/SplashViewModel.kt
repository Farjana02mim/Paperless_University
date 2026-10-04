package com.example.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.UserRole
import com.example.core.datastore.DataStoreManager
import com.example.domain.repository.AuthRepository
import com.example.ui.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class SplashDestination {
    object Loading : SplashDestination()
    data class Navigate(val route: String) : SplashDestination()
}

class SplashViewModel(
    private val dataStoreManager: DataStoreManager,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination>(SplashDestination.Loading)
    val destination: StateFlow<SplashDestination> = _destination.asStateFlow()

    init {
        decideNextDestination()
    }

    private fun decideNextDestination() {
        viewModelScope.launch {
            try {
                // Splash branding animation delay
                delay(1200)

                kotlinx.coroutines.withTimeoutOrNull(2000) {
                    val isOnboardingCompleted = dataStoreManager.isOnboardingCompleted.first()
                    if (!isOnboardingCompleted) {
                        _destination.value = SplashDestination.Navigate(Screen.Onboarding.route)
                        return@withTimeoutOrNull
                    }

                    val isLoggedIn = dataStoreManager.isLoggedIn.first()
                    if (!isLoggedIn) {
                        _destination.value = SplashDestination.Navigate(Screen.Welcome.route)
                        return@withTimeoutOrNull
                    }

                    val isEmailVerified = dataStoreManager.isEmailVerified.first()
                    if (!isEmailVerified) {
                        _destination.value = SplashDestination.Navigate(Screen.EmailVerification.route)
                        return@withTimeoutOrNull
                    }

                    val savedRole = dataStoreManager.savedUserRole.first()
                    val targetRoute = when (savedRole) {
                        UserRole.STUDENT -> Screen.StudentDashboard.route
                        UserRole.TEACHER -> Screen.TeacherDashboard.route
                        UserRole.ADMIN -> Screen.AdminDashboard.route
                    }

                    _destination.value = SplashDestination.Navigate(targetRoute)
                } ?: run {
                    _destination.value = SplashDestination.Navigate(Screen.Welcome.route)
                }
            } catch (e: Exception) {
                _destination.value = SplashDestination.Navigate(Screen.Welcome.route)
            }
        }
    }
}
