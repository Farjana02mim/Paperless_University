package com.example.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.datastore.DataStoreManager
import com.example.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val drawableRes: Int
)

class OnboardingViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    private val _navigateToWelcome = MutableSharedFlow<Unit>()
    val navigateToWelcome: SharedFlow<Unit> = _navigateToWelcome.asSharedFlow()

    fun completeOnboarding() {
        viewModelScope.launch {
            dataStoreManager.setOnboardingCompleted(true)
            _navigateToWelcome.emit(Unit)
        }
    }
}
