package com.example.domain.model.admission

enum class AdmissionSessionStatus {
    UPCOMING, OPEN, CLOSED, ARCHIVED
}

data class AdmissionSession(
    val sessionId: String = "",
    val name: String = "",
    val academicYear: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val status: AdmissionSessionStatus = AdmissionSessionStatus.OPEN,
    val departments: List<String> = emptyList(),
    val admissionFee: Double = 1500.0,
    val requirements: String = "",
    val sscMinGpa: Double = 3.5,
    val hscMinGpa: Double = 3.5,
    val createdAt: Long = System.currentTimeMillis()
)

enum class QuotaType {
    GENERAL, FREEDOM_FIGHTER, TRIBAL, PHYSICAL_DISABILITY, SPORTS
}

enum class ApplicationStatus {
    DRAFT,
    SUBMITTED,
    UNDER_REVIEW,
    DOCUMENT_VERIFICATION,
    ELIGIBLE,
    NOT_ELIGIBLE,
    MERIT_LISTED,
    WAITING_LIST,
    APPROVED,
    REJECTED,
    CANCELLED
}

enum class PaymentStatus {
    UNPAID, PAID, REFUNDED, PENDING
}

enum class DocumentType {
    PASSPORT_PHOTO,
    SIGNATURE,
    SSC_MARKSHEET,
    SSC_CERTIFICATE,
    HSC_MARKSHEET,
    HSC_CERTIFICATE,
    BIRTH_CERTIFICATE,
    NID_PASSPORT,
    QUOTA_CERTIFICATE,
    CHARACTER_CERTIFICATE,
    MEDICAL_CERTIFICATE
}

enum class DocVerificationStatus {
    PENDING, VERIFIED, REJECTED
}

data class AdmissionDocument(
    val docId: String = "",
    val docType: DocumentType = DocumentType.PASSPORT_PHOTO,
    val fileName: String = "",
    val fileUrl: String = "",
    val fileSizeKb: Long = 0,
    val status: DocVerificationStatus = DocVerificationStatus.PENDING,
    val verificationNote: String = ""
)

data class AcademicInfo(
    val board: String = "",
    val rollNumber: String = "",
    val registrationNumber: String = "",
    val passingYear: String = "",
    val gpa: Double = 0.0,
    val group: String = "Science", // Science, Commerce, Arts
    val instituteName: String = ""
)

data class AdmissionApplication(
    val applicationId: String = "",
    val applicantId: String = "",
    val sessionId: String = "",
    val sessionName: String = "",
    // Personal Information
    val fullName: String = "",
    val fatherName: String = "",
    val motherName: String = "",
    val dateOfBirth: String = "",
    val gender: String = "Male",
    val nationality: String = "Bangladeshi",
    val religion: String = "Islam",
    val bloodGroup: String = "A+",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    // Academic Information
    val sscInfo: AcademicInfo = AcademicInfo(),
    val hscInfo: AcademicInfo = AcademicInfo(),
    // Choices & Quota
    val departmentChoice1: String = "",
    val departmentChoice2: String = "",
    val quotaType: QuotaType = QuotaType.GENERAL,
    // Documents & Payment
    val documents: List<AdmissionDocument> = emptyList(),
    val paymentStatus: PaymentStatus = PaymentStatus.UNPAID,
    val paymentTransactionId: String = "",
    val paymentMethod: String = "", // bKash, Nagad, Rocket, Card
    val applicationStatus: ApplicationStatus = ApplicationStatus.DRAFT,
    val meritScore: Double = 0.0,
    val meritPosition: Int = 0,
    val rollNumber: String = "",
    val admitCardUrl: String = "",
    val verificationNote: String = "",
    val submittedAt: Long = 0L,
    val updatedAt: Long = System.currentTimeMillis()
)

data class MeritList(
    val listId: String = "",
    val sessionId: String = "",
    val department: String = "",
    val title: String = "",
    val publishedAt: Long = System.currentTimeMillis(),
    val sscWeight: Double = 0.3,
    val hscWeight: Double = 0.4,
    val testScoreWeight: Double = 0.3,
    val quotaBonusScore: Double = 2.0,
    val items: List<MeritListItem> = emptyList()
)

data class MeritListItem(
    val applicationId: String = "",
    val rollNumber: String = "",
    val applicantName: String = "",
    val meritPosition: Int = 0,
    val totalScore: Double = 0.0,
    val sscGpa: Double = 0.0,
    val hscGpa: Double = 0.0,
    val quotaType: QuotaType = QuotaType.GENERAL,
    val isWaitingList: Boolean = false,
    val status: ApplicationStatus = ApplicationStatus.MERIT_LISTED
)

data class AdmitCard(
    val admitCardId: String = "",
    val applicationId: String = "",
    val rollNumber: String = "",
    val applicantName: String = "",
    val fatherName: String = "",
    val photoUrl: String = "",
    val examDate: String = "2026-09-15 10:00 AM",
    val examCenter: String = "Academic Building 1, Hall A",
    val departmentChoices: List<String> = emptyList(),
    val qrCodePayload: String = "",
    val instructions: List<String> = listOf(
        "Bring printed copy of this Admit Card.",
        "Bring original HSC Registration Card / NID.",
        "Arrive at exam center 30 minutes prior.",
        "Mobile phones & electronic devices are strictly prohibited."
    )
)

data class AdmissionAnalytics(
    val totalApplicants: Int = 0,
    val departmentWiseCounts: Map<String, Int> = emptyMap(),
    val paymentSuccessRate: Double = 0.0,
    val quotaDistribution: Map<String, Int> = emptyMap(),
    val genderDistribution: Map<String, Int> = emptyMap(),
    val statusDistribution: Map<String, Int> = emptyMap(),
    val admissionConversionRate: Double = 0.0
)
