package com.example.domain.repository.teacher

import com.example.domain.model.teacher.*
import kotlinx.coroutines.flow.Flow

interface TeacherRepository {
    fun getTeacherProfile(): Flow<TeacherProfile>
    fun getAssignedCourses(): Flow<List<TeacherCourse>>
    fun getTodayClasses(): Flow<List<ClassScheduleItem>>
    fun getTeacherTasks(): Flow<List<TeacherTask>>
    suspend fun toggleTaskCompletion(taskId: String): Result<Unit>
    suspend fun addTask(task: TeacherTask): Result<Unit>
    fun getTeacherNotifications(): Flow<List<TeacherNotification>>
    fun getTeachingAnalytics(): Flow<TeachingAnalytics>
    fun getCourseResources(courseId: String? = null): Flow<List<CourseResource>>
    suspend fun uploadCourseResource(resource: CourseResource): Result<Unit>
    fun getEnrolledStudents(
        courseId: String? = null,
        searchQuery: String = "",
        deptFilter: String = "All",
        semFilter: String = "All"
    ): Flow<List<TeacherStudentItem>>
    fun getTeacherNotices(): Flow<List<TeacherNotice>>
    suspend fun publishNotice(notice: TeacherNotice): Result<Unit>
    fun getTeacherMessages(): Flow<List<TeacherMessage>>
    suspend fun sendMessage(message: TeacherMessage): Result<Unit>
    fun getAcademicCalendarEvents(): Flow<List<AcademicCalendarEvent>>
    suspend fun rescheduleClass(classId: String, newTime: String, newRoom: String): Result<Unit>
    suspend fun updateAttendanceStatus(classId: String, isTaken: Boolean): Result<Unit>
}
