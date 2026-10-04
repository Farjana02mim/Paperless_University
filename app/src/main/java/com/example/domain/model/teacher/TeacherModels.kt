package com.example.domain.model.teacher

data class TeacherProfile(
    val teacherId: String = "T-88219",
    val fullName: String = "Dr. Sarah Jenkins",
    val designation: String = "Senior Associate Professor",
    val department: String = "Computer Science & Engineering",
    val email: String = "sarah.jenkins@university.edu",
    val phone: String = "+1 (555) 234-5678",
    val officeRoom: String = "Building 3, Room 402",
    val photoUrl: String = "https://picsum.photos/300/300",
    val researchAreas: List<String> = listOf("Artificial Intelligence", "Distributed Systems", "Cloud Computing"),
    val joinYear: String = "2018",
    val rating: Double = 4.9,
    val totalStudentsTaught: Int = 1450,
    val isVerified: Boolean = true
)

data class TeacherCourse(
    val courseId: String,
    val courseCode: String,
    val courseName: String,
    val department: String,
    val semester: String,
    val section: String,
    val totalStudents: Int,
    val credits: Int,
    val syllabusSummary: String,
    val roomNumber: String,
    val scheduleTime: String,
    val coverImageUrl: String = "",
    val progressPercentage: Int = 65,
    val pendingEvaluationsCount: Int = 12,
    val averageAttendanceRate: Double = 88.5
)

enum class ScheduleStatus {
    UPCOMING, IN_PROGRESS, COMPLETED, CANCELLED, RESCHEDULED
}

data class ClassScheduleItem(
    val id: String,
    val courseId: String,
    val courseCode: String,
    val courseName: String,
    val timeSlot: String,
    val roomNumber: String,
    val topicTitle: String,
    val status: ScheduleStatus = ScheduleStatus.UPCOMING,
    val date: String,
    val section: String,
    val studentCount: Int = 45,
    val isAttendanceTaken: Boolean = false
)

enum class TaskType {
    EVALUATION, CLASS, RESULT, MEETING, PERSONAL_NOTE
}

data class TeacherTask(
    val taskId: String,
    val title: String,
    val description: String = "",
    val courseName: String = "",
    val deadline: String,
    val type: TaskType,
    val isCompleted: Boolean = false,
    val priority: String = "Medium" // High, Medium, Low
)

data class TeacherNotification(
    val notificationId: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val category: String, // Class, Assignment, Result, Notice, Message
    val isRead: Boolean = false
)

data class TeachingAnalytics(
    val totalTeachingHours: Double = 124.5,
    val overallAttendanceRate: Double = 91.2,
    val avgStudentScore: Double = 82.4,
    val courseProgressAvg: Double = 68.0,
    val totalAssignmentsEvaluated: Int = 340,
    val pendingEvaluations: Int = 18,
    val totalClassesConducted: Int = 42,
    val studentEngagementScore: Double = 94.0
)

enum class ResourceType {
    PDF, SLIDES, VIDEO, LAB_MANUAL, QUESTION_PAPER, SOLUTION
}

data class CourseResource(
    val resourceId: String,
    val courseId: String,
    val courseCode: String,
    val title: String,
    val resourceType: ResourceType,
    val fileUrl: String,
    val uploadDate: String,
    val sizeKb: Long,
    val downloadsCount: Int = 0
)

data class TeacherStudentItem(
    val studentId: String,
    val studentName: String,
    val rollNumber: String,
    val department: String,
    val semester: String,
    val section: String,
    val courseCode: String,
    val attendancePercentage: Double,
    val avgGrade: String,
    val assignmentsSubmitted: Int,
    val totalAssignments: Int,
    val photoUrl: String = "",
    val email: String = "",
    val phone: String = ""
)

data class TeacherNotice(
    val noticeId: String,
    val title: String,
    val content: String,
    val targetDepartment: String,
    val targetCourse: String = "All",
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val publishedAt: String,
    val authorName: String = "Dr. Sarah Jenkins"
)

data class TeacherMessage(
    val messageId: String,
    val senderId: String,
    val senderName: String,
    val receiverId: String,
    val receiverName: String,
    val content: String,
    val timestamp: String,
    val isRead: Boolean = false,
    val attachmentUrl: String? = null
)

data class AcademicCalendarEvent(
    val eventId: String,
    val title: String,
    val date: String,
    val eventType: String, // Exam, Class, Meeting, Deadline, Holiday
    val description: String
)
