package com.example.domain.model

import java.util.UUID

enum class NoticeCategory(val displayName: String) {
    ALL("All"),
    ACADEMIC("Academic"),
    EXAM("Exam"),
    ADMISSION("Admission"),
    SCHOLARSHIP("Scholarship"),
    EVENTS("Events"),
    ROUTINE("Routine"),
    DEPARTMENT("Department"),
    FINANCE("Finance"),
    LIBRARY("Library"),
    INTERNSHIP("Internship"),
    PLACEMENT("Placement"),
    RESEARCH("Research"),
    HOLIDAY("Holiday"),
    EMERGENCY("Emergency"),
    SPORTS("Sports"),
    CLUB("Club"),
    HOSTEL("Hostel"),
    TRANSPORT("Transport"),
    GENERAL("General")
}

enum class NoticePriority(val weight: Int) {
    CRITICAL(4),
    HIGH(3),
    NORMAL(2),
    LOW(1)
}

data class NoticeAttachment(
    val id: String = UUID.randomUUID().toString(),
    val fileName: String = "",
    val fileUrl: String = "",
    val fileType: String = "PDF", // PDF, DOCX, PPTX, XLSX, ZIP, IMAGE, VIDEO
    val fileSizeFormatted: String = "1.2 MB"
)

data class NoticeComment(
    val commentId: String = UUID.randomUUID().toString(),
    val noticeId: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorPhoto: String = "",
    val content: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class Notice(
    val noticeId: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val summary: String = "",
    val category: NoticeCategory = NoticeCategory.ACADEMIC,
    val priority: NoticePriority = NoticePriority.NORMAL,
    val department: String = "All Departments",
    val targetRole: String = "All", // "All", "Student", "Teacher", "Administrator"
    val authorId: String = "",
    val authorName: String = "University Administration",
    val authorPhoto: String = "",
    val attachments: List<NoticeAttachment> = emptyList(),
    val coverImage: String = "",
    val publishDate: Long = System.currentTimeMillis(),
    val expiryDate: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val isPublished: Boolean = true,
    val viewCount: Int = 0,
    val downloadCount: Int = 0,
    val bookmarkCount: Int = 0,
    val shareCount: Int = 0,
    val allowComments: Boolean = true,
    val tags: List<String> = emptyList(),
    val isBookmarked: Boolean = false,
    val isRead: Boolean = false
)

enum class NotificationType {
    NEW_NOTICE,
    UPDATED_NOTICE,
    EMERGENCY_ALERT,
    EXAM_ROUTINE,
    RESULT_PUBLISHED,
    ASSIGNMENT_DEADLINE,
    FEE_REMINDER,
    LIBRARY_REMINDER
}

data class SmartNotification(
    val notificationId: String = UUID.randomUUID().toString(),
    val title: String = "",
    val message: String = "",
    val type: NotificationType = NotificationType.NEW_NOTICE,
    val receiverRole: String = "All",
    val receiverUid: String = "",
    val noticeId: String = "",
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val actionType: String = "VIEW_NOTICE",
    val imageUrl: String = "",
    val deepLink: String = ""
)
