package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.AttendanceRecord
import com.example.domain.model.AttendanceStatus
import com.example.domain.model.LeaveRequest

@Entity(tableName = "local_attendance_records")
data class LocalAttendanceRecordEntity(
    @PrimaryKey val recordId: String,
    val sessionId: String,
    val studentId: String,
    val studentName: String,
    val rollNumber: String,
    val courseId: String,
    val courseName: String,
    val status: String,
    val scannedAt: Long,
    val verifiedLatitude: Double,
    val verifiedLongitude: Double,
    val distanceFromClassroomMeters: Double,
    val deviceId: String,
    val isSynced: Boolean,
    val remarks: String
) {
    fun toDomainModel(): AttendanceRecord {
        return AttendanceRecord(
            recordId = recordId,
            sessionId = sessionId,
            studentId = studentId,
            studentName = studentName,
            rollNumber = rollNumber,
            courseId = courseId,
            courseName = courseName,
            status = try { AttendanceStatus.valueOf(status) } catch (e: Exception) { AttendanceStatus.PRESENT },
            scannedAt = scannedAt,
            verifiedLatitude = verifiedLatitude,
            verifiedLongitude = verifiedLongitude,
            distanceFromClassroomMeters = distanceFromClassroomMeters,
            deviceId = deviceId,
            isSynced = isSynced,
            remarks = remarks
        )
    }

    companion object {
        fun fromDomainModel(record: AttendanceRecord): LocalAttendanceRecordEntity {
            return LocalAttendanceRecordEntity(
                recordId = record.recordId,
                sessionId = record.sessionId,
                studentId = record.studentId,
                studentName = record.studentName,
                rollNumber = record.rollNumber,
                courseId = record.courseId,
                courseName = record.courseName,
                status = record.status.name,
                scannedAt = record.scannedAt,
                verifiedLatitude = record.verifiedLatitude,
                verifiedLongitude = record.verifiedLongitude,
                distanceFromClassroomMeters = record.distanceFromClassroomMeters,
                deviceId = record.deviceId,
                isSynced = record.isSynced,
                remarks = record.remarks
            )
        }
    }
}

@Entity(tableName = "local_leave_requests")
data class LocalLeaveRequestEntity(
    @PrimaryKey val requestId: String,
    val studentId: String,
    val studentName: String,
    val courseId: String,
    val courseName: String,
    val leaveType: String,
    val startDate: Long,
    val endDate: Long,
    val reason: String,
    val status: String,
    val reviewedBy: String,
    val createdAt: Long,
    val isSynced: Boolean
) {
    fun toDomainModel(): LeaveRequest {
        return LeaveRequest(
            requestId = requestId,
            studentId = studentId,
            studentName = studentName,
            courseId = courseId,
            courseName = courseName,
            leaveType = leaveType,
            startDate = startDate,
            endDate = endDate,
            reason = reason,
            status = status,
            reviewedBy = reviewedBy,
            createdAt = createdAt
        )
    }

    companion object {
        fun fromDomainModel(leave: LeaveRequest, isSynced: Boolean = true): LocalLeaveRequestEntity {
            return LocalLeaveRequestEntity(
                requestId = leave.requestId,
                studentId = leave.studentId,
                studentName = leave.studentName,
                courseId = leave.courseId,
                courseName = leave.courseName,
                leaveType = leave.leaveType,
                startDate = leave.startDate,
                endDate = leave.endDate,
                reason = leave.reason,
                status = leave.status,
                reviewedBy = leave.reviewedBy,
                createdAt = leave.createdAt,
                isSynced = isSynced
            )
        }
    }
}
