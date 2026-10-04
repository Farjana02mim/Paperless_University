package com.example.data.repository.teacher

import com.example.domain.model.teacher.*
import com.example.domain.repository.teacher.TeacherRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID

class TeacherRepositoryImpl(
    private val firestore: FirebaseFirestore? = null
) : TeacherRepository {

    private val profileState = MutableStateFlow(
        TeacherProfile(
            teacherId = "T-88219",
            fullName = "Dr. Sarah Jenkins",
            designation = "Senior Associate Professor",
            department = "Computer Science & Engineering",
            email = "sarah.jenkins@university.edu",
            phone = "+1 (555) 234-5678",
            officeRoom = "Building 3, Room 402",
            photoUrl = "https://picsum.photos/300/300",
            researchAreas = listOf("Artificial Intelligence", "Distributed Systems", "Cloud Computing"),
            joinYear = "2018",
            rating = 4.9,
            totalStudentsTaught = 1450,
            isVerified = true
        )
    )

    private val coursesState = MutableStateFlow(
        listOf(
            TeacherCourse(
                courseId = "CSE-301",
                courseCode = "CSE-301",
                courseName = "Algorithms & Complexity",
                department = "Computer Science & Engineering",
                semester = "Semester 5",
                section = "Section A",
                totalStudents = 58,
                credits = 3,
                syllabusSummary = "Graph Algorithms, Dynamic Programming, NP-Completeness, Divide & Conquer Strategy.",
                roomNumber = "Lab 402",
                scheduleTime = "Sun & Tue 10:00 AM - 11:30 AM",
                progressPercentage = 72,
                pendingEvaluationsCount = 8,
                averageAttendanceRate = 92.4
            ),
            TeacherCourse(
                courseId = "CSE-405",
                courseCode = "CSE-405",
                courseName = "Distributed Systems & Cloud",
                department = "Computer Science & Engineering",
                semester = "Semester 7",
                section = "Section B",
                totalStudents = 45,
                credits = 4,
                syllabusSummary = "Raft Consensus, Microservices, Kubernetes, Eventual Consistency, RPCs.",
                roomNumber = "Building 2, Room 301",
                scheduleTime = "Mon & Wed 01:30 PM - 03:00 PM",
                progressPercentage = 60,
                pendingEvaluationsCount = 14,
                averageAttendanceRate = 88.0
            ),
            TeacherCourse(
                courseId = "CSE-201",
                courseCode = "CSE-201",
                courseName = "Data Structures & Lab",
                department = "Computer Science & Engineering",
                semester = "Semester 3",
                section = "Section C",
                totalStudents = 62,
                credits = 4,
                syllabusSummary = "Trees, Heaps, Hash Tables, Balanced BSTs, Memory Layouts.",
                roomNumber = "Lab 105",
                scheduleTime = "Thu 09:00 AM - 12:00 PM",
                progressPercentage = 80,
                pendingEvaluationsCount = 0,
                averageAttendanceRate = 94.1
            )
        )
    )

    private val classesState = MutableStateFlow(
        listOf(
            ClassScheduleItem(
                id = "cls_1",
                courseId = "CSE-301",
                courseCode = "CSE-301",
                courseName = "Algorithms & Complexity",
                timeSlot = "10:00 AM - 11:30 AM",
                roomNumber = "Lab 402",
                topicTitle = "Lecture 14: Dynamic Programming - Knapsack Problem",
                status = ScheduleStatus.COMPLETED,
                date = "Today",
                section = "Section A",
                studentCount = 58,
                isAttendanceTaken = true
            ),
            ClassScheduleItem(
                id = "cls_2",
                courseId = "CSE-405",
                courseCode = "CSE-405",
                courseName = "Distributed Systems & Cloud",
                timeSlot = "01:30 PM - 03:00 PM",
                roomNumber = "Building 2, Room 301",
                topicTitle = "Lecture 9: Raft Consensus Algorithm & Leader Election",
                status = ScheduleStatus.UPCOMING,
                date = "Today",
                section = "Section B",
                studentCount = 45,
                isAttendanceTaken = false
            ),
            ClassScheduleItem(
                id = "cls_3",
                courseId = "CSE-201",
                courseCode = "CSE-201",
                courseName = "Data Structures & Lab",
                timeSlot = "03:15 PM - 04:45 PM",
                roomNumber = "Lab 105",
                topicTitle = "Lab Session 8: Red-Black Trees Implementation",
                status = ScheduleStatus.UPCOMING,
                date = "Today",
                section = "Section C",
                studentCount = 62,
                isAttendanceTaken = false
            )
        )
    )

    private val tasksState = MutableStateFlow(
        listOf(
            TeacherTask(
                taskId = "tsk_1",
                title = "Grade CSE-405 Midterm Papers",
                description = "Evaluate 45 answer scripts with rubrics and upload marks to portal.",
                courseName = "CSE-405",
                deadline = "Today, 05:00 PM",
                type = TaskType.EVALUATION,
                isCompleted = false,
                priority = "High"
            ),
            TeacherTask(
                taskId = "tsk_2",
                title = "Submit CSE-301 Quiz 2 Question Paper",
                description = "Prepare 5 problem sets covering Graph BFS/DFS.",
                courseName = "CSE-301",
                deadline = "Tomorrow, 11:59 PM",
                type = TaskType.CLASS,
                isCompleted = false,
                priority = "Medium"
            ),
            TeacherTask(
                taskId = "tsk_3",
                title = "Department Academic Committee Meeting",
                description = "Review 2026-2027 curriculum updates in Conference Room B.",
                courseName = "CSE Dept",
                deadline = "Aug 10, 02:00 PM",
                type = TaskType.MEETING,
                isCompleted = false,
                priority = "High"
            ),
            TeacherTask(
                taskId = "tsk_4",
                title = "Publish Final Assignment 3 Grades",
                description = "Verify lab demonstrator feedback before publishing.",
                courseName = "CSE-201",
                deadline = "Aug 12, 06:00 PM",
                type = TaskType.RESULT,
                isCompleted = true,
                priority = "Low"
            )
        )
    )

    private val notificationsState = MutableStateFlow(
        listOf(
            TeacherNotification(
                notificationId = "notif_1",
                title = "35 Submissions Pending",
                message = "Students submitted Assignment 2 for CSE-405.",
                timestamp = "10 mins ago",
                category = "Assignment",
                isRead = false
            ),
            TeacherNotification(
                notificationId = "notif_2",
                title = "Class Reschedule Approved",
                message = "CSE-301 Thursday lecture rescheduled to 02:00 PM.",
                timestamp = "1 hour ago",
                category = "Class",
                isRead = false
            ),
            TeacherNotification(
                notificationId = "notif_3",
                title = "Faculty Notice Published",
                message = "Grade submission deadline extended to Aug 20.",
                timestamp = "3 hours ago",
                category = "Notice",
                isRead = true
            )
        )
    )

    private val analyticsState = MutableStateFlow(
        TeachingAnalytics(
            totalTeachingHours = 128.0,
            overallAttendanceRate = 92.4,
            avgStudentScore = 84.6,
            courseProgressAvg = 70.5,
            totalAssignmentsEvaluated = 380,
            pendingEvaluations = 14,
            totalClassesConducted = 48,
            studentEngagementScore = 95.2
        )
    )

    private val resourcesState = MutableStateFlow(
        listOf(
            CourseResource(
                resourceId = "res_1",
                courseId = "CSE-301",
                courseCode = "CSE-301",
                title = "Dynamic Programming Master Notes & Examples",
                resourceType = ResourceType.PDF,
                fileUrl = "https://www.w3.org/W3C/DesignIssues/PDF.pdf",
                uploadDate = "2026-08-01",
                sizeKb = 2450,
                downloadsCount = 52
            ),
            CourseResource(
                resourceId = "res_2",
                courseId = "CSE-405",
                courseCode = "CSE-405",
                title = "Distributed Consensus & Raft Slides",
                resourceType = ResourceType.SLIDES,
                fileUrl = "https://www.w3.org/W3C/DesignIssues/PDF.pdf",
                uploadDate = "2026-08-03",
                sizeKb = 4120,
                downloadsCount = 38
            ),
            CourseResource(
                resourceId = "res_3",
                courseId = "CSE-201",
                courseCode = "CSE-201",
                title = "Lab Manual: Red-Black Tree Balancing",
                resourceType = ResourceType.LAB_MANUAL,
                fileUrl = "https://www.w3.org/W3C/DesignIssues/PDF.pdf",
                uploadDate = "2026-08-04",
                sizeKb = 1800,
                downloadsCount = 61
            )
        )
    )

    private val studentsState = MutableStateFlow(
        listOf(
            TeacherStudentItem(
                studentId = "STU-1001",
                studentName = "Arik Rahman",
                rollNumber = "CSE-2023-014",
                department = "Computer Science & Engineering",
                semester = "Semester 5",
                section = "Section A",
                courseCode = "CSE-301",
                attendancePercentage = 95.0,
                avgGrade = "A+",
                assignmentsSubmitted = 5,
                totalAssignments = 5,
                photoUrl = "https://picsum.photos/150/150",
                email = "arik.rahman@student.univ.edu",
                phone = "+1 (555) 901-2211"
            ),
            TeacherStudentItem(
                studentId = "STU-1002",
                studentName = "Tania Sultana",
                rollNumber = "CSE-2023-022",
                department = "Computer Science & Engineering",
                semester = "Semester 5",
                section = "Section A",
                courseCode = "CSE-301",
                attendancePercentage = 88.0,
                avgGrade = "A",
                assignmentsSubmitted = 4,
                totalAssignments = 5,
                photoUrl = "https://picsum.photos/151/151",
                email = "tania.sultana@student.univ.edu",
                phone = "+1 (555) 901-2212"
            ),
            TeacherStudentItem(
                studentId = "STU-1003",
                studentName = "Kazi Mahir",
                rollNumber = "CSE-2022-008",
                department = "Computer Science & Engineering",
                semester = "Semester 7",
                section = "Section B",
                courseCode = "CSE-405",
                attendancePercentage = 92.0,
                avgGrade = "A-",
                assignmentsSubmitted = 4,
                totalAssignments = 4,
                photoUrl = "https://picsum.photos/152/152",
                email = "kazi.mahir@student.univ.edu",
                phone = "+1 (555) 901-2213"
            ),
            TeacherStudentItem(
                studentId = "STU-1004",
                studentName = "Nusrat Jahan",
                rollNumber = "CSE-2024-041",
                department = "Computer Science & Engineering",
                semester = "Semester 3",
                section = "Section C",
                courseCode = "CSE-201",
                attendancePercentage = 78.5,
                avgGrade = "B+",
                assignmentsSubmitted = 3,
                totalAssignments = 4,
                photoUrl = "https://picsum.photos/153/153",
                email = "nusrat.jahan@student.univ.edu",
                phone = "+1 (555) 901-2214"
            )
        )
    )

    private val noticesState = MutableStateFlow(
        listOf(
            TeacherNotice(
                noticeId = "ntc_1",
                title = "Midterm Examination Syllabus & Guidelines",
                content = "Midterm exams for CSE-301 will cover Modules 1 to 4. Closed book exam with 1 page cheat sheet allowed.",
                targetDepartment = "Computer Science & Engineering",
                targetCourse = "CSE-301",
                isPinned = true,
                publishedAt = "2026-08-04 10:00 AM"
            ),
            TeacherNotice(
                noticeId = "ntc_2",
                title = "Lab 402 Maintenance Notice",
                content = "Software updates scheduled for Lab 402 on Friday 2:00 PM. All students must back up local project files.",
                targetDepartment = "Computer Science & Engineering",
                targetCourse = "All",
                isPinned = false,
                publishedAt = "2026-08-02 04:30 PM"
            )
        )
    )

    private val messagesState = MutableStateFlow(
        listOf(
            TeacherMessage(
                messageId = "msg_1",
                senderId = "STU-1001",
                senderName = "Arik Rahman",
                receiverId = "T-88219",
                receiverName = "Dr. Sarah Jenkins",
                content = "Good afternoon Dr. Sarah, I had a question regarding the DP recurrence relation in Assignment 2 Question 3.",
                timestamp = "10:15 AM",
                isRead = false
            ),
            TeacherMessage(
                messageId = "msg_2",
                senderId = "T-88219",
                senderName = "Dr. Sarah Jenkins",
                receiverId = "STU-1001",
                receiverName = "Arik Rahman",
                content = "Hello Arik, please check lecture slide 18 for the base case breakdown.",
                timestamp = "10:20 AM",
                isRead = true
            )
        )
    )

    private val calendarEventsState = MutableStateFlow(
        listOf(
            AcademicCalendarEvent(
                eventId = "evt_1",
                title = "CSE-301 Quiz 2",
                date = "2026-08-10",
                eventType = "Exam",
                description = "In-class 20-minute quiz on Graph BFS/DFS"
            ),
            AcademicCalendarEvent(
                eventId = "evt_2",
                title = "Department Curriculum Review",
                date = "2026-08-14",
                eventType = "Meeting",
                description = "Annual academic senate meeting"
            ),
            AcademicCalendarEvent(
                eventId = "evt_3",
                title = "CSE-405 Project Mid-check",
                date = "2026-08-18",
                eventType = "Deadline",
                description = "Kubernetes deployment demo submission"
            )
        )
    )

    override fun getTeacherProfile(): Flow<TeacherProfile> = profileState.asStateFlow()

    override fun getAssignedCourses(): Flow<List<TeacherCourse>> = coursesState.asStateFlow()

    override fun getTodayClasses(): Flow<List<ClassScheduleItem>> = classesState.asStateFlow()

    override fun getTeacherTasks(): Flow<List<TeacherTask>> = tasksState.asStateFlow()

    override suspend fun toggleTaskCompletion(taskId: String): Result<Unit> {
        val updated = tasksState.value.map { task ->
            if (task.taskId == taskId) task.copy(isCompleted = !task.isCompleted) else task
        }
        tasksState.value = updated
        return Result.success(Unit)
    }

    override suspend fun addTask(task: TeacherTask): Result<Unit> {
        val newTasks = tasksState.value + task
        tasksState.value = newTasks
        return Result.success(Unit)
    }

    override fun getTeacherNotifications(): Flow<List<TeacherNotification>> = notificationsState.asStateFlow()

    override fun getTeachingAnalytics(): Flow<TeachingAnalytics> = analyticsState.asStateFlow()

    override fun getCourseResources(courseId: String?): Flow<List<CourseResource>> {
        return resourcesState.map { list ->
            if (courseId.isNullOrBlank()) list else list.filter { it.courseId == courseId }
        }
    }

    override suspend fun uploadCourseResource(resource: CourseResource): Result<Unit> {
        resourcesState.value = resourcesState.value + resource
        return Result.success(Unit)
    }

    override fun getEnrolledStudents(
        courseId: String?,
        searchQuery: String,
        deptFilter: String,
        semFilter: String
    ): Flow<List<TeacherStudentItem>> {
        return studentsState.map { list ->
            list.filter { item ->
                val matchesCourse = courseId.isNullOrBlank() || item.courseCode.equals(courseId, ignoreCase = true)
                val matchesDept = deptFilter == "All" || item.department.equals(deptFilter, ignoreCase = true)
                val matchesSem = semFilter == "All" || item.semester.equals(semFilter, ignoreCase = true)
                val matchesQuery = searchQuery.isBlank() ||
                        item.studentName.contains(searchQuery, ignoreCase = true) ||
                        item.rollNumber.contains(searchQuery, ignoreCase = true)

                matchesCourse && matchesDept && matchesSem && matchesQuery
            }
        }
    }

    override fun getTeacherNotices(): Flow<List<TeacherNotice>> = noticesState.asStateFlow()

    override suspend fun publishNotice(notice: TeacherNotice): Result<Unit> {
        noticesState.value = listOf(notice) + noticesState.value
        return Result.success(Unit)
    }

    override fun getTeacherMessages(): Flow<List<TeacherMessage>> = messagesState.asStateFlow()

    override suspend fun sendMessage(message: TeacherMessage): Result<Unit> {
        messagesState.value = messagesState.value + message
        return Result.success(Unit)
    }

    override fun getAcademicCalendarEvents(): Flow<List<AcademicCalendarEvent>> = calendarEventsState.asStateFlow()

    override suspend fun rescheduleClass(
        classId: String,
        newTime: String,
        newRoom: String
    ): Result<Unit> {
        classesState.value = classesState.value.map { item ->
            if (item.id == classId) {
                item.copy(
                    timeSlot = newTime,
                    roomNumber = newRoom,
                    status = ScheduleStatus.RESCHEDULED
                )
            } else item
        }
        return Result.success(Unit)
    }

    override suspend fun updateAttendanceStatus(classId: String, isTaken: Boolean): Result<Unit> {
        classesState.value = classesState.value.map { item ->
            if (item.id == classId) {
                item.copy(
                    isAttendanceTaken = isTaken,
                    status = if (isTaken) ScheduleStatus.COMPLETED else item.status
                )
            } else item
        }
        return Result.success(Unit)
    }
}
