package com.example.domain.repository.library

import com.example.domain.model.library.*
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {
    fun getAllResources(): Flow<List<LibraryResourceModel>>
    fun getFavoriteResources(): Flow<List<LibraryResourceModel>>
    fun getOfflineResources(): Flow<List<LibraryResourceModel>>
    suspend fun getResourceById(id: String): Result<LibraryResourceModel>
    suspend fun toggleFavorite(resourceId: String, currentStatus: Boolean): Result<Boolean>
    suspend fun uploadResource(resource: LibraryResourceModel): Result<String>
    suspend fun updateResource(resource: LibraryResourceModel): Result<Unit>
    suspend fun deleteResource(resourceId: String): Result<Unit>
    
    // Downloads
    fun getDownloads(): Flow<List<DownloadItem>>
    suspend fun startDownload(resourceId: String): Result<Unit>
    suspend fun pauseDownload(downloadId: String): Result<Unit>
    suspend fun resumeDownload(downloadId: String): Result<Unit>
    suspend fun cancelDownload(downloadId: String): Result<Unit>
    
    // Bookmarks & History
    fun getBookmarks(resourceId: String): Flow<List<BookmarkItem>>
    suspend fun addBookmark(bookmark: BookmarkItem): Result<Unit>
    suspend fun deleteBookmark(bookmarkId: String): Result<Unit>
    
    fun getReadingHistory(): Flow<List<ReadingHistoryItem>>
    suspend fun recordReadingProgress(history: ReadingHistoryItem): Result<Unit>
    
    // Ratings & Reviews
    fun getRatings(resourceId: String): Flow<List<ResourceRating>>
    suspend fun addRating(rating: ResourceRating): Result<Unit>
    suspend fun addTeacherReply(ratingId: String, reply: String): Result<Unit>
    
    // Cloud Storage
    fun getCloudFolders(parentId: String?, folderType: FolderType): Flow<List<CloudFolder>>
    fun getCloudFiles(folderId: String): Flow<List<CloudFile>>
    suspend fun createFolder(folder: CloudFolder): Result<String>
    suspend fun uploadCloudFile(file: CloudFile): Result<String>
    suspend fun deleteCloudFile(fileId: String): Result<Unit>
    suspend fun deleteCloudFolder(folderId: String): Result<Unit>
    fun getStorageUsage(userId: String): Flow<StorageUsage>

    // Analytics
    suspend fun getLibraryAnalytics(): Result<LibraryAnalytics>
}
