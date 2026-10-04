package com.example.domain.model.ai

/**
 * Enterprise AI Platform Data Models.
 */

enum class AiRole {
    USER, ASSISTANT, SYSTEM, DEVELOPER
}

enum class AiCategory {
    ADMISSION, FEE_PAYMENT, ATTENDANCE, ASSIGNMENTS, RESULTS, LIBRARY,
    ROUTINE, DEPARTMENTS, COURSES, TEACHERS, EVENTS, CAMPUS_RULES,
    ACADEMIC_CALENDAR, EMERGENCY_PROCEDURES, GENERAL
}

data class AiCitation(
    val title: String,
    val sourceUrl: String? = null,
    val documentSnippet: String? = null
)

data class AiChatMessage(
    val messageId: String,
    val conversationId: String,
    val role: AiRole,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val category: AiCategory = AiCategory.GENERAL,
    val intent: String? = null,
    val confidence: Float = 0.95f,
    val citations: List<AiCitation> = emptyList(),
    val feedback: Boolean? = null, // true = thumbs up, false = thumbs down
    val isPending: Boolean = false,
    val isOfflineCached: Boolean = false,
    val imageUrl: String? = null
)

data class ConversationSession(
    val conversationId: String,
    val title: String,
    val category: AiCategory = AiCategory.GENERAL,
    val userRole: String = "STUDENT", // STUDENT, TEACHER, ADMIN, APPLICANT
    val lastUpdated: Long = System.currentTimeMillis(),
    val messageCount: Int = 0,
    val isPinned: Boolean = false
)

enum class RecommendationCategory {
    BOOK, COURSE, VIDEO, RESEARCH_PAPER, PRACTICE_MATERIAL, CAMPUS_EVENT, CLUB, INTERNSHIP, SCHOLARSHIP
}

data class AiRecommendationItem(
    val id: String,
    val title: String,
    val description: String,
    val category: RecommendationCategory,
    val relevanceScore: Float, // 0.0 to 1.0
    val targetSubject: String? = null,
    val actionUrl: String? = null,
    val actionText: String = "Explore",
    val isBookmarked: Boolean = false
)

enum class RiskCategory {
    EXCELLENT, GOOD, NEEDS_ATTENTION, HIGH_RISK, CRITICAL
}

data class AcademicRiskAssessment(
    val studentId: String,
    val studentName: String,
    val department: String,
    val semester: String,
    val riskCategory: RiskCategory,
    val riskScore: Int, // 0 to 100
    val attendancePercentage: Double,
    val currentCgpa: Double,
    val missedAssignments: Int,
    val unpaidDues: Boolean,
    val inactiveDays: Int,
    val contributingFactors: List<String>,
    val recommendedInterventions: List<String>
)

data class PredictiveAnalyticsResult(
    val metricName: String,
    val predictedValue: String,
    val confidencePercentage: Int, // e.g., 92%
    val trendDirection: String, // UP, DOWN, STABLE
    val keyInsights: List<String>
)

data class VoiceCommandResult(
    val rawTranscript: String,
    val recognizedIntent: String,
    val targetNavigationRoute: String? = null,
    val confidence: Float = 0.9f,
    val extractedParameters: Map<String, String> = emptyMap()
)

data class OcrScanResult(
    val documentType: String, // STUDENT_ID, ADMISSION_FORM, RECEIPT, BOOK_PAGE, GENERIC
    val rawExtractedText: String,
    val parsedFields: Map<String, String>,
    val confidence: Float = 0.94f,
    val scanTimestamp: Long = System.currentTimeMillis()
)

data class FaceVerificationResult(
    val isFaceDetected: Boolean,
    val isLivenessVerified: Boolean,
    val matchConfidence: Float,
    val verifiedUserId: String? = null,
    val statusMessage: String
)

data class SmartRoutineConfig(
    val department: String,
    val semester: String,
    val conflictFreeScore: Int, // 0 to 100
    val teacherConflictResolved: Boolean,
    val roomConflictResolved: Boolean,
    val lunchBreakPreserved: Boolean,
    val generatedScheduleSummary: String
)

enum class PriorityLevel {
    URGENT, IMPORTANT, NORMAL, LOW_PRIORITY
}

data class SmartNotificationItem(
    val id: String,
    val title: String,
    val body: String,
    val priority: PriorityLevel,
    val category: String,
    val scheduledTime: Long,
    val targetRoute: String? = null,
    val aiReasoning: String? = null
)

data class AiAnalyticsDashboardData(
    val predictionAccuracy: Float = 0.93f,
    val totalConversations: Int = 1240,
    val averageResponseTimeMs: Long = 450,
    val riskDistribution: Map<RiskCategory, Int> = emptyMap(),
    val topAskedCategories: List<Pair<String, Int>> = emptyList(),
    val recommendationClickThroughRate: Float = 0.78f
)
