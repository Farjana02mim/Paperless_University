package com.example.data.repository.admin

import com.example.domain.model.admin.*
import com.example.domain.repository.admin.AdminRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID

class AdminRepositoryImpl : AdminRepository {

    private val profileState = MutableStateFlow(AdminProfile())
    private val metricsState = MutableStateFlow(AdminSystemMetrics())

    private val usersState = MutableStateFlow(
        listOf(
            AdminUserItem("USR-1001", "Dr. Sarah Jenkins", "sarah.j@university.edu", UserRole.TEACHER, "Computer Science", UserStatus.ACTIVE, listOf("TEACH_COURSE", "ENTER_MARKS"), "+1-555-0192"),
            AdminUserItem("USR-1002", "Alex Mercer", "alex.m@student.edu", UserRole.STUDENT, "Computer Science", UserStatus.ACTIVE, listOf("VIEW_COURSES", "PAY_FEES"), "+1-555-0193"),
            AdminUserItem("USR-1003", "Prof. Robert Vance", "r.vance@university.edu", UserRole.SUPER_ADMIN, "Office of Registrar", UserStatus.ACTIVE, listOf("ALL_ACCESS"), "+1-555-0100"),
            AdminUserItem("USR-1004", "Marcus Brody", "m.brody@finance.edu", UserRole.FINANCE_OFFICER, "Accounts & Finance", UserStatus.ACTIVE, listOf("MANAGE_FEES", "ISSUE_INVOICES"), "+1-555-0194"),
            AdminUserItem("USR-1005", "Elena Rostova", "elena.r@library.edu", UserRole.LIBRARIAN, "Central Library", UserStatus.ACTIVE, listOf("MANAGE_BOOKS"), "+1-555-0195"),
            AdminUserItem("USR-1006", "David Miller", "david.m@student.edu", UserRole.STUDENT, "Electrical Engineering", UserStatus.DISABLED, listOf("VIEW_COURSES"), "+1-555-0196")
        )
    )

    private val rolesState = MutableStateFlow(
        listOf(
            RolePermission("SUPER_ADMIN", listOf("ALL_ACCESS", "SYSTEM_BACKUP", "SECURITY_LOGS"), 5),
            RolePermission("ADMIN", listOf("MANAGE_USERS", "MANAGE_DEPTS", "MANAGE_COURSES", "VIEW_REPORTS"), 28),
            RolePermission("TEACHER", listOf("TEACH_COURSE", "ENTER_MARKS", "QR_ATTENDANCE", "UPLOAD_RESOURCE"), 3850),
            RolePermission("STUDENT", listOf("VIEW_ENROLLED_COURSES", "VIEW_RESULTS", "PAY_FEES", "REQUEST_LEAVE"), 102450),
            RolePermission("FINANCE_OFFICER", listOf("MANAGE_FEES", "COLLECT_PAYMENTS", "GENERATE_FINANCIAL_REPORTS"), 45),
            RolePermission("LIBRARIAN", listOf("MANAGE_LIBRARY", "CATALOG_BOOKS", "ISSUE_RESOURCES"), 60)
        )
    )

    private val departmentsState = MutableStateFlow(
        listOf(
            AdminDepartment("DPT-101", "CSE", "Computer Science & Engineering", "Dr. Sarah Jenkins", 120, 24500, 180, 4500000.0),
            AdminDepartment("DPT-102", "EEE", "Electrical & Electronic Engineering", "Prof. Alan Turing", 95, 18200, 140, 3800000.0),
            AdminDepartment("DPT-103", "BBA", "Business Administration & Management", "Dr. Gordon Gekko", 80, 21000, 110, 3200000.0),
            AdminDepartment("DPT-104", "CE", "Civil Engineering & Infrastructure", "Prof. Gustave Eiffel", 70, 14500, 95, 2900000.0),
            AdminDepartment("DPT-105", "ME", "Mechanical Engineering & Robotics", "Dr. Nikola Tesla", 110, 19800, 150, 4100000.0)
        )
    )

    private val coursesState = MutableStateFlow(
        listOf(
            AdminCourse("CRS-301", "CSE-301", "Database Management Systems & Distributed Architectures", "Computer Science", 4, "Dr. Sarah Jenkins", 450, "CSE-101 Basics"),
            AdminCourse("CRS-302", "CSE-302", "Artificial Intelligence & Neural Networks", "Computer Science", 4, "Prof. Alan Turing", 380, "CSE-201 Data Structures"),
            AdminCourse("CRS-204", "EEE-204", "Digital Signal Processing & Microcontrollers", "Electrical Engineering", 3, "Dr. Nikola Tesla", 290, "EEE-101 Circuit Theory"),
            AdminCourse("CRS-105", "BBA-105", "Corporate Finance & Enterprise Investment", "Business Administration", 3, "Dr. Gordon Gekko", 520, "None")
        )
    )

