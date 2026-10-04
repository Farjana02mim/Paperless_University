package com.example.domain.repository

import com.example.domain.model.*
import kotlinx.coroutines.flow.Flow

interface AssignmentRepository {
    fun getAssignmentsForStudent(department: String, semester: String): Flow<List<Assignment>>
    fun getAssignmentsForTeacher(teacherId: String): Flow<List<Assignment>>
    suspend fun getAssignmentById(assignmentId: String): Result<Assignment>
    suspend fun createAssignment(assignment: Assignment): Result<String>
    suspend fun updateAssignment(assignment: Assignment): Result<Unit>
    suspend fun deleteAssignment(assignmentId: String): Result<Unit>

    // Submissions
    fun getSubmissionsByStudent(studentId: String): Flow<List<AssignmentSubmission>>
    fun getSubmissionsForAssignment(assignmentId: String): Flow<List<AssignmentSubmission>>
    suspend fun getSubmissionById(submissionId: String): Result<AssignmentSubmission>
    suspend fun submitAssignment(submission: AssignmentSubmission): Result<String>
    suspend fun evaluateSubmission(
        submissionId: String,
        obtainedMarks: Double,
        teacherFeedback: String,
        teacherRemarks: String,
        rubricGrades: List<RubricCriterion>,
        annotatedFiles: List<AssignmentAttachment>
    ): Result<Unit>

    // Analytics & Summary
    fun getDashboardSummary(studentId: String, department: String, semester: String): Flow<AssignmentDashboardSummary>
    suspend fun getAssignmentAnalytics(courseId: String): Result<AssignmentAnalytics>
    
    // Offline sync
    suspend fun syncUnsyncedSubmissions(): Result<Int>
}
