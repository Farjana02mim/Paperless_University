package com.example.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class StudentDashboardData(
    val studentName: String = "Alex Mercer",
    val universityId: String = "S-2026-9041",
    val department: String = "Computer Science & Engineering",
    val semester: String = "6th Semester",
    val cgpa: Float = 3.85f,
    val todayAttendancePercent: Int = 92,
    val photoUrl: String = "",
    val schedules: List<ScheduleItem> = emptyList(),
    val notices: List<NoticeItem> = emptyList(),
    val assignments: List<AssignmentItem> = emptyList(),
    val banners: List<AnnouncementBanner> = emptyList(),
    val performance: PerformanceSummary = PerformanceSummary(),
    val activities: List<ActivityItem> = emptyList(),
    val notifications: List<NotificationItem> = emptyList()
)

data class ScheduleItem(
    val id: String = "",
    val subject: String = "",
    val teacher: String = "",
    val time: String = "",
    val room: String = "",
    val status: ScheduleStatus = ScheduleStatus.UPCOMING
)

enum class ScheduleStatus { UPCOMING, ONGOING, COMPLETED }

data class NoticeItem(
    val id: String = "",
    val title: String = "",
    val category: String = "Academic",
    val priority: PriorityLevel = PriorityLevel.NORMAL,
    val publishedTime: String = "",
    val isRead: Boolean = false,
    val details: String = ""
)

enum class PriorityLevel { URGENT, HIGH, NORMAL }

data class AssignmentItem(
    val id: String = "",
    val subject: String = "",
    val title: String = "",
    val deadline: String = "",
    val remainingDays: Int = 0,
    val submissionStatus: String = "Pending",
    val progressPercent: Float = 0f
)

data class AnnouncementBanner(
    val id: String = "",
    val title: String = "",
    val category: String = "",
    val description: String = "",
    val imageUrl: String = ""
)

data class PerformanceSummary(
    val attendancePercent: Int = 92,
    val completedAssignments: Int = 18,
    val pendingAssignments: Int = 2,
    val currentGpa: Float = 3.88f,
    val creditsCompleted: Int = 96
)

data class ActivityItem(
    val id: String = "",
    val title: String = "",
    val category: String = "",
    val timestamp: String = "",
    val type: ActivityType = ActivityType.ATTENDANCE
)

enum class ActivityType { ASSIGNMENT, PAYMENT, ATTENDANCE, RESULT, NOTICE }

data class NotificationItem(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val category: NotificationCategory = NotificationCategory.ACADEMIC,
    val timestamp: String = "",
    val isRead: Boolean = false
)

enum class NotificationCategory { ACADEMIC, ADMINISTRATIVE, EMERGENCY, FINANCE, LIBRARY }

data class CourseItem(
    val code: String = "",
    val title: String = "",
    val instructor: String = "",
    val credits: Int = 3,
    val progress: Float = 0.75f,
    val currentGrade: String = "A"
)

data class LibraryBook(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val category: String = "",
    val isAvailable: Boolean = true,
    val copies: Int = 5
)
