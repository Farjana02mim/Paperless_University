package com.example.domain.model

enum class AttendanceStatus {
    PRESENT,
    LATE,
    ABSENT,
    EXCUSED,
    MEDICAL_LEAVE,
    HOLIDAY
}

data class AttendanceSession(
    val sessionId: String = "",
    val courseId: String = "",
    val courseName: String = "",
    val teacherId: String = "",
    val teacherName: String = "",
    val department: String = "",
    val semester: String = "",
    val classroom: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val allowedRadiusMeters: Double = 30.0,
    val startTime: Long = System.currentTimeMillis(),
    val expireTime: Long = System.currentTimeMillis() + 3600000L, // 1 hour default
    val qrRotationSeconds: Int = 60,
    val secretKey: String = "",
    val isActive: Boolean = true,
    val totalStudentsCount: Int = 0,
    val presentCount: Int = 0,
    val lateCount: Int = 0,
    val absentCount: Int = 0,
    val excusedCount: Int = 0
)

data class QRCodePayload(
    val sessionId: String = "",
    val courseId: String = "",
    val teacherId: String = "",
    val department: String = "",
    val semester: String = "",
    val classroom: String = "",
    val timestamp: Long = 0L,
    val expiresAt: Long = 0L,
    val encryptedToken: String = "",
    val digitalSignature: String = ""
)

data class AttendanceRecord(
    val recordId: String = "",
    val sessionId: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val rollNumber: String = "",
    val courseId: String = "",
    val courseName: String = "",
    val status: AttendanceStatus = AttendanceStatus.PRESENT,
    val scannedAt: Long = System.currentTimeMillis(),
    val verifiedLatitude: Double = 0.0,
    val verifiedLongitude: Double = 0.0,
    val distanceFromClassroomMeters: Double = 0.0,
    val deviceId: String = "",
    val isSynced: Boolean = true,
    val remarks: String = ""
)

data class LeaveRequest(
    val requestId: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val courseId: String = "",
    val courseName: String = "",
    val leaveType: String = "Medical", // Medical, Personal, Official
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long = System.currentTimeMillis(),
    val reason: String = "",
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val reviewedBy: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class AttendanceSummary(
    val totalClasses: Int = 0,
    val attendedClasses: Int = 0,
    val lateClasses: Int = 0,
    val excusedClasses: Int = 0,
    val attendancePercentage: Double = 0.0,
    val subjectWise: List<SubjectAttendanceSummary> = emptyList()
)

data class SubjectAttendanceSummary(
    val courseId: String = "",
    val courseName: String = "",
    val totalClasses: Int = 0,
    val attendedClasses: Int = 0,
    val percentage: Double = 0.0
)

data class DepartmentAttendanceSummary(
    val departmentName: String = "",
    val totalStudents: Int = 0,
    val averageAttendance: Double = 0.0,
    val activeClassesToday: Int = 0
)

data class StudentAttendanceSummary(
    val studentId: String = "",
    val studentName: String = "",
    val rollNumber: String = "",
    val department: String = "",
    val percentage: Double = 0.0,
    val isDefaulter: Boolean = false
)

data class AttendanceAnalytics(
    val overallPercentage: Double = 0.0,
    val defaulterCount: Int = 0,
    val totalSessionsToday: Int = 0,
    val departmentSummaries: List<DepartmentAttendanceSummary> = emptyList(),
    val defaulterStudents: List<StudentAttendanceSummary> = emptyList(),
    val topAttendanceStudents: List<StudentAttendanceSummary> = emptyList()
)

data class AttendancePolicy(
    val policyId: String = "default_policy",
    val minimumPercentageRequired: Double = 75.0,
    val lateThresholdMinutes: Int = 10,
    val defaultQrExpirySeconds: Int = 60,
    val maxRadiusMeters: Double = 30.0,
    val enforceDeviceIdLock: Boolean = true,
    val enforceGpsValidation: Boolean = true
)

data class AttendanceAuditLog(
    val logId: String = "",
    val action: String = "",
    val userId: String = "",
    val userRole: String = "",
    val details: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val flaggedSuspicious: Boolean = false
)
