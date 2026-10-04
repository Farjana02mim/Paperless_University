package com.example.domain.repository

import com.example.domain.model.*
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {

    suspend fun createSession(session: AttendanceSession): Result<String>
    fun getSessionById(sessionId: String): Flow<AttendanceSession?>
    fun getActiveSessionsByTeacher(teacherId: String): Flow<List<AttendanceSession>>
    fun getAllActiveSessions(): Flow<List<AttendanceSession>>
    suspend fun closeSession(sessionId: String): Result<Unit>

    suspend fun markAttendance(record: AttendanceRecord): Result<AttendanceRecord>
    fun getStudentAttendanceRecords(studentId: String): Flow<List<AttendanceRecord>>
    fun getSessionAttendanceRecords(sessionId: String): Flow<List<AttendanceRecord>>
    suspend fun manualAttendanceCorrection(
        recordId: String,
        sessionId: String,
        studentId: String,
        newStatus: AttendanceStatus,
        remarks: String
    ): Result<Unit>

    suspend fun submitLeaveRequest(leaveRequest: LeaveRequest): Result<String>
    fun getStudentLeaveRequests(studentId: String): Flow<List<LeaveRequest>>
    fun getAllLeaveRequests(): Flow<List<LeaveRequest>>
    suspend fun updateLeaveStatus(requestId: String, status: String, reviewedBy: String): Result<Unit>

    fun getStudentSummary(studentId: String): Flow<AttendanceSummary>
    fun getAttendanceAnalytics(): Flow<AttendanceAnalytics>
    fun getAttendancePolicy(): Flow<AttendancePolicy>
    suspend fun updateAttendancePolicy(policy: AttendancePolicy): Result<Unit>
    fun getAuditLogs(): Flow<List<AttendanceAuditLog>>

    suspend fun syncOfflineRecords(): Result<Int>
}
