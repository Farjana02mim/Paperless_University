package com.example.domain.repository.ai

import com.example.domain.model.ai.*
import kotlinx.coroutines.flow.Flow

/**
 * Clean Architecture Repository Interfaces for Enterprise AI Platform.
 */

interface AIRepository {
    suspend fun processQuery(
        prompt: String,
        conversationId: String,
        userRole: String,
        category: AiCategory = AiCategory.GENERAL
    ): Result<AiChatMessage>

    fun streamQueryResponse(
        prompt: String,
        conversationId: String
    ): Flow<String>

    suspend fun getAnalyticsDashboardData(): Result<AiAnalyticsDashboardData>
    suspend fun queueOfflineRequest(prompt: String, conversationId: String)
    suspend fun syncPendingRequests(): Int
}

interface ChatRepository {
    fun getConversationSessions(userRole: String): Flow<List<ConversationSession>>
    fun getMessagesForConversation(conversationId: String): Flow<List<AiChatMessage>>
    suspend fun createNewSession(title: String, category: AiCategory, userRole: String): String
    suspend fun saveMessage(message: AiChatMessage)
    suspend fun deleteConversation(conversationId: String)
    suspend fun clearHistory()
    suspend fun submitFeedback(messageId: String, isHelpful: Boolean)
}

interface RecommendationRepository {
    suspend fun getPersonalizedRecommendations(
        userId: String,
        userRole: String,
        weakSubjects: List<String> = emptyList()
    ): Flow<List<AiRecommendationItem>>

    suspend fun bookmarkRecommendation(id: String, isBookmarked: Boolean)
}

interface PredictionRepository {
    suspend fun assessAcademicRisk(studentId: String): Result<AcademicRiskAssessment>
    suspend fun getAllStrugglingStudents(): Result<List<AcademicRiskAssessment>>
    suspend fun getPredictiveAnalytics(metric: String): Result<PredictiveAnalyticsResult>
    suspend fun generateSmartRoutineConfig(department: String, semester: String): Result<SmartRoutineConfig>
}

interface VoiceRepository {
    suspend fun parseVoiceCommand(audioTranscript: String): Result<VoiceCommandResult>
    suspend fun processOcrDocument(documentType: String, rawText: String): Result<OcrScanResult>
    suspend fun verifyFaceBiometrics(facialDataPayload: String): Result<FaceVerificationResult>
    suspend fun categorizeNotificationPriority(title: String, body: String): Result<SmartNotificationItem>
}
