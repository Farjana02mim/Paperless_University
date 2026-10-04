package com.example.domain.model.library

import java.io.Serializable

enum class ResourceType {
    EBOOK,
    LECTURE_NOTES,
    LAB_MANUAL,
    ASSIGNMENT,
    QUESTION_PAPER,
    SOLUTION,
    RESEARCH_PAPER,
    JOURNAL,
    THESIS,
    COURSE_SLIDE,
    TUTORIAL_VIDEO,
    AUDIO_LECTURE,
    UNIVERSITY_FORM,
    ACADEMIC_CALENDAR,
    POLICY,
    NOTICE_ARCHIVE
}

enum class FileType {
    PDF, DOC, DOCX, PPT, PPTX, XLS, XLSX, TXT, ZIP, RAR, MP4, MP3, JPG, PNG
}

enum class VisibilityPermission {
    PUBLIC,
    STUDENT_ONLY,
    TEACHER_ONLY,
    DEPARTMENT_ONLY,
    PRIVATE
}

enum class DownloadState {
    NOT_DOWNLOADED,
    QUEUED,
    DOWNLOADING,
    PAUSED,
    COMPLETED,
    FAILED
}

data class LibraryResourceModel(
    val resourceId: String = "",
    val title: String = "",
    val description: String = "",
    val author: String = "",
    val uploadedBy: String = "",
    val uploadedByRole: String = "Teacher",
    val department: String = "Computer Science",
    val faculty: String = "Engineering & Technology",
    val semester: String = "Semester 4",
    val courseId: String = "CS-204",
    val courseCode: String = "CS204",
    val resourceType: ResourceType = ResourceType.EBOOK,
    val language: String = "English",
    val keywords: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val coverImage: String = "",
    val fileUrl: String = "",
    val previewUrl: String = "",
    val fileType: FileType = FileType.PDF,
    val fileSize: Long = 1024 * 1024 * 5, // in Bytes
    val pageCount: Int = 120,
    val durationSeconds: Long = 0,
    val downloadCount: Int = 42,
    val viewCount: Int = 185,
    val favoriteCount: Int = 18,
    val rating: Double = 4.7,
    val ratingCount: Int = 24,
    val allowDownload: Boolean = true,
    val allowOffline: Boolean = true,
    val visibility: VisibilityPermission = VisibilityPermission.PUBLIC,
    val status: String = "APPROVED", // PENDING, APPROVED, ARCHIVED
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val isBookmarked: Boolean = false,
    val downloadProgress: Int = 0,
    val downloadState: DownloadState = DownloadState.NOT_DOWNLOADED,
    val localFilePath: String = ""
) : Serializable

data class ResourceCategory(
    val categoryId: String = "",
    val name: String = "",
    val iconName: String = "Book",
    val resourceCount: Int = 0,
    val description: String = ""
)

data class DownloadItem(
    val downloadId: String = "",
    val resourceId: String = "",
    val title: String = "",
    val fileType: FileType = FileType.PDF,
    val fileSize: Long = 0,
    val downloadedBytes: Long = 0,
    val progress: Int = 0,
    val state: DownloadState = DownloadState.NOT_DOWNLOADED,
    val localPath: String = "",
    val speedKbps: Long = 0,
    val timestamp: Long = System.currentTimeMillis()
)

data class FavoriteItem(
    val favoriteId: String = "",
    val userId: String = "",
    val resourceId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class BookmarkItem(
    val bookmarkId: String = "",
    val userId: String = "",
    val resourceId: String = "",
    val pageNumber: Int = 1,
    val title: String = "",
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class ReadingHistoryItem(
    val historyId: String = "",
    val userId: String = "",
    val resourceId: String = "",
    val resourceTitle: String = "",
    val lastPageRead: Int = 1,
    val totalPages: Int = 100,
    val progressPercentage: Int = 1,
    val lastReadTimestamp: Long = System.currentTimeMillis(),
    val totalTimeSpentSeconds: Long = 0
)

data class ResourceRating(
    val ratingId: String = "",
    val resourceId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userAvatar: String = "",
    val stars: Double = 5.0,
    val comment: String = "",
    val teacherReply: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

enum class FolderType {
    PRIVATE_STUDENT,
    PRIVATE_TEACHER,
    SHARED_COURSE,
    DEPARTMENT,
    UNIVERSITY_PUBLIC,
    TRASH
}

data class CloudFolder(
    val folderId: String = "",
    val parentFolderId: String? = null,
    val name: String = "",
    val folderType: FolderType = FolderType.PRIVATE_STUDENT,
    val ownerId: String = "",
    val department: String = "",
    val courseCode: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)

data class CloudFile(
    val fileId: String = "",
    val folderId: String = "",
    val name: String = "",
    val fileType: FileType = FileType.PDF,
    val fileSize: Long = 0,
    val fileUrl: String = "",
    val ownerId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val downloadCount: Int = 0
)

data class StorageUsage(
    val totalQuotaBytes: Long = 10L * 1024 * 1024 * 1024, // 10 GB
    val usedBytes: Long = 2L * 1024 * 1024 * 1024, // 2 GB
    val documentsUsedBytes: Long = 1200L * 1024 * 1024,
    val mediaUsedBytes: Long = 600L * 1024 * 1024,
    val archivesUsedBytes: Long = 200L * 1024 * 1024
)

data class LibraryAnalytics(
    val totalReadingTimeMinutes: Long = 1420,
    val booksReadCount: Int = 28,
    val totalDownloadsCount: Int = 145,
    val totalFavoritesCount: Int = 12,
    val storageUsedPercentage: Float = 0.22f,
    val mostPopularResources: List<LibraryResourceModel> = emptyList(),
    val departmentResourceCounts: Map<String, Int> = emptyMap()
)
