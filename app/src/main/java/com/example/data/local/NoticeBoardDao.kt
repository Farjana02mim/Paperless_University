package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoticeBoardDao {

    @Query("SELECT * FROM notices_cache ORDER BY isPinned DESC, publishDate DESC")
    fun getAllCachedNotices(): Flow<List<NoticeBoardEntity>>

    @Query("SELECT * FROM notices_cache WHERE noticeId = :noticeId LIMIT 1")
    fun getNoticeById(noticeId: String): Flow<NoticeBoardEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotices(notices: List<NoticeBoardEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotice(notice: NoticeBoardEntity)

    @Query("DELETE FROM notices_cache WHERE noticeId = :noticeId")
    suspend fun deleteNotice(noticeId: String)

    @Query("DELETE FROM notices_cache")
    suspend fun clearAllNotices()

    // Bookmarks
    @Query("SELECT noticeId FROM notice_bookmarks")
    fun getBookmarkedNoticeIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: NoticeBookmarkEntity)

    @Query("DELETE FROM notice_bookmarks WHERE noticeId = :noticeId")
    suspend fun removeBookmark(noticeId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM notice_bookmarks WHERE noticeId = :noticeId)")
    suspend fun isBookmarked(noticeId: String): Boolean

    // Read History
    @Query("SELECT noticeId FROM notice_read_history")
    fun getReadNoticeIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun markNoticeAsRead(read: NoticeReadEntity)

    // Notifications
    @Query("SELECT * FROM fcm_notifications_cache ORDER BY createdAt DESC")
    fun getAllNotifications(): Flow<List<NotificationFcmEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationFcmEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationFcmEntity>)

    @Query("UPDATE fcm_notifications_cache SET isRead = 1 WHERE notificationId = :id")
    suspend fun markNotificationAsRead(id: String)

    @Query("UPDATE fcm_notifications_cache SET isRead = 1")
    suspend fun markAllNotificationsAsRead()

    @Query("DELETE FROM fcm_notifications_cache WHERE notificationId = :id")
    suspend fun deleteNotification(id: String)
}
