package com.example.domain.repository.admission

import com.example.domain.model.admission.*
import kotlinx.coroutines.flow.Flow

interface AdmissionRepository {
    fun getAdmissionSessions(): Flow<List<AdmissionSession>>
    fun getActiveSession(): Flow<AdmissionSession?>
    suspend fun createAdmissionSession(session: AdmissionSession): Result<Unit>

    fun getUserApplications(applicantId: String): Flow<List<AdmissionApplication>>
    fun getApplicationById(applicationId: String): Flow<AdmissionApplication?>
    fun getDraftApplication(applicantId: String, sessionId: String): Flow<AdmissionApplication?>

    suspend fun saveApplicationDraft(application: AdmissionApplication): Result<Unit>
    suspend fun submitApplication(application: AdmissionApplication): Result<String>
    suspend fun updateApplicationStatus(applicationId: String, status: ApplicationStatus, note: String = ""): Result<Unit>

    suspend fun uploadDocument(
        applicationId: String,
        docType: DocumentType,
        fileName: String,
        fileBytes: ByteArray
    ): Result<AdmissionDocument>

    suspend fun verifyDocument(
        applicationId: String,
        docType: DocumentType,
        isVerified: Boolean,
        note: String
    ): Result<Unit>

    suspend fun processAdmissionPayment(
        applicationId: String,
        paymentMethod: String,
        amount: Double,
        transactionId: String
    ): Result<Boolean>

    suspend fun calculateMeritList(
        sessionId: String,
        department: String,
        sscWeight: Double,
        hscWeight: Double,
        testScoreWeight: Double,
        quotaBonus: Double
    ): Result<MeritList>

    suspend fun publishMeritList(meritList: MeritList): Result<Unit>
    fun getMeritList(sessionId: String, department: String): Flow<MeritList?>

    suspend fun generateAdmitCard(applicationId: String): Result<AdmitCard>
    fun getAdmitCard(applicationId: String): Flow<AdmitCard?>

    fun getAllApplications(
        sessionId: String,
        department: String? = null,
        status: ApplicationStatus? = null,
        searchQuery: String = ""
    ): Flow<List<AdmissionApplication>>

    fun getAdmissionAnalytics(sessionId: String): Flow<AdmissionAnalytics>
}