    private val applicationsState = MutableStateFlow(
        listOf(
            AdmissionApplication("APP-8801", "Jonathan Wick", "Computer Science", 98.5, 5.00, "APPROVED", "Seat CSE-A-12", "2026-07-28"),
            AdmissionApplication("APP-8802", "Bruce Wayne", "Business Administration", 95.0, 4.90, "APPROVED", "Seat BBA-B-04", "2026-07-29"),
            AdmissionApplication("APP-8803", "Clark Kent", "Electrical Engineering", 88.2, 4.50, "PENDING", "Unassigned", "2026-08-01"),
            AdmissionApplication("APP-8804", "Diana Prince", "Civil Engineering", 92.4, 4.80, "WAITING_LIST", "Unassigned", "2026-08-02")
        )
    )

    private val invoicesState = MutableStateFlow(
        listOf(
            AdminFinanceInvoice("INV-9001", "Alex Mercer", "2024-CSE-042", "Computer Science", "Tuition Fee Semester 5", 1450.00, "2026-08-15", "PAID", "Credit Card"),
            AdminFinanceInvoice("INV-9002", "David Miller", "2024-EEE-019", "Electrical Engineering", "Lab & Library Fee", 320.00, "2026-08-20", "UNPAID", "N/A"),
            AdminFinanceInvoice("INV-9003", "Jonathan Wick", "2026-CSE-101", "Computer Science", "Merit Waiver Grant", 0.00, "2026-08-10", "WAIVED", "Scholarship")
        )
    )

    private val auditLogsState = MutableStateFlow(
        listOf(
            AdminAuditLog("LOG-701", "Prof. Robert Vance", "SUPER_ADMIN", "UPDATE_SYSTEM_POLICY", "Security", "2026-08-06 11:42:10", "192.168.1.10", "Android ERP Admin Station", "Enabled mandatory MFA for all faculty roles."),
            AdminAuditLog("LOG-702", "Marcus Brody", "FINANCE_OFFICER", "BULK_INVOICE_GENERATION", "Finance", "2026-08-06 10:15:00", "192.168.1.45", "Finance Web Portal", "Generated 2,450 semester tuition invoices."),
            AdminAuditLog("LOG-703", "Dr. Sarah Jenkins", "TEACHER", "LOCK_RESULT_MARKS", "Academic", "2026-08-05 16:30:22", "192.168.1.88", "Faculty App", "Locked Midterm grades for CSE-301.")
        )
    )

    private val settingsState = MutableStateFlow(SystemSettings())

    private val aiInsightsState = MutableStateFlow(
        listOf(
            AiInsightsArchitecture(
                modelName = "Student Dropout Risk Predictor",
                description = "Monitors low attendance (<70%) and declining quiz scores to identify students at risk.",
                status = "Ready / Scalable Architecture",
                accuracyPercentage = 94.6,
                predictedRiskStudentsCount = 142,
                lastTrainedDate = "2026-08-01",
                smartRecommendations = listOf("Schedule Academic Counseling for CSE Sem 3", "Trigger Attendance Alerts to Guardians")
            ),
            AiInsightsArchitecture(
                modelName = "Smart Seat & Admission Allocator",
                description = "Optimizes department seat distribution based on historical merit rank distributions.",
                status = "Ready / Scalable Architecture",
                accuracyPercentage = 97.2,
                predictedRiskStudentsCount = 0,
                lastTrainedDate = "2026-07-20",
                smartRecommendations = listOf("Increase CSE quota by 15%", "Reallocate 20 seats from Civil to Robotics")
            )
        )
    )

    private val reportsState = MutableStateFlow(
        listOf(
            AdminReportItem("REP-101", "Q2 University Financial Revenue & Audit", "Finance", "PDF", "2026-08-01", 4250),
            AdminReportItem("REP-102", "Fall 2026 Campus-wide Attendance Summary", "Attendance", "EXCEL", "2026-08-04", 1820),
            AdminReportItem("REP-103", "Student Academic Progression & Transcripts Log", "Academic", "CSV", "2026-08-05", 940)
        )
    )

