package com.example.domain.model.admin

enum class UserRole {
    SUPER_ADMIN,
    ADMIN,
    TEACHER,
    STUDENT,
    FINANCE_OFFICER,
    LIBRARIAN,
    STAFF
}

enum class UserStatus {
    ACTIVE,
    DISABLED,
    PENDING_APPROVAL
}

data class AdminProfile(
    val adminId: String = "ADM-001",
    val fullName: String = "Prof. Robert Vance",
    val email: String = "r.vance@university.edu",
    val role: UserRole = UserRole.SUPER_ADMIN,
    val officeRoom: String = "Admin Block Room 101",
    val department: String = "Office of Registrar",
    val permissions: List<String> = listOf("ALL_ACCESS"),
    val mfaEnabled: Boolean = true
)

data class AdminSystemMetrics(
    val totalStudents: Int = 102450,
    val totalTeachers: Int = 3850,
    val totalDepartments: Int = 42,
    val totalCourses: Int = 1280,
    val todayAttendanceRate: Double = 94.2,
    val todayPaymentsTotal: Double = 428500.0,
    val pendingAssignments: Int = 340,
    val publishedResultsCount: Int = 890,
    val admissionApplicationsCount: Int = 15200,
    val libraryResourcesCount: Int = 68400,
    val storageUsageGb: Double = 482.5,
    val systemHealthPercent: Int = 99,
    val activeAlertsCount: Int = 2
)

data class AdminUserItem(
    val userId: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val department: String,
    val status: UserStatus,
    val permissions: List<String>,
    val phone: String,
    val avatarUrl: String = "",
    val createdAt: String = "2024-01-15"
)

data class RolePermission(
    val roleName: String,
    val permissions: List<String>,
    val userCount: Int,
    val isCustom: Boolean = false
)

data class AdminDepartment(
    val deptId: String,
    val code: String,
    val name: String,
    val headOfDepartment: String,
    val totalFaculty: Int,
    val totalStudents: Int,
    val totalCourses: Int,
    val budget: Double,
    val status: String = "Active"
)

data class AdminCourse(
    val courseId: String,
    val code: String,
    val name: String,
    val department: String,
    val credits: Int,
    val assignedTeacherName: String,
    val totalEnrolledStudents: Int,
    val prerequisites: String = "None",
    val status: String = "Active"
)

data class AdmissionApplication(
    val appId: String,
    val applicantName: String,
    val appliedDept: String,
    val meritScore: Double,
    val hscGpa: Double,
    val status: String, // PENDING, APPROVED, REJECTED, WAITING_LIST
    val seatAllocated: String,
    val submissionDate: String
)

data class AdminFinanceInvoice(
    val invoiceId: String,
    val studentName: String,
    val studentRoll: String,
    val department: String,
    val feeCategory: String,
    val amount: Double,
    val dueDate: String,
    val status: String, // PAID, UNPAID, WAIVED, PARTIAL
    val paymentMethod: String = "N/A"
)

data class AdminAuditLog(
    val logId: String,
    val user: String,
    val role: String,
    val action: String,
    val category: String,
    val timestamp: String,
    val ipAddress: String,
    val device: String,
    val details: String
)

data class SystemSettings(
    val universityName: String = "Smart University Portal",
    val universityCode: String = "SUP-2026",
    val currentSemester: String = "Fall 2026",
    val defaultGradingPolicy: String = "Standard CGPA 4.0",
    val minAttendancePercentage: Int = 75,
    val autoBackupFrequency: String = "Daily at Midnight",
    val mfaRequired: Boolean = true,
    val maintenanceMode: Boolean = false
)

data class AiInsightsArchitecture(
    val modelName: String,
    val description: String,
    val status: String,
    val accuracyPercentage: Double,
    val predictedRiskStudentsCount: Int,
    val lastTrainedDate: String,
    val smartRecommendations: List<String>
)

data class AdminReportItem(
    val reportId: String,
    val title: String,
    val category: String,
    val format: String, // PDF, EXCEL, CSV, JSON
    val generatedDate: String,
    val sizeKb: Long
)
