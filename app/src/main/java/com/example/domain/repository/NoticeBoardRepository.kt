package com.example.domain.repository

import com.example.core.common.Resource
import com.example.domain.model.Notice
import com.example.domain.model.NoticeComment
import com.example.domain.model.SmartNotification
import kotlinx.coroutines.flow.Flow

interface NoticeBoardRepository {
    fun getNotices(): Flow<Resource<List<Notice>>>
    fun getNoticeById(noticeId: String): Flow<Resource<Notice?>>
    suspend fun createNotice(notice: Notice): Resource<Unit>
    suspend fun updateNotice(notice: Notice): Resource<Unit>
    suspend fun deleteNotice(noticeId: String): Resource<Unit>
    suspend fun togglePinNotice(noticeId: String, isPinned: Boolean): Resource<Unit>
    suspend fun toggleBookmark(noticeId: String): Resource<Boolean>
    suspend fun markNoticeAsRead(noticeId: String): Resource<Unit>
    fun getNotifications(): Flow<List<SmartNotification>>
    suspend fun sendNotification(notification: SmartNotification): Resource<Unit>
    suspend fun markNotificationRead(id: String): Resource<Unit>
    suspend fun markAllNotificationsRead(): Resource<Unit>
    suspend fun addComment(noticeId: String, content: String, authorName: String): Resource<Unit>
    fun getComments(noticeId: String): Flow<List<NoticeComment>>
    suspend fun incrementViewCount(noticeId: String): Resource<Unit>
    suspend fun incrementDownloadCount(noticeId: String): Resource<Unit>
    suspend fun incrementShareCount(noticeId: String): Resource<Unit>
}
