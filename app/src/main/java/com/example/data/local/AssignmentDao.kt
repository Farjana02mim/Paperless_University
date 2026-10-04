package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AssignmentDao {
    @Query("SELECT * FROM assignments ORDER BY deadline ASC")
    fun getAllAssignmentsFlow(): Flow<List<EnterpriseAssignmentEntity>>

    @Query("SELECT * FROM assignments WHERE department = :dept AND semester = :sem ORDER BY deadline ASC")
    fun getAssignmentsForStudent(dept: String, sem: String): Flow<List<EnterpriseAssignmentEntity>>

    @Query("SELECT * FROM assignments WHERE teacherId = :teacherId ORDER BY createdAt DESC")
    fun getAssignmentsByTeacher(teacherId: String): Flow<List<EnterpriseAssignmentEntity>>

    @Query("SELECT * FROM assignments WHERE assignmentId = :assignmentId LIMIT 1")
    suspend fun getAssignmentById(assignmentId: String): EnterpriseAssignmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignments(assignments: List<EnterpriseAssignmentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: EnterpriseAssignmentEntity)

    @Query("DELETE FROM assignments WHERE assignmentId = :assignmentId")
    suspend fun deleteAssignment(assignmentId: String)

    // Submissions
    @Query("SELECT * FROM assignment_submissions WHERE studentId = :studentId")
    fun getSubmissionsByStudent(studentId: String): Flow<List<AssignmentSubmissionEntity>>

    @Query("SELECT * FROM assignment_submissions WHERE assignmentId = :assignmentId ORDER BY submittedAt DESC")
    fun getSubmissionsForAssignment(assignmentId: String): Flow<List<AssignmentSubmissionEntity>>

    @Query("SELECT * FROM assignment_submissions WHERE submissionId = :submissionId LIMIT 1")
    suspend fun getSubmissionById(submissionId: String): AssignmentSubmissionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(submission: AssignmentSubmissionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmissions(submissions: List<AssignmentSubmissionEntity>)

    @Query("SELECT * FROM assignment_submissions WHERE isSyncedWithServer = 0")
    suspend fun getUnsyncedSubmissions(): List<AssignmentSubmissionEntity>
}
