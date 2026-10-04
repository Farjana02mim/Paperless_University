package com.example.domain.model.result

enum class ResultStatus {
    DRAFT,
    SUBMITTED,
    VERIFIED_BY_DEPT,
    APPROVED_BY_ADMIN,
    PUBLISHED,
    LOCKED
}

enum class AcademicStanding {
    DEANS_LIST,
    EXCELLENT,
    GOOD,
    SATISFACTORY,
    ACADEMIC_WARNING,
    PROBATION
}

data class GradeRule(
    val letterGrade: String,
    val gradePoint: Double,
    val minPercentage: Double,
    val maxPercentage: Double,
    val remarks: String
)

data class GradingPolicy(
    val policyId: String = "DEFAULT_2026",
    val name: String = "Standard University 4.0 Scale",
    val rules: List<GradeRule> = listOf(
        GradeRule("A+", 4.00, 80.0, 100.0, "Outstanding"),
        GradeRule("A", 3.75, 75.0, 79.99, "Excellent"),
        GradeRule("A-", 3.50, 70.0, 74.99, "Very Good"),
        GradeRule("B+", 3.25, 65.0, 69.99, "Good"),
        GradeRule("B", 3.00, 60.0, 64.99, "Above Average"),
        GradeRule("B-", 2.75, 55.0, 59.99, "Average"),
        GradeRule("C+", 2.50, 50.0, 54.99, "Below Average"),
        GradeRule("C", 2.25, 45.0, 49.99, "Pass"),
        GradeRule("D", 2.00, 40.0, 44.99, "Conditional Pass"),
        GradeRule("F", 0.00, 0.0, 39.99, "Fail")
    )
)

data class CourseResult(
    val resultId: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val rollNumber: String = "",
    val department: String = "",
    val faculty: String = "",
    val semester: String = "",
    val academicYear: String = "2025-2026",
    val courseId: String = "",
    val courseCode: String = "",
    val courseTitle: String = "",
    val creditHours: Double = 3.0,
    val teacherId: String = "",
    val teacherName: String = "",
    val attendanceMarks: Double = 0.0,
    val assignmentMarks: Double = 0.0,
    val quizMarks: Double = 0.0,
    val midMarks: Double = 0.0,
    val labMarks: Double = 0.0,
    val finalMarks: Double = 0.0,
    val totalMarks: Double = 0.0,
    val letterGrade: String = "F",
    val gradePoint: Double = 0.0,
    val remarks: String = "",
    val isRetake: Boolean = false,
    val isImprovement: Boolean = false,
    val published: Boolean = false,
    val status: ResultStatus = ResultStatus.DRAFT,
    val publishedAt: Long = 0L,
    val updatedAt: Long = System.currentTimeMillis()
)

data class SemesterResult(
    val semesterId: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val department: String = "",
    val semester: String = "",
    val academicYear: String = "2025-2026",
    val courseResults: List<CourseResult> = emptyList(),
    val registeredCredits: Double = 0.0,
    val earnedCredits: Double = 0.0,
    val semesterGPA: Double = 0.0,
    val semesterCGPA: Double = 0.0,
    val academicStanding: AcademicStanding = AcademicStanding.SATISFACTORY,
    val rankInBatch: Int = 1,
    val totalStudentsInBatch: Int = 60,
    val publishStatus: ResultStatus = ResultStatus.PUBLISHED,
    val publishedAt: Long = System.currentTimeMillis()
)

data class TranscriptModel(
    val transcriptId: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val rollNumber: String = "",
    val department: String = "",
    val faculty: String = "Faculty of Science & Engineering",
    val degreeTitle: String = "Bachelor of Science in Computer Science & Engineering",
    val enrollmentYear: String = "2022",
    val allSemesterResults: List<SemesterResult> = emptyList(),
    val overallCredits: Double = 0.0,
    val overallCGPA: Double = 0.0,
    val graduationStatus: String = "In Progress",
    val generatedAt: Long = System.currentTimeMillis(),
    val verificationCode: String = "",
    val digitalSignature: String = "SHA256-UNI-SIG-VERIFIED"
)

data class UniversityResultAnalytics(
    val department: String = "",
    val totalStudents: Int = 0,
    val passPercentage: Double = 0.0,
    val averageCGPA: Double = 0.0,
    val topPerformers: List<Pair<String, Double>> = emptyList(),
    val gradeDistribution: Map<String, Int> = emptyMap(),
    val gpaTrendPerSemester: Map<String, Double> = emptyMap()
)
