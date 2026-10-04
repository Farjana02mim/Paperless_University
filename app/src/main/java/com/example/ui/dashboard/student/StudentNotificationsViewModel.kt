package com.example.ui.dashboard.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.Resource
import com.example.domain.model.NotificationCategory
import com.example.domain.model.NotificationItem
import com.example.domain.usecase.DeleteNotificationUseCase
import com.example.domain.usecase.GetNotificationsUseCase
import com.example.domain.usecase.MarkNotificationReadUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudentNotificationsUiState(
    val selectedCategory: NotificationCategory? = null,
    val isFilterActive: Boolean = false
)

class StudentNotificationsViewModel(
    getNotificationsUseCase: GetNotificationsUseCase,
    private val markNotificationReadUseCase: MarkNotificationReadUseCase,
    private val deleteNotificationUseCase: DeleteNotificationUseCase
) : ViewModel() {

    val notifications: StateFlow<List<NotificationItem>> = getNotificationsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow(StudentNotificationsUiState())
    val uiState: StateFlow<StudentNotificationsUiState> = _uiState.asStateFlow()

    fun filterByCategory(category: NotificationCategory?) {
        _uiState.update { it.copy(selectedCategory = category, isFilterActive = category != null) }
    }

    fun markAsRead(id: String) {
        viewModelScope.launch {
            markNotificationReadUseCase(id)
        }
    }

    fun deleteNotification(id: String) {
        viewModelScope.launch {
            deleteNotificationUseCase(id)
        }
    }
}
