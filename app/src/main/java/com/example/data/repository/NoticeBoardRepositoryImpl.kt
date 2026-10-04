package com.example.data.repository

import com.example.core.common.Resource
import com.example.data.local.NoticeBoardDao
import com.example.data.local.NoticeBoardEntity
import com.example.data.local.NoticeBookmarkEntity
import com.example.data.local.NoticeReadEntity
import com.example.data.local.NotificationFcmEntity
import com.example.domain.model.Notice
import com.example.domain.model.NoticeAttachment
import com.example.domain.model.NoticeCategory
import com.example.domain.model.NoticeComment
import com.example.domain.model.NoticePriority
import com.example.domain.model.NotificationType
import com.example.domain.model.SmartNotification
import com.example.domain.repository.NoticeBoardRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class NoticeBoardRepositoryImpl(
    private val dao: NoticeBoardDao,
    private val firestore: FirebaseFirestore? = null
) : NoticeBoardRepository {

    private val initialSampleNotices = listOf(
        Notice(
            noticeId = "n-101",
            title = "Final Examination Schedule & Guidelines for Spring 2026",
            summary = "Official timetable and instructions for all undergraduate and postgraduate final exams starting May 15, 2026.",
            description = "All students are requested to review the attached examination timetable carefully. Admit cards must be downloaded from the student portal before May 10. Electronic devices, smartwatches, and programmable calculators are strictly prohibited inside examination halls.",
            category = NoticeCategory.EXAM,
            priority = NoticePriority.CRITICAL,
            department = "Academic Affairs",
            targetRole = "All",
            authorName = "Controller of Examinations",
            isPinned = true,
            isPublished = true,
            viewCount = 1420,
            downloadCount = 890,
            bookmarkCount = 312,
            publishDate = System.currentTimeMillis() - (2 * 3600 * 1000),
            attachments = listOf(
                NoticeAttachment("att-1", "Spring_2026_Exam_Routine.pdf", "https://example.com/exam.pdf", "PDF", "2.4 MB"),
                NoticeAttachment("att-2", "Exam_Hall_Rules.pdf", "https://example.com/rules.pdf", "PDF", "512 KB")
            ),
            tags = listOf("Exam", "Routine", "Spring2026")
        ),
        Notice(
            noticeId = "n-102",
            title = "Merit Scholarship Applications Open for Academic Year 2026-27",
            summary = "Financial assistance and merit awards available for top 10% students across all departments.",
            description = "The Office of Student Financial Aid invites applications for the Chancellor's Merit Scholarship. Eligible students with CGPA >= 3.75 can submit their online applications along with income statements and recommendation letters by June 1, 2026.",
            category = NoticeCategory.SCHOLARSHIP,
            priority = NoticePriority.HIGH,
            department = "Student Welfare",
            targetRole = "Student",
            authorName = "Dean of Student Welfare",
            isPinned = true,
            isPublished = true,
            viewCount = 980,
            downloadCount = 420,
            bookmarkCount = 210,
            publishDate = System.currentTimeMillis() - (5 * 3600 * 1000),
            attachments = listOf(
                NoticeAttachment("att-3", "Scholarship_Form_2026.docx", "https://example.com/form.docx", "DOCX", "180 KB")
            ),
            tags = listOf("Scholarship", "FinancialAid")
        ),
        Notice(
            noticeId = "n-103",
            title = "Annual University Tech Fest 'InnovateX 2026' Registration",
            summary = "Showcase your robotics, AI, and hackathon projects. Prize pool worth $15,000!",
            description = "Department of Computer Science & Engineering presents InnovateX 2026! Featuring 24-hour Hackathon, AI Bot Wars, Project Exhibition, and Gaming Championship. Register your teams before May 20.",
            category = NoticeCategory.EVENTS,
            priority = NoticePriority.NORMAL,
            department = "Computer Science",
            targetRole = "All",
            authorName = "Tech Fest Committee",
            isPinned = false,
            isPublished = true,
            viewCount = 2100,
            downloadCount = 150,
            bookmarkCount = 450,
            publishDate = System.currentTimeMillis() - (24 * 3600 * 1000),
            tags = listOf("TechFest", "Hackathon", "InnovateX")
        ),
        Notice(
            noticeId = "n-104",
            title = "Emergency Maintenance: Central Library Network Offline on Sunday",
            summary = "Network upgrade scheduled for May 10 from 08:00 AM to 02:00 PM.",
            description = "Central Library e-resource portal and Wi-Fi services will be temporarily unavailable due to fiber optic maintenance. Reading rooms will remain open with offline book circulation.",
            category = NoticeCategory.EMERGENCY,
            priority = NoticePriority.HIGH,
            department = "IT Infrastructure",
            targetRole = "All",
            authorName = "Network Operations Center",
            isPinned = false,
            isPublished = true,
            viewCount = 650,
            downloadCount = 10,
            bookmarkCount = 45,
            publishDate = System.currentTimeMillis() - (36 * 3600 * 1000),
            tags = listOf("Maintenance", "Library", "IT")
        )
    )

    private val sampleNotifications = listOf(
        SmartNotification(
            notificationId = "fcm-1",
            title = "CRITICAL: Final Exam Schedule Released",
            message = "Spring 2026 Examination timetable is now live on the Digital Notice Board.",
            type = NotificationType.EXAM_ROUTINE,
            receiverRole = "All",
            noticeId = "n-101",
            isRead = false,
            createdAt = System.currentTimeMillis() - (2 * 3600 * 1000)
        ),
        SmartNotification(
            notificationId = "fcm-2",
            title = "Scholarship Application Open",
            message = "Merit Scholarship applications for 2026-27 are now open.",
            type = NotificationType.NEW_NOTICE,
            receiverRole = "Student",
            noticeId = "n-102",
            isRead = false,
            createdAt = System.currentTimeMillis() - (5 * 3600 * 1000)
        ),
        SmartNotification(
            notificationId = "fcm-3",
            title = "Library System Maintenance",
            message = "Central Library Wi-Fi and online portal offline on Sunday morning.",
            type = NotificationType.EMERGENCY_ALERT,
            receiverRole = "All",
            noticeId = "n-104",
            isRead = true,
            createdAt = System.currentTimeMillis() - (36 * 3600 * 1000)
        )
    )

    override fun getNotices(): Flow<Resource<List<Notice>>> = callbackFlow {
        val currentCached = dao.getAllCachedNotices().first()
        if (currentCached.isEmpty()) {
            dao.insertNotices(initialSampleNotices.map { NoticeBoardEntity.fromDomainModel(it) })
            dao.insertNotifications(sampleNotifications.map { NotificationFcmEntity.fromDomainModel(it) })
        }

        var isFirestoreConnected = false
        val listenerRegistration = try {
            firestore?.collection("notices")
                ?.whereEqualTo("isPublished", true)
                ?.orderBy("publishDate", Query.Direction.DESCENDING)
                ?.addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        return@addSnapshotListener
                    }
                    isFirestoreConnected = true
                    val remoteNotices = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Notice::class.java)
                    }
                }
        } catch (e: Exception) {
            null
        }

        dao.getAllCachedNotices().collect { entities ->
            val bookmarkedIds = dao.getBookmarkedNoticeIds().first().toSet()
            val readIds = dao.getReadNoticeIds().first().toSet()

            val domainNotices = entities.map { entity ->
                entity.toDomainModel(
                    isBookmarked = bookmarkedIds.contains(entity.noticeId),
                    isRead = readIds.contains(entity.noticeId)
                )
            }
            trySend(Resource.Success(domainNotices))
        }

        awaitClose {
            listenerRegistration?.remove()
        }
    }

    override fun getNoticeById(noticeId: String): Flow<Resource<Notice?>> = callbackFlow {
        dao.getNoticeById(noticeId).collect { entity ->
            if (entity != null) {
                val isBookmarked = dao.isBookmarked(noticeId)
                val readIds = dao.getReadNoticeIds().first().toSet()
                val isRead = readIds.contains(noticeId)
                trySend(Resource.Success(entity.toDomainModel(isBookmarked, isRead)))
            } else {
                trySend(Resource.Success(null))
            }
        }
        awaitClose { }
    }

    override suspend fun createNotice(notice: Notice): Resource<Unit> {
        return try {
            val entity = NoticeBoardEntity.fromDomainModel(notice)
            dao.insertNotice(entity)

            try {
                firestore?.collection("notices")?.document(notice.noticeId)?.set(notice)?.await()
            } catch (_: Exception) { }

            if (notice.isPublished) {
                val notification = SmartNotification(
                    notificationId = UUID.randomUUID().toString(),
                    title = "NEW NOTICE: ${notice.title}",
                    message = notice.summary.ifEmpty { notice.description.take(100) },
                    type = if (notice.priority == NoticePriority.CRITICAL) NotificationType.EMERGENCY_ALERT else NotificationType.NEW_NOTICE,
                    receiverRole = notice.targetRole,
                    noticeId = notice.noticeId,
                    createdAt = System.currentTimeMillis()
                )
                sendNotification(notification)
            }

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to create notice")
        }
    }

    override suspend fun updateNotice(notice: Notice): Resource<Unit> {
        return try {
            val entity = NoticeBoardEntity.fromDomainModel(notice)
            dao.insertNotice(entity)
            try {
                firestore?.collection("notices")?.document(notice.noticeId)?.set(notice)?.await()
            } catch (_: Exception) { }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to update notice")
        }
    }

    override suspend fun deleteNotice(noticeId: String): Resource<Unit> {
        return try {
            dao.deleteNotice(noticeId)
            try {
                firestore?.collection("notices")?.document(noticeId)?.delete()?.await()
            } catch (_: Exception) { }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to delete notice")
        }
    }

    override suspend fun togglePinNotice(noticeId: String, isPinned: Boolean): Resource<Unit> {
        return try {
            val existing = dao.getAllCachedNotices().first().find { it.noticeId == noticeId }
            if (existing != null) {
                val updated = existing.copy(isPinned = isPinned)
                dao.insertNotice(updated)
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Failed to pin notice")
        }
    }

    override suspend fun toggleBookmark(noticeId: String): Resource<Boolean> {
        return try {
            val isBookmarked = dao.isBookmarked(noticeId)
            if (isBookmarked) {
                dao.removeBookmark(noticeId)
                Resource.Success(false)
            } else {
                dao.addBookmark(NoticeBookmarkEntity(noticeId))
                Resource.Success(true)
            }
        } catch (e: Exception) {
            Resource.Error("Failed to bookmark notice")
        }
    }

    override suspend fun markNoticeAsRead(noticeId: String): Resource<Unit> {
        return try {
            dao.markNoticeAsRead(NoticeReadEntity(noticeId))
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Failed to mark read")
        }
    }

    override fun getNotifications(): Flow<List<SmartNotification>> {
        return dao.getAllNotifications().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun sendNotification(notification: SmartNotification): Resource<Unit> {
        return try {
            val entity = NotificationFcmEntity.fromDomainModel(notification)
            dao.insertNotification(entity)
            try {
                firestore?.collection("notifications")?.document(notification.notificationId)?.set(notification)?.await()
            } catch (_: Exception) { }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Failed to send notification")
        }
    }

    override suspend fun markNotificationRead(id: String): Resource<Unit> {
        dao.markNotificationAsRead(id)
        return Resource.Success(Unit)
    }

    override suspend fun markAllNotificationsRead(): Resource<Unit> {
        dao.markAllNotificationsAsRead()
        return Resource.Success(Unit)
    }

    override suspend fun addComment(noticeId: String, content: String, authorName: String): Resource<Unit> {
        val comment = NoticeComment(
            commentId = UUID.randomUUID().toString(),
            noticeId = noticeId,
            authorName = authorName,
            content = content,
            createdAt = System.currentTimeMillis()
        )
        return try {
            firestore?.collection("notices")?.document(noticeId)?.collection("comments")
                ?.document(comment.commentId)?.set(comment)?.await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Success(Unit)
        }
    }

    override fun getComments(noticeId: String): Flow<List<NoticeComment>> = callbackFlow {
        val listener = firestore?.collection("notices")?.document(noticeId)?.collection("comments")
            ?.orderBy("createdAt", Query.Direction.ASCENDING)
            ?.addSnapshotListener { snapshot, _ ->
                val comments = snapshot?.documents?.mapNotNull { it.toObject(NoticeComment::class.java) } ?: emptyList()
                trySend(comments)
            }
        awaitClose { listener?.remove() }
    }

    override suspend fun incrementViewCount(noticeId: String): Resource<Unit> {
        val entity = dao.getAllCachedNotices().first().find { it.noticeId == noticeId }
        if (entity != null) {
            dao.insertNotice(entity.copy(viewCount = entity.viewCount + 1))
        }
        return Resource.Success(Unit)
    }

    override suspend fun incrementDownloadCount(noticeId: String): Resource<Unit> {
        val entity = dao.getAllCachedNotices().first().find { it.noticeId == noticeId }
        if (entity != null) {
            dao.insertNotice(entity.copy(downloadCount = entity.downloadCount + 1))
        }
        return Resource.Success(Unit)
    }

    override suspend fun incrementShareCount(noticeId: String): Resource<Unit> {
        val entity = dao.getAllCachedNotices().first().find { it.noticeId == noticeId }
        if (entity != null) {
            dao.insertNotice(entity.copy(shareCount = entity.shareCount + 1))
        }
        return Resource.Success(Unit)
    }
}
