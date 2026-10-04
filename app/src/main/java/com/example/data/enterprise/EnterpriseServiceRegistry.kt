package com.example.data.enterprise

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Enterprise Event-Driven Architecture Bus.
 * Dispatches cross-module domain events for async background processing,
 * analytics ingestion (BigQuery), Cloud Tasks, and Push Notifications.
 */
sealed class SystemDomainEvent(val eventId: String = java.util.UUID.randomUUID().toString(), val timestamp: Long = System.currentTimeMillis()) {
    data class StudentRegistered(val studentId: String, val tenantId: String, val campusId: String) : SystemDomainEvent()
    data class AttendanceRecorded(val courseId: String, val studentId: String, val status: String) : SystemDomainEvent()
    data class ResultPublished(val studentId: String, val courseCode: String, val gradePoint: Double) : SystemDomainEvent()
    data class FeePaid(val studentId: String, val amountBdt: Double, val transactionId: String) : SystemDomainEvent()
    data class BookBorrowed(val studentId: String, val bookIsbn: String) : SystemDomainEvent()
    data class EmergencyAlertTriggered(val senderId: String, val location: String, val message: String) : SystemDomainEvent()
}

class SystemEventBus {
    private val _events = MutableSharedFlow<SystemDomainEvent>(replay = 10)
    val events: SharedFlow<SystemDomainEvent> = _events.asSharedFlow()

    suspend fun emitEvent(event: SystemDomainEvent) {
        _events.emit(event)
    }

    companion object {
        val Instance = SystemEventBus()
    }
}

/**
 * Enterprise Backend Service Boundaries definitions.
 */
enum class BackendService(val serviceName: String, val restEndpoint: String, val grpcService: String) {
    AUTH_SERVICE("Authentication Service", "/api/v1/auth", "jstu.auth.v1.AuthService"),
    ACADEMIC_SERVICE("Academic & Grading Service", "/api/v1/academic", "jstu.academic.v1.AcademicService"),
    ATTENDANCE_SERVICE("Attendance Service", "/api/v1/attendance", "jstu.attendance.v1.AttendanceService"),
    FINANCE_SERVICE("Finance & Fee Service", "/api/v1/finance", "jstu.finance.v1.FinanceService"),
    LIBRARY_SERVICE("Digital Library Service", "/api/v1/library", "jstu.library.v1.LibraryService"),
    NOTIFICATION_SERVICE("Notification & Push Service", "/api/v1/notifications", "jstu.notification.v1.NotificationService"),
    AI_SERVICE("Vertex AI & Search Service", "/api/v1/ai", "jstu.ai.v1.AiService"),
    CAREER_SERVICE("Placement & Career Service", "/api/v1/career", "jstu.career.v1.CareerService"),
    FILE_STORAGE_SERVICE("GCS File Storage Service", "/api/v1/storage", "jstu.storage.v1.StorageService")
}
