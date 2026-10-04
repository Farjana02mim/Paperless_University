package com.example.ui.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.AttendanceAnalytics
import com.example.domain.model.AttendanceAuditLog
import com.example.domain.model.AttendancePolicy
import com.example.domain.model.AttendanceSession
import com.example.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AdminAttendanceViewModel(
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {

    val analytics: StateFlow<AttendanceAnalytics> = attendanceRepository
        .getAttendanceAnalytics()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AttendanceAnalytics()
        )

    val activeSessions: StateFlow<List<AttendanceSession>> = attendanceRepository
        .getAllActiveSessions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val policy: StateFlow<AttendancePolicy> = attendanceRepository
        .getAttendancePolicy()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AttendancePolicy()
        )

    val auditLogs: StateFlow<List<AttendanceAuditLog>> = attendanceRepository
        .getAuditLogs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updatePolicy(newPolicy: AttendancePolicy) {
        viewModelScope.launch {
            attendanceRepository.updateAttendancePolicy(newPolicy)
        }
    }

    fun syncOfflineRecords() {
        viewModelScope.launch {
            attendanceRepository.syncOfflineRecords()
        }
    }
}
