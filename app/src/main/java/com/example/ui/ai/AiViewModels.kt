package com.example.ui.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.ai.*
import com.example.domain.repository.ai.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ViewModels powering Enterprise AI Platform.
 */

data class AiChatUiState(
    val messages: List<AiChatMessage> = emptyList(),
    val currentSessionId: String = "",
    val isLoading: Boolean = false,
    val selectedCategory: AiCategory = AiCategory.GENERAL,
    val userRole: String = "STUDENT",
    val errorMessage: String? = null,
    val isVoiceRecording: Boolean = false,
    val voiceTranscript: String? = null
)

class AiChatViewModel(
    private val aiRepository: AIRepository,
    private val chatRepository: ChatRepository,
    private val voiceRepository: VoiceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiChatUiState())
    val uiState: StateFlow<AiChatUiState> = _uiState.asStateFlow()

    init {
        initNewChatSession()
    }

    fun initNewChatSession(category: AiCategory = AiCategory.GENERAL, role: String = "STUDENT") {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, selectedCategory = category, userRole = role)
            val sessionId = chatRepository.createNewSession("Chat Session", category, role)
            
            // Initial welcome greeting message
            val welcomeMsg = AiChatMessage(
                messageId = UUID.randomUUID().toString(),
                conversationId = sessionId,
                role = AiRole.ASSISTANT,
                content = "Hello! I am your **Smart Campus AI Assistant**.\n" +
                        "How can I help you today with your courses, attendance, fees, or library resources?",
                category = category
            )
            chatRepository.saveMessage(welcomeMsg)

            chatRepository.getMessagesForConversation(sessionId).collect { msgList ->
                _uiState.value = _uiState.value.copy(
                    currentSessionId = sessionId,
                    messages = msgList,
                    isLoading = false
                )
            }
        }
    }

    fun sendMessage(userPrompt: String) {
        val trimmed = userPrompt.trim()
        if (trimmed.isEmpty()) return

        val sessionId = _uiState.value.currentSessionId
        val category = _uiState.value.selectedCategory
        val userRole = _uiState.value.userRole

        val userMsg = AiChatMessage(
            messageId = UUID.randomUUID().toString(),
            conversationId = sessionId,
            role = AiRole.USER,
            content = trimmed,
            category = category
        )

        viewModelScope.launch {
            // Save user message
            chatRepository.saveMessage(userMsg)
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            // Send to AI Repository
            val result = aiRepository.processQuery(
                prompt = trimmed,
                conversationId = sessionId,
                userRole = userRole,
                category = category
            )

            result.fold(
                onSuccess = { assistantMsg ->
                    chatRepository.saveMessage(assistantMsg)
                    _uiState.value = _uiState.value.copy(isLoading = false)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "AI Engine is offline. Request queued for sync."
                    )
                }
            )
        }
    }

    fun submitFeedback(messageId: String, isHelpful: Boolean) {
        viewModelScope.launch {
            chatRepository.submitFeedback(messageId, isHelpful)
        }
    }

    fun toggleVoiceRecording() {
        val nextState = !_uiState.value.isVoiceRecording
        _uiState.value = _uiState.value.copy(isVoiceRecording = nextState)
        if (!nextState) {
            // Simulate voice transcript capture
            val voiceQuery = "Show my pending assignments"
            _uiState.value = _uiState.value.copy(voiceTranscript = voiceQuery)
            sendMessage(voiceQuery)
        }
    }
}

data class AiDashboardUiState(
    val analyticsData: AiAnalyticsDashboardData = AiAnalyticsDashboardData(),
    val strugglingStudents: List<AcademicRiskAssessment> = emptyList(),
    val isLoading: Boolean = false
)

class AiDashboardViewModel(
    private val aiRepository: AIRepository,
    private val predictionRepository: PredictionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiDashboardUiState())
    val uiState: StateFlow<AiDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val analyticsResult = aiRepository.getAnalyticsDashboardData()
            val strugglingResult = predictionRepository.getAllStrugglingStudents()

            _uiState.value = _uiState.value.copy(
                analyticsData = analyticsResult.getOrDefault(AiAnalyticsDashboardData()),
                strugglingStudents = strugglingResult.getOrDefault(emptyList()),
                isLoading = false
            )
        }
    }
}

data class AiStudentAssistantUiState(
    val recommendations: List<AiRecommendationItem> = emptyList(),
    val riskAssessment: AcademicRiskAssessment? = null,
    val isLoading: Boolean = false
)

class AiStudentAssistantViewModel(
    private val recommendationRepository: RecommendationRepository,
    private val predictionRepository: PredictionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiStudentAssistantUiState())
    val uiState: StateFlow<AiStudentAssistantUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val riskResult = predictionRepository.assessAcademicRisk("STU-101")
            
            recommendationRepository.getPersonalizedRecommendations("STU-101", "STUDENT")
                .collect { recs ->
                    _uiState.value = _uiState.value.copy(
                        recommendations = recs,
                        riskAssessment = riskResult.getOrNull(),
                        isLoading = false
                    )
                }
        }
    }

    fun bookmarkItem(id: String, isBookmarked: Boolean) {
        viewModelScope.launch {
            recommendationRepository.bookmarkRecommendation(id, isBookmarked)
        }
    }
}
