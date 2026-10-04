package com.example.data.local.library

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.library.*

@Entity(tableName = "enterprise_library_resources")
data class EnterpriseLibraryResourceEntity(
    @PrimaryKey val resourceId: String,
    val title: String,
    val description: String,
    val author: String,
    val uploadedBy: String,
    val uploadedByRole: String,
    val department: String,
    val faculty: String,
    val semester: String,
    val courseId: String,
    val courseCode: String,
    val resourceType: String,
    val language: String,
    val keywords: String, // comma-separated
    val tags: String, // comma-separated
    val coverImage: String,
    val fileUrl: String,
    val previewUrl: String,
    val fileType: String,
    val fileSize: Long,
    val pageCount: Int,
    val durationSeconds: Long,
    val downloadCount: Int,
    val viewCount: Int,
    val favoriteCount: Int,
    val rating: Double,
    val ratingCount: Int,
    val allowDownload: Boolean,
    val allowOffline: Boolean,
    val visibility: String,
    val status: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isFavorite: Boolean = false,
    val isBookmarked: Boolean = false,
    val downloadProgress: Int = 0,
    val downloadState: String = "NOT_DOWNLOADED",
    val localFilePath: String = ""
) {
    fun toDomainModel(): LibraryResourceModel {
        return LibraryResourceModel(
            resourceId = resourceId,
            title = title,
            description = description,
            author = author,
            uploadedBy = uploadedBy,
            uploadedByRole = uploadedByRole,
            department = department,
            faculty = faculty,
            semester = semester,
            courseId = courseId,
            courseCode = courseCode,
            resourceType = try { ResourceType.valueOf(resourceType) } catch (_: Exception) { ResourceType.EBOOK },
            language = language,
            keywords = if (keywords.isBlank()) emptyList() else keywords.split(","),
            tags = if (tags.isBlank()) emptyList() else tags.split(","),
            coverImage = coverImage,
            fileUrl = fileUrl,
            previewUrl = previewUrl,
            fileType = try { FileType.valueOf(fileType) } catch (_: Exception) { FileType.PDF },
            fileSize = fileSize,
            pageCount = pageCount,
            durationSeconds = durationSeconds,
            downloadCount = downloadCount,
            viewCount = viewCount,
            favoriteCount = favoriteCount,
            rating = rating,
            ratingCount = ratingCount,
            allowDownload = allowDownload,
            allowOffline = allowOffline,
            visibility = try { VisibilityPermission.valueOf(visibility) } catch (_: Exception) { VisibilityPermission.PUBLIC },
            status = status,
            createdAt = createdAt,
            updatedAt = updatedAt,
            isFavorite = isFavorite,
            isBookmarked = isBookmarked,
            downloadProgress = downloadProgress,
            downloadState = try { DownloadState.valueOf(downloadState) } catch (_: Exception) { DownloadState.NOT_DOWNLOADED },
            localFilePath = localFilePath
        )
    }

    companion object {
        fun fromDomainModel(model: LibraryResourceModel): EnterpriseLibraryResourceEntity {
            return EnterpriseLibraryResourceEntity(
                resourceId = model.resourceId,
                title = model.title,
                description = model.description,
                author = model.author,
                uploadedBy = model.uploadedBy,
                uploadedByRole = model.uploadedByRole,
                department = model.department,
                faculty = model.faculty,
                semester = model.semester,
                courseId = model.courseId,
                courseCode = model.courseCode,
                resourceType = model.resourceType.name,
                language = model.language,
                keywords = model.keywords.joinToString(","),
                tags = model.tags.joinToString(","),
                coverImage = model.coverImage,
                fileUrl = model.fileUrl,
                previewUrl = model.previewUrl,
                fileType = model.fileType.name,
                fileSize = model.fileSize,
                pageCount = model.pageCount,
                durationSeconds = model.durationSeconds,
                downloadCount = model.downloadCount,
                viewCount = model.viewCount,
                favoriteCount = model.favoriteCount,
                rating = model.rating,
                ratingCount = model.ratingCount,
                allowDownload = model.allowDownload,
                allowOffline = model.allowOffline,
                visibility = model.visibility.name,
                status = model.status,
                createdAt = model.createdAt,
                updatedAt = model.updatedAt,
                isFavorite = model.isFavorite,
                isBookmarked = model.isBookmarked,
                downloadProgress = model.downloadProgress,
                downloadState = model.downloadState.name,
                localFilePath = model.localFilePath
            )
        }
    }
}

@Entity(tableName = "library_downloads")
data class DownloadEntity(
    @PrimaryKey val downloadId: String,
    val resourceId: String,
    val title: String,
    val fileType: String,
    val fileSize: Long,
    val downloadedBytes: Long,
    val progress: Int,
    val state: String,
    val localPath: String,
    val speedKbps: Long,
    val timestamp: Long
)

@Entity(tableName = "library_bookmarks")
data class BookmarkEntity(
    @PrimaryKey val bookmarkId: String,
    val userId: String,
    val resourceId: String,
    val pageNumber: Int,
    val title: String,
    val note: String,
    val timestamp: Long
)

@Entity(tableName = "library_reading_history")
data class ReadingHistoryEntity(
    @PrimaryKey val historyId: String,
    val userId: String,
    val resourceId: String,
    val resourceTitle: String,
    val lastPageRead: Int,
    val totalPages: Int,
    val progressPercentage: Int,
    val lastReadTimestamp: Long,
    val totalTimeSpentSeconds: Long
)

@Entity(tableName = "cloud_folders")
data class CloudFolderEntity(
    @PrimaryKey val folderId: String,
    val parentFolderId: String?,
    val name: String,
    val folderType: String,
    val ownerId: String,
    val department: String,
    val courseCode: String,
    val createdAt: Long,
    val isDeleted: Boolean
)

@Entity(tableName = "cloud_files")
data class CloudFileEntity(
    @PrimaryKey val fileId: String,
    val folderId: String,
    val name: String,
    val fileType: String,
    val fileSize: Long,
    val fileUrl: String,
    val ownerId: String,
    val createdAt: Long,
    val isDeleted: Boolean,
    val downloadCount: Int
)
