package com.example.data.repository

import com.example.core.common.Resource
import com.example.core.datastore.DataStoreManager
import com.example.data.local.AssignmentEntity
import com.example.data.local.CourseEntity
import com.example.data.local.LibraryEntity
import com.example.data.local.NoticeEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.ScheduleEntity
import com.example.data.local.StudentDao
import com.example.domain.model.ActivityItem
import com.example.domain.model.ActivityType
import com.example.domain.model.AnnouncementBanner
import com.example.domain.model.AssignmentItem
import com.example.domain.model.CourseItem
import com.example.domain.model.LibraryBook
import com.example.domain.model.NoticeItem
import com.example.domain.model.NotificationCategory
import com.example.domain.model.NotificationItem
import com.example.domain.model.PerformanceSummary
import com.example.domain.model.PriorityLevel
import com.example.domain.model.ScheduleItem
import com.example.domain.model.ScheduleStatus
import com.example.domain.model.StudentDashboardData
import com.example.domain.repository.StudentDashboardRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class StudentDashboardRepositoryImpl(
    private val studentDao: StudentDao,
    private val dataStoreManager: DataStoreManager
) : StudentDashboardRepository {

    private val firebaseAuth: FirebaseAuth?
        get() = try {
            FirebaseApp.getInstance()
            FirebaseAuth.getInstance()
        } catch (e: Exception) { null }

    private val firestore: FirebaseFirestore?
        get() = try {
            FirebaseApp.getInstance()
            FirebaseFirestore.getInstance()
        } catch (e: Exception) { null }

    override suspend fun getStudentDashboardData(): Resource<StudentDashboardData> {
        return try {
            val name = dataStoreManager.savedUserName.first() ?: "Alex Mercer"
            val email = dataStoreManager.savedUserEmail.first() ?: "alex.mercer@university.edu"

            val schedules = getSampleSchedules()
            val notices = getSampleNotices()
            val assignments = getSampleAssignments()
            val banners = getSampleBanners()
            val performance = PerformanceSummary(
                attendancePercent = 94,
                completedAssignments = 18,
                pendingAssignments = 2,
                currentGpa = 3.88f,
                creditsCompleted = 96
            )
            val activities = getSampleActivities()
            val notifications = getSampleNotifications()

            // Cache data into Room
            try {
                studentDao.insertNotices(notices.map {
                    NoticeEntity(it.id, it.title, it.category, it.priority.name, it.publishedTime, it.isRead, it.details)
                })
                studentDao.insertSchedules(schedules.map {
                    ScheduleEntity(it.id, it.subject, it.teacher, it.time, it.room, it.status.name)
                })
                studentDao.insertAssignments(assignments.map {
                    AssignmentEntity(it.id, it.subject, it.title, it.deadline, it.remainingDays, it.submissionStatus, it.progressPercent)
                })
                studentDao.insertNotifications(notifications.map {
                    NotificationEntity(it.id, it.title, it.message, it.category.name, it.timestamp, it.isRead)
                })
            } catch (ignored: Exception) { }

            val data = StudentDashboardData(
                studentName = name,
                universityId = "S-2026-9041",
                department = "Computer Science & Engineering",
                semester = "6th Semester",
                cgpa = 3.85f,
                todayAttendancePercent = 94,
                photoUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
                schedules = schedules,
                notices = notices,
                assignments = assignments,
                banners = banners,
                performance = performance,
                activities = activities,
                notifications = notifications
            )

            Resource.Success(data)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to load student dashboard")
        }
    }

    override fun observeNotifications(): Flow<List<NotificationItem>> {
        return studentDao.getCachedNotifications().map { entities ->
            if (entities.isEmpty()) {
                getSampleNotifications()
            } else {
                entities.map {
                    NotificationItem(
                        id = it.id,
                        title = it.title,
                        message = it.message,
                        category = try { NotificationCategory.valueOf(it.category) } catch (e: Exception) { NotificationCategory.ACADEMIC },
                        timestamp = it.timestamp,
                        isRead = it.isRead
                    )
                }
            }
        }
    }

    override suspend fun markNotificationAsRead(id: String): Resource<Unit> {
        return try {
            studentDao.markNotificationRead(id)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to mark as read")
        }
    }

    override suspend fun deleteNotification(id: String): Resource<Unit> {
        return try {
            studentDao.deleteNotification(id)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to delete notification")
        }
    }

    override suspend fun scanAttendanceQr(qrCodeData: String): Resource<String> {
        return try {
            if (qrCodeData.contains("ATTENDANCE") || qrCodeData.isNotBlank()) {
                Resource.Success("Attendance successfully recorded for CSE-302 Web Engineering!")
            } else {
                Resource.Error("Invalid QR Code payload")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "QR scanning error")
        }
    }

    override suspend fun getStudentCourses(): Resource<List<CourseItem>> {
        val courses = listOf(
            CourseItem("CSE-301", "Algorithms & Data Structures II", "Dr. Sarah Jenkins", 3, 0.85f, "A"),
            CourseItem("CSE-302", "Web & Mobile App Engineering", "Prof. Michael Faraday", 3, 0.90f, "A+"),
            CourseItem("CSE-305", "Database Systems & Spanner", "Dr. Alan Turing", 4, 0.70f, "A-"),
            CourseItem("MAT-204", "Linear Algebra & Probability", "Dr. Katherine Johnson", 3, 0.65f, "B+"),
            CourseItem("HUM-102", "Engineering Ethics & Paperless Systems", "Prof. Robert Vance", 2, 0.95f, "A+")
        )
        return Resource.Success(courses)
    }

    override suspend fun getLibraryBooks(): Resource<List<LibraryBook>> {
        val books = listOf(
            LibraryBook("B1", "Introduction to Modern Algorithms 4th Ed", "Thomas Cormen", "Computer Science", true, 8),
            LibraryBook("B2", "Jetpack Compose & Kotlin Architecture", "Android Dev Team", "Software Eng", true, 4),
            LibraryBook("B3", "Clean Architecture in Practice", "Robert C. Martin", "Software Eng", false, 0),
            LibraryBook("B4", "Database System Concepts 7th Ed", "Silberschatz", "Database", true, 12),
            LibraryBook("B5", "Artificial Intelligence: A Modern Approach", "Stuart Russell", "AI & ML", true, 3)
        )
        return Resource.Success(books)
    }

    private fun getSampleSchedules(): List<ScheduleItem> = listOf(
        ScheduleItem("1", "CSE-302: Web & Mobile Eng", "Prof. Michael Faraday", "09:00 AM - 10:30 AM", "Lab Room 402", ScheduleStatus.COMPLETED),
        ScheduleItem("2", "CSE-301: Algorithms II", "Dr. Sarah Jenkins", "11:00 AM - 12:30 PM", "Audience Hall B", ScheduleStatus.ONGOING),
        ScheduleItem("3", "MAT-204: Linear Algebra", "Dr. Katherine Johnson", "02:00 PM - 03:30 PM", "Classroom 204", ScheduleStatus.UPCOMING)
    )

    private fun getSampleNotices(): List<NoticeItem> = listOf(
        NoticeItem("N1", "Mid-Semester Examination Schedule Published", "Academic", PriorityLevel.URGENT, "10 mins ago", false, "The official mid-semester examination routine for Spring 2026 has been published. All students are advised to download their digital admit card from the portal."),
        NoticeItem("N2", "Paperless Fee Payment Deadline Extended", "Finance", PriorityLevel.HIGH, "2 hours ago", false, "The last date for tuition fee payment for 6th semester without fine is extended to March 15, 2026."),
        NoticeItem("N3", "Smart Campus Hackathon 2026 Registration Open", "Event", PriorityLevel.NORMAL, "1 day ago", true, "Register your 3-member team for the annual AI & IoT Smart Campus Hackathon before next Friday.")
    )

    private fun getSampleAssignments(): List<AssignmentItem> = listOf(
        AssignmentItem("A1", "CSE-302", "Build Jetpack Compose Dashboard UI", "Tomorrow, 11:59 PM", 1, "Pending", 0.70f),
        AssignmentItem("A2", "CSE-301", "Graph Shortest Path Optimization Lab", "Feb 18, 2026", 3, "Pending", 0.40f),
        AssignmentItem("A3", "MAT-204", "Eigenvalues & Vector Spaces Problem Set", "Feb 22, 2026", 7, "Submitted", 1.0f)
    )

    private fun getSampleBanners(): List<AnnouncementBanner> = listOf(
        AnnouncementBanner("B1", "Smart Paperless Initiative 2026", "Campus Tech", "100% Digital Student Records & Instant Verification Services now live!"),
        AnnouncementBanner("B2", "Annual Sports & Cultural Festival", "University Life", "Join the inter-departmental championship next week at the main stadium."),
        AnnouncementBanner("B3", "Global University Exchange Program", "Academics", "Applications are now open for semester exchange at partner European universities.")
    )

    private fun getSampleActivities(): List<ActivityItem> = listOf(
        ActivityItem("ACT1", "Scanned Class Attendance via QR", "Attendance", "Today, 11:02 AM", ActivityType.ATTENDANCE),
        ActivityItem("ACT2", "Submitted CSE-305 Spanner Assignment", "Assignment", "Yesterday, 08:30 PM", ActivityType.ASSIGNMENT),
        ActivityItem("ACT3", "Semester Fee Receipt Generated", "Payment", "Feb 10, 2026", ActivityType.PAYMENT),
        ActivityItem("ACT4", "Spring 2026 Midterm Routine Published", "Notice", "Feb 08, 2026", ActivityType.NOTICE)
    )

    private fun getSampleNotifications(): List<NotificationItem> = listOf(
        NotificationItem("NOT1", "Attendance Verified", "Your attendance for CSE-301 has been logged successfully.", NotificationCategory.ACADEMIC, "10 mins ago", false),
        NotificationItem("NOT2", "Assignment Graded", "Dr. Sarah Jenkins graded your Lab 3 assignment: 98/100.", NotificationCategory.ACADEMIC, "1 hour ago", false),
        NotificationItem("NOT3", "Library Book Due Reminder", "Thomas Cormen 4th Ed is due in 2 days.", NotificationCategory.LIBRARY, "5 hours ago", false),
        NotificationItem("NOT4", "Fee Payment Received", "Transaction ID TXN-994812 confirmed ($450.00).", NotificationCategory.FINANCE, "Yesterday", true)
    )
}
