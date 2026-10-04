package com.example.domain.repository

import com.example.core.common.Resource
import com.example.domain.model.CourseItem
import com.example.domain.model.LibraryBook
import com.example.domain.model.NotificationItem
import com.example.domain.model.StudentDashboardData
import kotlinx.coroutines.flow.Flow

interface StudentDashboardRepository {
    suspend fun getStudentDashboardData(): Resource<StudentDashboardData>
    fun observeNotifications(): Flow<List<NotificationItem>>
    suspend fun markNotificationAsRead(id: String): Resource<Unit>
    suspend fun deleteNotification(id: String): Resource<Unit>
    suspend fun scanAttendanceQr(qrCodeData: String): Resource<String>
    suspend fun getStudentCourses(): Resource<List<CourseItem>>
    suspend fun getLibraryBooks(): Resource<List<LibraryBook>>
}
