package com.example.data.local.admission

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "admission_sessions")
data class AdmissionSessionEntity(
    @PrimaryKey val sessionId: String,
    val name: String,
    val academicYear: String,
    val startDate: String,
    val endDate: String,
    val status: String,
    val departmentsJson: String,
    val admissionFee: Double,
    val requirements: String,
    val sscMinGpa: Double,
    val hscMinGpa: Double,
    val createdAt: Long
)

@Entity(tableName = "admission_applications")
data class AdmissionApplicationEntity(
    @PrimaryKey val applicationId: String,
    val applicantId: String,
    val sessionId: String,
    val sessionName: String,
    val fullName: String,
    val fatherName: String,
    val motherName: String,
    val dateOfBirth: String,
    val gender: String,
    val nationality: String,
    val religion: String,
    val bloodGroup: String,
    val email: String,
    val phone: String,
    val address: String,
    val sscBoard: String,
    val sscRoll: String,
    val sscReg: String,
    val sscYear: String,
    val sscGpa: Double,
    val sscGroup: String,
    val sscInstitute: String,
    val hscBoard: String,
    val hscRoll: String,
    val hscReg: String,
    val hscYear: String,
    val hscGpa: Double,
    val hscGroup: String,
    val hscInstitute: String,
    val departmentChoice1: String,
    val departmentChoice2: String,
    val quotaType: String,
    val documentsJson: String,
    val paymentStatus: String,
    val paymentTransactionId: String,
    val paymentMethod: String,
    val applicationStatus: String,
    val meritScore: Double,
    val meritPosition: Int,
    val rollNumber: String,
    val admitCardUrl: String,
    val verificationNote: String,
    val submittedAt: Long,
    val updatedAt: Long
)

@Dao
interface AdmissionDao {
    @Query("SELECT * FROM admission_sessions")
    fun getAllSessions(): Flow<List<AdmissionSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<AdmissionSessionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: AdmissionSessionEntity)

    @Query("SELECT * FROM admission_applications WHERE applicantId = :applicantId ORDER BY updatedAt DESC")
    fun getUserApplications(applicantId: String): Flow<List<AdmissionApplicationEntity>>

    @Query("SELECT * FROM admission_applications WHERE applicationId = :applicationId")
    fun getApplicationById(applicationId: String): Flow<AdmissionApplicationEntity?>

    @Query("SELECT * FROM admission_applications WHERE applicantId = :applicantId AND sessionId = :sessionId AND applicationStatus = 'DRAFT' LIMIT 1")
    fun getDraftApplication(applicantId: String, sessionId: String): Flow<AdmissionApplicationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(application: AdmissionApplicationEntity)

    @Query("DELETE FROM admission_applications WHERE applicationId = :applicationId")
    suspend fun deleteApplication(applicationId: String)

    @Query("SELECT * FROM admission_applications WHERE sessionId = :sessionId")
    fun getSessionApplications(sessionId: String): Flow<List<AdmissionApplicationEntity>>
}
