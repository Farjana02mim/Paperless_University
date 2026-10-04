package com.example.domain.model

enum class AssignmentStatus {
    DRAFT,
    PUBLISHED,
    CLOSED,
    ARCHIVED
}

enum class SubmissionStatus {
    PENDING,
    SUBMITTED,
    LATE_SUBMITTED,
    UNDER_REVIEW,
    EVALUATED,
    RESUBMISSION_REQUESTED
}

data class RubricCriterion(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val maxMarks: Double = 0.0,
    val obtainedMarks: Double = 0.0,
    val feedback: String = ""
)

data class AssignmentRubric(
    val rubricId: String = "",
    val title: String = "",
    val criteria: List<RubricCriterion> = emptyList()
)

data class AssignmentAttachment(
    val fileId: String = "",
    val fileName: String = "",
    val fileUrl: String = "",
    val fileType: String = "", // PDF, DOCX, ZIP, etc.
    val fileSizeFormatted: String = ""
)

data class PlagiarismReport(
    val reportId: String = "",
    val similarityPercentage: Double = 0.0,
    val status: String = "NOT_CHECKED", // PASSED, FLAGGED, UNDER_REVIEW
    val matchedSourcesCount: Int = 0,
    val reportSummaryUrl: String = ""
)

data class Assignment(
    val assignmentId: String = "",
    val courseId: String = "",
    val courseName: String = "",
    val teacherId: String = "",
    val teacherName: String = "",
    val title: String = "",
    val description: String = "",
    val instructions: String = "",
    val subject: String = "",
    val department: String = "",
    val semester: String = "",
    val section: String = "",
    val attachments: List<AssignmentAttachment> = emptyList(),
    val coverImageUrl: String = "",
    val deadline: Long = System.currentTimeMillis() + 86400000L * 7, // 7 days from now
    val allowLateSubmission: Boolean = true,
    val latePenaltyPercentagePerDay: Double = 5.0,
    val maxLateDays: Int = 3,
    val allowResubmission: Boolean = true,
    val maximumMarks: Double = 100.0,
    val passingMarks: Double = 40.0,
    val rubric: AssignmentRubric = AssignmentRubric(),
    val allowedFileTypes: List<String> = listOf("PDF", "DOCX", "ZIP", "PNG", "JPG"),
    val maximumFileSizeMb: Int = 25,
    val totalSubmissions: Int = 0,
    val status: AssignmentStatus = AssignmentStatus.PUBLISHED,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class AssignmentSubmission(
    val submissionId: String = "",
    val assignmentId: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val rollNumber: String = "",
    val studentPhotoUrl: String = "",
    val submittedFiles: List<AssignmentAttachment> = emptyList(),
    val submittedAt: Long = System.currentTimeMillis(),
    val status: SubmissionStatus = SubmissionStatus.SUBMITTED,
    val attemptNumber: Int = 1,
    val isLate: Boolean = false,
    val latePenaltyDeducted: Double = 0.0,
    val obtainedMarks: Double = 0.0,
    val teacherFeedback: String = "",
    val teacherRemarks: String = "",
    val rubricGrades: List<RubricCriterion> = emptyList(),
    val evaluationDate: Long? = null,
    val gradedBy: String = "",
    val returnedAnnotatedFiles: List<AssignmentAttachment> = emptyList(),
    val plagiarismReport: PlagiarismReport = PlagiarismReport()
)

data class AssignmentDashboardSummary(
    val totalAssignments: Int = 0,
    val pendingCount: Int = 0,
    val submittedCount: Int = 0,
    val lateCount: Int = 0,
    val evaluatedCount: Int = 0,
    val averageScorePercentage: Double = 0.0,
    val nearestDeadlineAssignment: Assignment? = null
)

data class AssignmentAnalytics(
    val courseId: String = "",
    val totalSubmissions: Int = 0,
    val submissionRatePercentage: Double = 0.0,
    val latePercentage: Double = 0.0,
    val averageMarks: Double = 0.0,
    val highestMarks: Double = 0.0,
    val lowestMarks: Double = 0.0,
    val gradeDistribution: Map<String, Int> = emptyMap() // "A", "B", "C", "D", "F"
)
