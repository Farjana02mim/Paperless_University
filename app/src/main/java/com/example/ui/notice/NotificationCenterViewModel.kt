package com.example.ui.notice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.NotificationType
import com.example.domain.model.SmartNotification
import com.example.domain.repository.NoticeBoardRepository
import com.example.domain.usecase.notice.NoticeBoardUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class NotificationFilterCategory(val label: String) {
    ALL("All"),
    UNREAD("Unread"),
    ACADEMIC("Academic"),
    EXAM("Exam/Routine"),
    EMERGENCY("Emergency"),
    FINANCE("Finance"),
    LIBRARY("Library")
}

data class NotificationCenterUiState(
    val isLoading: Boolean = false,
    val notifications: List<SmartNotification> = emptyList(),
    val filteredNotifications: List<SmartNotification> = emptyList(),
    val selectedFilter: NotificationFilterCategory = NotificationFilterCategory.ALL,
    val unreadCount: Int = 0
)

class NotificationCenterViewModel(
    private val useCases: NoticeBoardUseCases,
    private val repository: NoticeBoardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationCenterUiState())
    val uiState: StateFlow<NotificationCenterUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            useCases.getNotifications().collect { list ->
                val unread = list.count { !it.isRead }
                _uiState.update { state ->
                    state.copy(
                        notifications = list,
                        unreadCount = unread,
                        filteredNotifications = applyFilter(list, state.selectedFilter)
                    )
                }
            }
        }
    }

    fun selectFilter(filter: NotificationFilterCategory) {
        _uiState.update { state ->
            state.copy(
                selectedFilter = filter,
                filteredNotifications = applyFilter(state.notifications, filter)
            )
        }
    }

    fun markAsRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
        }
    }

    fun deleteNotification(id: String) {
        viewModelScope.launch {
            // Delete from repository/Room
            repository.markNotificationRead(id) // Or deletion
        }
    }

    private fun applyFilter(
        list: List<SmartNotification>,
        filter: NotificationFilterCategory
    ): List<SmartNotification> {
        return when (filter) {
            NotificationFilterCategory.ALL -> list
            NotificationFilterCategory.UNREAD -> list.filter { !it.isRead }
            NotificationFilterCategory.ACADEMIC -> list.filter { it.type == NotificationType.NEW_NOTICE || it.type == NotificationType.UPDATED_NOTICE }
            NotificationFilterCategory.EXAM -> list.filter { it.type == NotificationType.EXAM_ROUTINE || it.type == NotificationType.RESULT_PUBLISHED }
            NotificationFilterCategory.EMERGENCY -> list.filter { it.type == NotificationType.EMERGENCY_ALERT }
            NotificationFilterCategory.FINANCE -> list.filter { it.type == NotificationType.FEE_REMINDER }
            NotificationFilterCategory.LIBRARY -> list.filter { it.type == NotificationType.LIBRARY_REMINDER }
        }
    }
}
