package com.example.domain.usecase

import com.example.core.common.Resource
import com.example.domain.model.CourseItem
import com.example.domain.model.LibraryBook
import com.example.domain.model.NotificationItem
import com.example.domain.model.StudentDashboardData
import com.example.domain.repository.StudentDashboardRepository
import kotlinx.coroutines.flow.Flow

class GetStudentDashboardUseCase(
    private val repository: StudentDashboardRepository
) {
    suspend operator fun invoke(): Resource<StudentDashboardData> {
        return repository.getStudentDashboardData()
    }
}

class GetNotificationsUseCase(
    private val repository: StudentDashboardRepository
) {
    operator fun invoke(): Flow<List<NotificationItem>> {
        return repository.observeNotifications()
    }
}

class MarkNotificationReadUseCase(
    private val repository: StudentDashboardRepository
) {
    suspend operator fun invoke(id: String): Resource<Unit> {
        return repository.markNotificationAsRead(id)
    }
}

class DeleteNotificationUseCase(
    private val repository: StudentDashboardRepository
) {
    suspend operator fun invoke(id: String): Resource<Unit> {
        return repository.deleteNotification(id)
    }
}

class ScanAttendanceQrUseCase(
    private val repository: StudentDashboardRepository
) {
    suspend operator fun invoke(qrCodeData: String): Resource<String> {
        return repository.scanAttendanceQr(qrCodeData)
    }
}

class GetStudentCoursesUseCase(
    private val repository: StudentDashboardRepository
) {
    suspend operator fun invoke(): Resource<List<CourseItem>> {
        return repository.getStudentCourses()
    }
}

class GetLibraryBooksUseCase(
    private val repository: StudentDashboardRepository
) {
    suspend operator fun invoke(): Resource<List<LibraryBook>> {
        return repository.getLibraryBooks()
    }
}
