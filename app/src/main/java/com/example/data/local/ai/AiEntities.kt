package com.example.data.local.ai

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_chat_messages")
data class AiChatMessageEntity(
    @PrimaryKey val messageId: String,
    val conversationId: String,
    val role: String,
    val content: String,
    val timestamp: Long,
    val category: String,
    val intent: String?,
    val confidence: Float,
    val feedback: Boolean?,
    val isPending: Boolean,
    val isOfflineCached: Boolean
)

@Entity(tableName = "ai_conversation_sessions")
data class ConversationSessionEntity(
    @PrimaryKey val conversationId: String,
    val title: String,
    val category: String,
    val userRole: String,
    val lastUpdated: Long,
    val messageCount: Int,
    val isPinned: Boolean
)

@Entity(tableName = "ai_recommendations")
data class AiRecommendationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,
    val relevanceScore: Float,
    val targetSubject: String?,
    val actionUrl: String?,
    val isBookmarked: Boolean
)

@Entity(tableName = "pending_ai_requests")
data class PendingAiRequestEntity(
    @PrimaryKey(autoGenerate = true) val requestId: Long = 0,
    val conversationId: String,
    val prompt: String,
    val timestamp: Long = System.currentTimeMillis()
)
