package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.Assignment
import com.example.domain.model.AssignmentStatus
import com.example.domain.model.AssignmentSubmission
import com.example.domain.model.SubmissionStatus

@Entity(tableName = "assignments")
data class EnterpriseAssignmentEntity(
    @PrimaryKey val assignmentId: String,
    val courseId: String,
    val courseName: String,
    val teacherId: String,
    val teacherName: String,
    val title: String,
    val description: String,
    val instructions: String,
    val subject: String,
    val department: String,
    val semester: String,
    val section: String,
    val deadline: Long,
    val allowLateSubmission: Boolean,
    val latePenaltyPercentagePerDay: Double,
    val maximumMarks: Double,
    val passingMarks: Double,
    val allowedFileTypes: String, // Comma separated
    val totalSubmissions: Int,
    val status: String,
    val createdAt: Long,
    val updatedAt: Long
)

fun EnterpriseAssignmentEntity.toDomain(): Assignment {
    return Assignment(
        assignmentId = assignmentId,
        courseId = courseId,
        courseName = courseName,
        teacherId = teacherId,
        teacherName = teacherName,
        title = title,
        description = description,
        instructions = instructions,
        subject = subject,
        department = department,
        semester = semester,
        section = section,
        deadline = deadline,
        allowLateSubmission = allowLateSubmission,
        latePenaltyPercentagePerDay = latePenaltyPercentagePerDay,
        maximumMarks = maximumMarks,
        passingMarks = passingMarks,
        allowedFileTypes = allowedFileTypes.split(",").map { it.trim() },
        totalSubmissions = totalSubmissions,
        status = try { AssignmentStatus.valueOf(status) } catch (e: Exception) { AssignmentStatus.PUBLISHED },
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Assignment.toEnterpriseEntity(): EnterpriseAssignmentEntity {
    return EnterpriseAssignmentEntity(
        assignmentId = assignmentId,
        courseId = courseId,
        courseName = courseName,
        teacherId = teacherId,
        teacherName = teacherName,
        title = title,
        description = description,
        instructions = instructions,
        subject = subject,
        department = department,
        semester = semester,
        section = section,
        deadline = deadline,
        allowLateSubmission = allowLateSubmission,
        latePenaltyPercentagePerDay = latePenaltyPercentagePerDay,
        maximumMarks = maximumMarks,
        passingMarks = passingMarks,
        allowedFileTypes = allowedFileTypes.joinToString(","),
        totalSubmissions = totalSubmissions,
        status = status.name,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

@Entity(tableName = "assignment_submissions")
data class AssignmentSubmissionEntity(
    @PrimaryKey val submissionId: String,
    val assignmentId: String,
    val studentId: String,
    val studentName: String,
    val rollNumber: String,
    val submittedAt: Long,
    val status: String,
    val attemptNumber: Int,
    val isLate: Boolean,
    val obtainedMarks: Double,
    val teacherFeedback: String,
    val teacherRemarks: String,
    val evaluationDate: Long?,
    val isSyncedWithServer: Boolean = true
)

fun AssignmentSubmissionEntity.toDomain(): AssignmentSubmission {
    return AssignmentSubmission(
        submissionId = submissionId,
        assignmentId = assignmentId,
        studentId = studentId,
        studentName = studentName,
        rollNumber = rollNumber,
        submittedAt = submittedAt,
        status = try { SubmissionStatus.valueOf(status) } catch (e: Exception) { SubmissionStatus.SUBMITTED },
        attemptNumber = attemptNumber,
        isLate = isLate,
        obtainedMarks = obtainedMarks,
        teacherFeedback = teacherFeedback,
        teacherRemarks = teacherRemarks,
        evaluationDate = evaluationDate
    )
}

fun AssignmentSubmission.toEntity(isSynced: Boolean = true): AssignmentSubmissionEntity {
    return AssignmentSubmissionEntity(
        submissionId = submissionId,
        assignmentId = assignmentId,
        studentId = studentId,
        studentName = studentName,
        rollNumber = rollNumber,
        submittedAt = submittedAt,
        status = status.name,
        attemptNumber = attemptNumber,
        isLate = isLate,
        obtainedMarks = obtainedMarks,
        teacherFeedback = teacherFeedback,
        teacherRemarks = teacherRemarks,
        evaluationDate = evaluationDate,
        isSyncedWithServer = isSynced
    )
}
