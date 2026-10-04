package com.example.data.repository.admission

import com.example.data.local.admission.*
import com.example.domain.model.admission.*
import com.example.domain.repository.admission.AdmissionRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class AdmissionRepositoryImpl(
    private val admissionDao: AdmissionDao,
    private val firestore: FirebaseFirestore?
) : AdmissionRepository {

    private val scope = CoroutineScope(Dispatchers.IO)

    // In-memory fallback / cache for merit lists & admit cards
    private val meritListCache = MutableStateFlow<Map<String, MeritList>>(emptyMap())
    private val admitCardCache = MutableStateFlow<Map<String, AdmitCard>>(emptyMap())

    init {
        scope.launch {
            seedDefaultDataIfEmpty()
        }
    }

    private suspend fun seedDefaultDataIfEmpty() {
        val defaultDepts = JSONArray(listOf("Computer Science & Engineering", "Electrical Engineering", "Business Administration", "Law & Justice", "Pharmacy")).toString()
        val defaultSessions = listOf(
            AdmissionSessionEntity(
                sessionId = "sess_2026_01",
                name = "Undergraduate Admission 2026-2027",
                academicYear = "2026-2027",
                startDate = "2026-08-01",
                endDate = "2026-09-30",
                status = AdmissionSessionStatus.OPEN.name,
                departmentsJson = defaultDepts,
                admissionFee = 1500.0,
                requirements = "SSC & HSC Minimum GPA 3.5 each in Science / Commerce / Arts stream.",
                sscMinGpa = 3.5,
                hscMinGpa = 3.5,
                createdAt = System.currentTimeMillis()
            )
        )
        admissionDao.insertSessions(defaultSessions)

        val sampleDocs = listOf(
            AdmissionDocument("doc_1", DocumentType.PASSPORT_PHOTO, "photo.jpg", "https://picsum.photos/300/300", 250, DocVerificationStatus.VERIFIED, "Clear photo"),
            AdmissionDocument("doc_2", DocumentType.SSC_MARKSHEET, "ssc_marksheet.pdf", "https://www.w3.org/W3C/DesignIssues/PDF.pdf", 1200, DocVerificationStatus.VERIFIED, "Authentic transcript"),
            AdmissionDocument("doc_3", DocumentType.HSC_MARKSHEET, "hsc_marksheet.pdf", "https://www.w3.org/W3C/DesignIssues/PDF.pdf", 1100, DocVerificationStatus.VERIFIED, "Verified with board")
        )

        val sampleApp = AdmissionApplicationEntity(
            applicationId = "APP-2026-8819",
            applicantId = "user_applicant_01",
            sessionId = "sess_2026_01",
            sessionName = "Undergraduate Admission 2026-2027",
            fullName = "Rahim Ahmed",
            fatherName = "Mahmud Ahmed",
            motherName = "Salma Begum",
            dateOfBirth = "2006-05-14",
            gender = "Male",
            nationality = "Bangladeshi",
            religion = "Islam",
            bloodGroup = "B+",
            email = "rahim.admission@example.com",
            phone = "01712345678",
            address = "House 12, Road 5, Dhanmondi, Dhaka",
            sscBoard = "Dhaka",
            sscRoll = "123456",
            sscReg = "987654321",
            sscYear = "2023",
            sscGpa = 5.0,
            sscGroup = "Science",
            sscInstitute = "Dhaka Residential Model College",
            hscBoard = "Dhaka",
            hscRoll = "654321",
            hscReg = "987654321",
            hscYear = "2025",
            hscGpa = 4.92,
            hscGroup = "Science",
            hscInstitute = "Notre Dame College",
            departmentChoice1 = "Computer Science & Engineering",
            departmentChoice2 = "Electrical Engineering",
            quotaType = QuotaType.GENERAL.name,
            documentsJson = serializeDocs(sampleDocs),
            paymentStatus = PaymentStatus.PAID.name,
            paymentTransactionId = "BKASH-TRX-99812",
            paymentMethod = "bKash",
            applicationStatus = ApplicationStatus.MERIT_LISTED.name,
            meritScore = 93.5,
            meritPosition = 12,
            rollNumber = "CSE-2026-0012",
            admitCardUrl = "https://example.com/admit/CSE-2026-0012",
            verificationNote = "Verified by Admission Committee",
            submittedAt = System.currentTimeMillis() - 86400000L * 5,
            updatedAt = System.currentTimeMillis()
        )
        admissionDao.insertApplication(sampleApp)
    }

    override fun getAdmissionSessions(): Flow<List<AdmissionSession>> {
        return admissionDao.getAllSessions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getActiveSession(): Flow<AdmissionSession?> {
        return getAdmissionSessions().map { sessions ->
            sessions.find { it.status == AdmissionSessionStatus.OPEN } ?: sessions.firstOrNull()
        }
    }

    override suspend fun createAdmissionSession(session: AdmissionSession): Result<Unit> {
        return try {
            val entity = session.toEntity()
            admissionDao.insertSession(entity)
            firestore?.collection("admissionSessions")?.document(session.sessionId)?.set(session)?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }

    override fun getUserApplications(applicantId: String): Flow<List<AdmissionApplication>> {
        return admissionDao.getUserApplications(applicantId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getApplicationById(applicationId: String): Flow<AdmissionApplication?> {
        return admissionDao.getApplicationById(applicationId).map { entity ->
            entity?.toDomain()
        }
    }

    override fun getDraftApplication(applicantId: String, sessionId: String): Flow<AdmissionApplication?> {
        return admissionDao.getDraftApplication(applicantId, sessionId).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun saveApplicationDraft(application: AdmissionApplication): Result<Unit> {
        return try {
            val appToSave = if (application.applicationId.isBlank()) {
                application.copy(applicationId = "APP-DRAFT-${UUID.randomUUID().toString().take(6).uppercase()}")
            } else application
            val entity = appToSave.copy(applicationStatus = ApplicationStatus.DRAFT, updatedAt = System.currentTimeMillis()).toEntity()
            admissionDao.insertApplication(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun submitApplication(application: AdmissionApplication): Result<String> {
        return try {
            val finalAppId = if (application.applicationId.isBlank() || application.applicationId.startsWith("APP-DRAFT")) {
                "APP-2026-${(1000..9999).random()}"
            } else application.applicationId

            val roll = "ROLL-${(10000..99999).random()}"

            val submittedApp = application.copy(
                applicationId = finalAppId,
                rollNumber = roll,
                applicationStatus = ApplicationStatus.SUBMITTED,
                submittedAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            admissionDao.insertApplication(submittedApp.toEntity())

            firestore?.collection("applications")?.document(finalAppId)?.set(submittedApp)?.await()

            Result.success(finalAppId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateApplicationStatus(applicationId: String, status: ApplicationStatus, note: String): Result<Unit> {
        return try {
            val entity = admissionDao.getApplicationById(applicationId).firstOrNull()
            if (entity != null) {
                val updated = entity.copy(
                    applicationStatus = status.name,
                    verificationNote = if (note.isNotBlank()) note else entity.verificationNote,
                    updatedAt = System.currentTimeMillis()
                )
                admissionDao.insertApplication(updated)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadDocument(
        applicationId: String,
        docType: DocumentType,
        fileName: String,
        fileBytes: ByteArray
    ): Result<AdmissionDocument> {
        return try {
            val doc = AdmissionDocument(
                docId = "doc_${UUID.randomUUID().toString().take(8)}",
                docType = docType,
                fileName = fileName,
                fileUrl = "https://www.w3.org/W3C/DesignIssues/PDF.pdf",
                fileSizeKb = (fileBytes.size / 1024).toLong().coerceAtLeast(150),
                status = DocVerificationStatus.PENDING,
                verificationNote = "Uploaded by applicant"
            )

            val entity = admissionDao.getApplicationById(applicationId).firstOrNull()
            if (entity != null) {
                val app = entity.toDomain()
                val updatedDocs = app.documents.filterNot { it.docType == docType } + doc
                val updatedEntity = app.copy(documents = updatedDocs).toEntity()
                admissionDao.insertApplication(updatedEntity)
            }

            Result.success(doc)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyDocument(
        applicationId: String,
        docType: DocumentType,
        isVerified: Boolean,
        note: String
    ): Result<Unit> {
        return try {
            val entity = admissionDao.getApplicationById(applicationId).firstOrNull()
            if (entity != null) {
                val app = entity.toDomain()
                val updatedDocs = app.documents.map { doc ->
                    if (doc.docType == docType) {
                        doc.copy(
                            status = if (isVerified) DocVerificationStatus.VERIFIED else DocVerificationStatus.REJECTED,
                            verificationNote = note
                        )
                    } else doc
                }
                val updatedEntity = app.copy(documents = updatedDocs).toEntity()
                admissionDao.insertApplication(updatedEntity)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun processAdmissionPayment(
        applicationId: String,
        paymentMethod: String,
        amount: Double,
        transactionId: String
    ): Result<Boolean> {
        return try {
            val entity = admissionDao.getApplicationById(applicationId).firstOrNull()
            if (entity != null) {
                val updated = entity.copy(
                    paymentStatus = PaymentStatus.PAID.name,
                    paymentMethod = paymentMethod,
                    paymentTransactionId = transactionId,
                    applicationStatus = if (entity.applicationStatus == ApplicationStatus.DRAFT.name) ApplicationStatus.SUBMITTED.name else entity.applicationStatus,
                    updatedAt = System.currentTimeMillis()
                )
                admissionDao.insertApplication(updated)
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun calculateMeritList(
        sessionId: String,
        department: String,
        sscWeight: Double,
        hscWeight: Double,
        testScoreWeight: Double,
        quotaBonus: Double
    ): Result<MeritList> {
        return try {
            val sessionApps = admissionDao.getSessionApplications(sessionId).firstOrNull() ?: emptyList()
            val domainApps = sessionApps.map { it.toDomain() }
                .filter { it.departmentChoice1.contains(department, ignoreCase = true) || it.departmentChoice2.contains(department, ignoreCase = true) }

            val items = domainApps.map { app ->
                val sscNormalized = (app.sscInfo.gpa / 5.0) * 100 * sscWeight
                val hscNormalized = (app.hscInfo.gpa / 5.0) * 100 * hscWeight
                val mockTestScore = (70..98).random().toDouble() * testScoreWeight
                val quotaExtra = if (app.quotaType != QuotaType.GENERAL) quotaBonus else 0.0

                val totalScore = (sscNormalized + hscNormalized + mockTestScore + quotaExtra).coerceAtMost(100.0)

                MeritListItem(
                    applicationId = app.applicationId,
                    rollNumber = if (app.rollNumber.isNotBlank()) app.rollNumber else "ROLL-${(10000..99999).random()}",
                    applicantName = app.fullName,
                    totalScore = Math.round(totalScore * 100.0) / 100.0,
                    sscGpa = app.sscInfo.gpa,
                    hscGpa = app.hscInfo.gpa,
                    quotaType = app.quotaType,
                    isWaitingList = false,
                    status = ApplicationStatus.MERIT_LISTED
                )
            }.sortedByDescending { it.totalScore }
                .mapIndexed { index, item ->
                    val pos = index + 1
                    val isWaiting = pos > 10
                    item.copy(
                        meritPosition = pos,
                        isWaitingList = isWaiting,
                        status = if (isWaiting) ApplicationStatus.WAITING_LIST else ApplicationStatus.MERIT_LISTED
                    )
                }

            val listId = "MERIT_${sessionId}_${department.replace(" ", "_")}"
            val meritList = MeritList(
                listId = listId,
                sessionId = sessionId,
                department = department,
                title = "Official Merit List - $department",
                publishedAt = System.currentTimeMillis(),
                sscWeight = sscWeight,
                hscWeight = hscWeight,
                testScoreWeight = testScoreWeight,
                quotaBonusScore = quotaBonus,
                items = items
            )

            val currentCache = meritListCache.value.toMutableMap()
            currentCache[listId] = meritList
            meritListCache.value = currentCache

            Result.success(meritList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun publishMeritList(meritList: MeritList): Result<Unit> {
        return try {
            val currentCache = meritListCache.value.toMutableMap()
            currentCache[meritList.listId] = meritList
            meritListCache.value = currentCache

            meritList.items.forEach { item ->
                updateApplicationStatus(item.applicationId, item.status)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getMeritList(sessionId: String, department: String): Flow<MeritList?> {
        val listId = "MERIT_${sessionId}_${department.replace(" ", "_")}"
        return meritListCache.map { map ->
            map[listId] ?: map.values.find { it.sessionId == sessionId && it.department == department }
        }
    }

    override suspend fun generateAdmitCard(applicationId: String): Result<AdmitCard> {
        return try {
            val entity = admissionDao.getApplicationById(applicationId).firstOrNull()
            val app = entity?.toDomain()
            val card = AdmitCard(
                admitCardId = "ADMIT-${applicationId.takeLast(6)}",
                applicationId = applicationId,
                rollNumber = app?.rollNumber ?: "CSE-2026-0012",
                applicantName = app?.fullName ?: "Applicant",
                fatherName = app?.fatherName ?: "Father Name",
                photoUrl = app?.documents?.find { it.docType == DocumentType.PASSPORT_PHOTO }?.fileUrl ?: "https://picsum.photos/300/300",
                examDate = "2026-09-15 10:00 AM",
                examCenter = "Academic Building 1, Hall A",
                departmentChoices = listOfNotNull(app?.departmentChoice1, app?.departmentChoice2),
                qrCodePayload = "SMART_CAMPUS_ADMISSION_VERIFY:${app?.applicationId}:${app?.rollNumber}"
            )

            val currentMap = admitCardCache.value.toMutableMap()
            currentMap[applicationId] = card
            admitCardCache.value = currentMap

            Result.success(card)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAdmitCard(applicationId: String): Flow<AdmitCard?> {
        return admitCardCache.map { map ->
            map[applicationId]
        }
    }

    override fun getAllApplications(
        sessionId: String,
        department: String?,
        status: ApplicationStatus?,
        searchQuery: String
    ): Flow<List<AdmissionApplication>> {
        return admissionDao.getSessionApplications(sessionId).map { entities ->
            entities.map { it.toDomain() }.filter { app ->
                val matchesDept = department.isNullOrBlank() || app.departmentChoice1.equals(department, ignoreCase = true) || app.departmentChoice2.equals(department, ignoreCase = true)
                val matchesStatus = status == null || app.applicationStatus == status
                val matchesSearch = searchQuery.isBlank() ||
                        app.fullName.contains(searchQuery, ignoreCase = true) ||
                        app.applicationId.contains(searchQuery, ignoreCase = true) ||
                        app.phone.contains(searchQuery, ignoreCase = true) ||
                        app.rollNumber.contains(searchQuery, ignoreCase = true)

                matchesDept && matchesStatus && matchesSearch
            }
        }
    }

    override fun getAdmissionAnalytics(sessionId: String): Flow<AdmissionAnalytics> {
        return admissionDao.getSessionApplications(sessionId).map { entities ->
            val apps = entities.map { it.toDomain() }
            val total = apps.size.coerceAtLeast(1)

            val deptCounts = mutableMapOf<String, Int>()
            val quotaCounts = mutableMapOf<String, Int>()
            val genderCounts = mutableMapOf<String, Int>()
            val statusCounts = mutableMapOf<String, Int>()

            var paidCount = 0
            var approvedCount = 0

            apps.forEach { app ->
                val dept = app.departmentChoice1.ifBlank { "General" }
                deptCounts[dept] = (deptCounts[dept] ?: 0) + 1

                val q = app.quotaType.name
                quotaCounts[q] = (quotaCounts[q] ?: 0) + 1

                val g = app.gender
                genderCounts[g] = (genderCounts[g] ?: 0) + 1

                val st = app.applicationStatus.name
                statusCounts[st] = (statusCounts[st] ?: 0) + 1

                if (app.paymentStatus == PaymentStatus.PAID) paidCount++
                if (app.applicationStatus == ApplicationStatus.APPROVED || app.applicationStatus == ApplicationStatus.MERIT_LISTED) approvedCount++
            }

            AdmissionAnalytics(
                totalApplicants = total,
                departmentWiseCounts = deptCounts,
                paymentSuccessRate = Math.round((paidCount.toDouble() / total) * 1000.0) / 10.0,
                quotaDistribution = quotaCounts,
                genderDistribution = genderCounts,
                statusDistribution = statusCounts,
                admissionConversionRate = Math.round((approvedCount.toDouble() / total) * 1000.0) / 10.0
            )
        }
    }
}

// JSON Helper functions
private fun serializeDocs(docs: List<AdmissionDocument>): String {
    val arr = JSONArray()
    docs.forEach { doc ->
        val obj = JSONObject()
        obj.put("docId", doc.docId)
        obj.put("docType", doc.docType.name)
        obj.put("fileName", doc.fileName)
        obj.put("fileUrl", doc.fileUrl)
        obj.put("fileSizeKb", doc.fileSizeKb)
        obj.put("status", doc.status.name)
        obj.put("verificationNote", doc.verificationNote)
        arr.put(obj)
    }
    return arr.toString()
}

private fun deserializeDocs(jsonStr: String): List<AdmissionDocument> {
    if (jsonStr.isBlank()) return emptyList()
    return try {
        val arr = JSONArray(jsonStr)
        (0 until arr.length()).map { i ->
            val obj = arr.getJSONObject(i)
            AdmissionDocument(
                docId = obj.optString("docId"),
                docType = try { DocumentType.valueOf(obj.optString("docType")) } catch (e: Exception) { DocumentType.PASSPORT_PHOTO },
                fileName = obj.optString("fileName"),
                fileUrl = obj.optString("fileUrl"),
                fileSizeKb = obj.optLong("fileSizeKb"),
                status = try { DocVerificationStatus.valueOf(obj.optString("status")) } catch (e: Exception) { DocVerificationStatus.PENDING },
                verificationNote = obj.optString("verificationNote")
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}

private fun deserializeStringList(jsonStr: String): List<String> {
    if (jsonStr.isBlank()) return emptyList()
    return try {
        val arr = JSONArray(jsonStr)
        (0 until arr.length()).map { arr.getString(it) }
    } catch (e: Exception) {
        emptyList()
    }
}

// Mappers
fun AdmissionSessionEntity.toDomain(): AdmissionSession {
    val depts = deserializeStringList(departmentsJson)
    return AdmissionSession(
        sessionId = sessionId,
        name = name,
        academicYear = academicYear,
        startDate = startDate,
        endDate = endDate,
        status = try { AdmissionSessionStatus.valueOf(status) } catch (e: Exception) { AdmissionSessionStatus.OPEN },
        departments = depts,
        admissionFee = admissionFee,
        requirements = requirements,
        sscMinGpa = sscMinGpa,
        hscMinGpa = hscMinGpa,
        createdAt = createdAt
    )
}

fun AdmissionSession.toEntity(): AdmissionSessionEntity {
    return AdmissionSessionEntity(
        sessionId = sessionId,
        name = name,
        academicYear = academicYear,
        startDate = startDate,
        endDate = endDate,
        status = status.name,
        departmentsJson = JSONArray(departments).toString(),
        admissionFee = admissionFee,
        requirements = requirements,
        sscMinGpa = sscMinGpa,
        hscMinGpa = hscMinGpa,
        createdAt = createdAt
    )
}

fun AdmissionApplicationEntity.toDomain(): AdmissionApplication {
    val docs = deserializeDocs(documentsJson)
    return AdmissionApplication(
        applicationId = applicationId,
        applicantId = applicantId,
        sessionId = sessionId,
        sessionName = sessionName,
        fullName = fullName,
        fatherName = fatherName,
        motherName = motherName,
        dateOfBirth = dateOfBirth,
        gender = gender,
        nationality = nationality,
        religion = religion,
        bloodGroup = bloodGroup,
        email = email,
        phone = phone,
        address = address,
        sscInfo = AcademicInfo(board = sscBoard, rollNumber = sscRoll, registrationNumber = sscReg, passingYear = sscYear, gpa = sscGpa, group = sscGroup, instituteName = sscInstitute),
        hscInfo = AcademicInfo(board = hscBoard, rollNumber = hscRoll, registrationNumber = hscReg, passingYear = hscYear, gpa = hscGpa, group = hscGroup, instituteName = hscInstitute),
        departmentChoice1 = departmentChoice1,
        departmentChoice2 = departmentChoice2,
        quotaType = try { QuotaType.valueOf(quotaType) } catch (e: Exception) { QuotaType.GENERAL },
        documents = docs,
        paymentStatus = try { PaymentStatus.valueOf(paymentStatus) } catch (e: Exception) { PaymentStatus.UNPAID },
        paymentTransactionId = paymentTransactionId,
        paymentMethod = paymentMethod,
        applicationStatus = try { ApplicationStatus.valueOf(applicationStatus) } catch (e: Exception) { ApplicationStatus.DRAFT },
        meritScore = meritScore,
        meritPosition = meritPosition,
        rollNumber = rollNumber,
        admitCardUrl = admitCardUrl,
        verificationNote = verificationNote,
        submittedAt = submittedAt,
        updatedAt = updatedAt
    )
}

fun AdmissionApplication.toEntity(): AdmissionApplicationEntity {
    return AdmissionApplicationEntity(
        applicationId = applicationId,
        applicantId = applicantId,
        sessionId = sessionId,
        sessionName = sessionName,
        fullName = fullName,
        fatherName = fatherName,
        motherName = motherName,
        dateOfBirth = dateOfBirth,
        gender = gender,
        nationality = nationality,
        religion = religion,
        bloodGroup = bloodGroup,
        email = email,
        phone = phone,
        address = address,
        sscBoard = sscInfo.board,
        sscRoll = sscInfo.rollNumber,
        sscReg = sscInfo.registrationNumber,
        sscYear = sscInfo.passingYear,
        sscGpa = sscInfo.gpa,
        sscGroup = sscInfo.group,
        sscInstitute = sscInfo.instituteName,
        hscBoard = hscInfo.board,
        hscRoll = hscInfo.rollNumber,
        hscReg = hscInfo.registrationNumber,
        hscYear = hscInfo.passingYear,
        hscGpa = hscInfo.gpa,
        hscGroup = hscInfo.group,
        hscInstitute = hscInfo.instituteName,
        departmentChoice1 = departmentChoice1,
        departmentChoice2 = departmentChoice2,
        quotaType = quotaType.name,
        documentsJson = serializeDocs(documents),
        paymentStatus = paymentStatus.name,
        paymentTransactionId = paymentTransactionId,
        paymentMethod = paymentMethod,
        applicationStatus = applicationStatus.name,
        meritScore = meritScore,
        meritPosition = meritPosition,
        rollNumber = rollNumber,
        admitCardUrl = admitCardUrl,
        verificationNote = verificationNote,
        submittedAt = submittedAt,
        updatedAt = updatedAt
    )
}