    override fun getAdminProfile(): Flow<AdminProfile> = profileState.asStateFlow()
    override fun getSystemMetrics(): Flow<AdminSystemMetrics> = metricsState.asStateFlow()

    override fun getUsers(searchQuery: String, roleFilter: String, deptFilter: String): Flow<List<AdminUserItem>> {
        return usersState.map { list ->
            list.filter { user ->
                val matchesQuery = searchQuery.isBlank() ||
                        user.name.contains(searchQuery, ignoreCase = true) ||
                        user.email.contains(searchQuery, ignoreCase = true) ||
                        user.userId.contains(searchQuery, ignoreCase = true)
                val matchesRole = roleFilter == "All" || user.role.name == roleFilter
                val matchesDept = deptFilter == "All" || user.department == deptFilter
                matchesQuery && matchesRole && matchesDept
            }
        }
    }

    override fun getRoles(): Flow<List<RolePermission>> = rolesState.asStateFlow()
    override fun getDepartments(): Flow<List<AdminDepartment>> = departmentsState.asStateFlow()
    override fun getCourses(): Flow<List<AdminCourse>> = coursesState.asStateFlow()
    override fun getAdmissionApplications(): Flow<List<AdmissionApplication>> = applicationsState.asStateFlow()
    override fun getFinanceInvoices(): Flow<List<AdminFinanceInvoice>> = invoicesState.asStateFlow()
    override fun getAuditLogs(): Flow<List<AdminAuditLog>> = auditLogsState.asStateFlow()
    override fun getSystemSettings(): Flow<SystemSettings> = settingsState.asStateFlow()
    override fun getAiInsights(): Flow<List<AiInsightsArchitecture>> = aiInsightsState.asStateFlow()
    override fun getGeneratedReports(): Flow<List<AdminReportItem>> = reportsState.asStateFlow()

    override suspend fun createUser(user: AdminUserItem) {
        val current = usersState.value.toMutableList()
        current.add(0, user)
        usersState.value = current
        val currentMetrics = metricsState.value
        metricsState.value = if (user.role == UserRole.STUDENT) {
            currentMetrics.copy(totalStudents = currentMetrics.totalStudents + 1)
        } else if (user.role == UserRole.TEACHER) {
            currentMetrics.copy(totalTeachers = currentMetrics.totalTeachers + 1)
        } else currentMetrics
    }

    override suspend fun updateUserStatus(userId: String, status: UserStatus) {
        val current = usersState.value.map {
            if (it.userId == userId) it.copy(status = status) else it
        }
        usersState.value = current
    }

    override suspend fun deleteUser(userId: String) {
        usersState.value = usersState.value.filter { it.userId != userId }
    }

    override suspend fun createDepartment(dept: AdminDepartment) {
        val current = departmentsState.value.toMutableList()
        current.add(0, dept)
        departmentsState.value = current
        metricsState.value = metricsState.value.copy(totalDepartments = metricsState.value.totalDepartments + 1)
    }

    override suspend fun createCourse(course: AdminCourse) {
        val current = coursesState.value.toMutableList()
        current.add(0, course)
        coursesState.value = current
        metricsState.value = metricsState.value.copy(totalCourses = metricsState.value.totalCourses + 1)
    }

    override suspend fun updateAdmissionStatus(appId: String, status: String, seat: String) {
        val current = applicationsState.value.map {
            if (it.appId == appId) it.copy(status = status, seatAllocated = if (seat.isBlank()) it.seatAllocated else seat) else it
        }
        applicationsState.value = current
    }

    override suspend fun updateInvoiceStatus(invoiceId: String, status: String) {
        val current = invoicesState.value.map {
            if (it.invoiceId == invoiceId) it.copy(status = status) else it
        }
        invoicesState.value = current
    }

    override suspend fun updateSystemSettings(settings: SystemSettings) {
        settingsState.value = settings
    }

    override suspend fun triggerCloudBackup(): String {
        return "Backup created successfully! Cloud Snapshot #SNAP-${System.currentTimeMillis().toString().takeLast(6)}"
    }

    override suspend fun generateReport(title: String, category: String, format: String): AdminReportItem {
        val report = AdminReportItem(
            reportId = "REP-${UUID.randomUUID().toString().take(6).uppercase()}",
            title = title,
            category = category,
            format = format,
            generatedDate = "Just Now",
            sizeKb = (1200..4500).random().toLong()
        )
        val current = reportsState.value.toMutableList()
        current.add(0, report)
        reportsState.value = current
        return report
    }
}
