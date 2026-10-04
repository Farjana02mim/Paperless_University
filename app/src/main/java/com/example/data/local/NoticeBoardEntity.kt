package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.Notice
import com.example.domain.model.NoticeAttachment
import com.example.domain.model.NoticeCategory
import com.example.domain.model.NoticePriority
import com.example.domain.model.NotificationType
import com.example.domain.model.SmartNotification

@Entity(tableName = "notices_cache")
data class NoticeBoardEntity(
    @PrimaryKey val noticeId: String,
    val title: String,
    val description: String,
    val summary: String,
    val category: String,
    val priority: String,
    val department: String,
    val targetRole: String,
    val authorId: String,
    val authorName: String,
    val authorPhoto: String,
    val coverImage: String,
    val publishDate: Long,
    val expiryDate: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val isPinned: Boolean,
    val isPublished: Boolean,
    val viewCount: Int,
    val downloadCount: Int,
    val bookmarkCount: Int,
    val shareCount: Int,
    val allowComments: Boolean,
    val tagsRaw: String // Comma separated
) {
    fun toDomainModel(isBookmarked: Boolean = false, isRead: Boolean = false): Notice {
        return Notice(
            noticeId = noticeId,
            title = title,
            description = description,
            summary = summary,
            category = try { NoticeCategory.valueOf(category) } catch (e: Exception) { NoticeCategory.ACADEMIC },
            priority = try { NoticePriority.valueOf(priority) } catch (e: Exception) { NoticePriority.NORMAL },
            department = department,
            targetRole = targetRole,
            authorId = authorId,
            authorName = authorName,
            authorPhoto = authorPhoto,
            attachments = emptyList(), // Attachments loaded separately if needed
            coverImage = coverImage,
            publishDate = publishDate,
            expiryDate = expiryDate,
            createdAt = createdAt,
            updatedAt = updatedAt,
            isPinned = isPinned,
            isPublished = isPublished,
            viewCount = viewCount,
            downloadCount = downloadCount,
            bookmarkCount = bookmarkCount,
            shareCount = shareCount,
            allowComments = allowComments,
            tags = if (tagsRaw.isBlank()) emptyList() else tagsRaw.split(","),
            isBookmarked = isBookmarked,
            isRead = isRead
        )
    }

    companion object {
        fun fromDomainModel(notice: Notice): NoticeBoardEntity {
            return NoticeBoardEntity(
                noticeId = notice.noticeId,
                title = notice.title,
                description = notice.description,
                summary = notice.summary,
                category = notice.category.name,
                priority = notice.priority.name,
                department = notice.department,
                targetRole = notice.targetRole,
                authorId = notice.authorId,
                authorName = notice.authorName,
                authorPhoto = notice.authorPhoto,
                coverImage = notice.coverImage,
                publishDate = notice.publishDate,
                expiryDate = notice.expiryDate,
                createdAt = notice.createdAt,
                updatedAt = notice.updatedAt,
                isPinned = notice.isPinned,
                isPublished = notice.isPublished,
                viewCount = notice.viewCount,
                downloadCount = notice.downloadCount,
                bookmarkCount = notice.bookmarkCount,
                shareCount = notice.shareCount,
                allowComments = notice.allowComments,
                tagsRaw = notice.tags.joinToString(",")
            )
        }
    }
}

@Entity(tableName = "notice_bookmarks")
data class NoticeBookmarkEntity(
    @PrimaryKey val noticeId: String,
    val bookmarkedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notice_read_history")
data class NoticeReadEntity(
    @PrimaryKey val noticeId: String,
    val readAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "fcm_notifications_cache")
data class NotificationFcmEntity(
    @PrimaryKey val notificationId: String,
    val title: String,
    val message: String,
    val type: String,
    val receiverRole: String,
    val receiverUid: String,
    val noticeId: String,
    val isRead: Boolean,
    val createdAt: Long,
    val actionType: String,
    val imageUrl: String,
    val deepLink: String
) {
    fun toDomainModel(): SmartNotification {
        return SmartNotification(
            notificationId = notificationId,
            title = title,
            message = message,
            type = try { NotificationType.valueOf(type) } catch (e: Exception) { NotificationType.NEW_NOTICE },
            receiverRole = receiverRole,
            receiverUid = receiverUid,
            noticeId = noticeId,
            isRead = isRead,
            createdAt = createdAt,
            actionType = actionType,
            imageUrl = imageUrl,
            deepLink = deepLink
        )
    }

    companion object {
        fun fromDomainModel(notification: SmartNotification): NotificationFcmEntity {
            return NotificationFcmEntity(
                notificationId = notification.notificationId,
                title = notification.title,
                message = notification.message,
                type = notification.type.name,
                receiverRole = notification.receiverRole,
                receiverUid = notification.receiverUid,
                noticeId = notification.noticeId,
                isRead = notification.isRead,
                createdAt = notification.createdAt,
                actionType = notification.actionType,
                imageUrl = notification.imageUrl,
                deepLink = notification.deepLink
            )
        }
    }
}
