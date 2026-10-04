package com.example.ui.assignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.AssignmentAnalytics
import com.example.domain.repository.AssignmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AssignmentAnalyticsViewModel(
    private val repository: AssignmentRepository
) : ViewModel() {

    private val _analytics = MutableStateFlow<AssignmentAnalytics?>(null)
    val analytics: StateFlow<AssignmentAnalytics?> = _analytics.asStateFlow()

    private val _syncedCount = MutableStateFlow<Int?>(null)
    val syncedCount: StateFlow<Int?> = _syncedCount.asStateFlow()

    init {
        loadAnalytics("CS-301")
    }

    fun loadAnalytics(courseId: String) {
        viewModelScope.launch {
            val result = repository.getAssignmentAnalytics(courseId)
            if (result.isSuccess) {
                _analytics.value = result.getOrNull()
            }
        }
    }

    fun syncOfflineSubmissions() {
        viewModelScope.launch {
            val result = repository.syncUnsyncedSubmissions()
            if (result.isSuccess) {
                _syncedCount.value = result.getOrNull()
            }
        }
    }

    fun clearSyncMessage() {
        _syncedCount.value = null
    }
}
