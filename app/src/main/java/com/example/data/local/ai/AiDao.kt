package com.example.data.local.ai

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AiDao {

    // --- Messages ---
    @Query("SELECT * FROM ai_chat_messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationId: String): Flow<List<AiChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: AiChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<AiChatMessageEntity>)

    @Query("UPDATE ai_chat_messages SET feedback = :isHelpful WHERE messageId = :messageId")
    suspend fun updateMessageFeedback(messageId: String, isHelpful: Boolean)

    @Query("DELETE FROM ai_chat_messages WHERE conversationId = :conversationId")
    suspend fun deleteMessagesForConversation(conversationId: String)

    @Query("DELETE FROM ai_chat_messages")
    suspend fun deleteAllMessages()

    // --- Conversation Sessions ---
    @Query("SELECT * FROM ai_conversation_sessions WHERE userRole = :userRole ORDER BY lastUpdated DESC")
    fun getSessionsForUserRole(userRole: String): Flow<List<ConversationSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ConversationSessionEntity)

    @Query("DELETE FROM ai_conversation_sessions WHERE conversationId = :conversationId")
    suspend fun deleteSession(conversationId: String)

    @Query("DELETE FROM ai_conversation_sessions")
    suspend fun deleteAllSessions()

    // --- Recommendations ---
    @Query("SELECT * FROM ai_recommendations ORDER BY relevanceScore DESC")
    fun getAllRecommendations(): Flow<List<AiRecommendationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecommendations(items: List<AiRecommendationEntity>)

    @Query("UPDATE ai_recommendations SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmarkStatus(id: String, isBookmarked: Boolean)

    // --- Pending Offline Requests ---
    @Query("SELECT * FROM pending_ai_requests ORDER BY timestamp ASC")
    suspend fun getPendingRequests(): List<PendingAiRequestEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingRequest(request: PendingAiRequestEntity)

    @Query("DELETE FROM pending_ai_requests WHERE requestId = :requestId")
    suspend fun deletePendingRequest(requestId: Long)

    @Query("DELETE FROM pending_ai_requests")
    suspend fun clearPendingRequests()
}
