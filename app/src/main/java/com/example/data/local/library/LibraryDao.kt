package com.example.data.local.library

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {
    @Query("SELECT * FROM enterprise_library_resources ORDER BY createdAt DESC")
    fun getAllResources(): Flow<List<EnterpriseLibraryResourceEntity>>

    @Query("SELECT * FROM enterprise_library_resources WHERE resourceId = :id")
    suspend fun getResourceById(id: String): EnterpriseLibraryResourceEntity?

    @Query("SELECT * FROM enterprise_library_resources WHERE isFavorite = 1 ORDER BY updatedAt DESC")
    fun getFavoriteResources(): Flow<List<EnterpriseLibraryResourceEntity>>

    @Query("SELECT * FROM enterprise_library_resources WHERE downloadState = 'COMPLETED'")
    fun getOfflineDownloadedResources(): Flow<List<EnterpriseLibraryResourceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResource(resource: EnterpriseLibraryResourceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResources(resources: List<EnterpriseLibraryResourceEntity>)

    @Query("UPDATE enterprise_library_resources SET isFavorite = :isFav WHERE resourceId = :id")
    suspend fun updateFavoriteStatus(id: String, isFav: Boolean)

    @Query("UPDATE enterprise_library_resources SET downloadState = :state, downloadProgress = :progress, localFilePath = :path WHERE resourceId = :id")
    suspend fun updateDownloadState(id: String, state: String, progress: Int, path: String)

    @Delete
    suspend fun deleteResource(resource: EnterpriseLibraryResourceEntity)

    // Downloads
    @Query("SELECT * FROM library_downloads ORDER BY timestamp DESC")
    fun getAllDownloads(): Flow<List<DownloadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: DownloadEntity)

    @Query("DELETE FROM library_downloads WHERE downloadId = :id")
    suspend fun deleteDownload(id: String)

    // Bookmarks
    @Query("SELECT * FROM library_bookmarks WHERE resourceId = :resourceId ORDER BY pageNumber ASC")
    fun getBookmarksForResource(resourceId: String): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM library_bookmarks WHERE bookmarkId = :id")
    suspend fun deleteBookmark(id: String)

    // Reading History
    @Query("SELECT * FROM library_reading_history ORDER BY lastReadTimestamp DESC")
    fun getReadingHistory(): Flow<List<ReadingHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReadingHistory(history: ReadingHistoryEntity)

    // Cloud Folders & Files
    @Query("SELECT * FROM cloud_folders WHERE parentFolderId IS :parentId AND isDeleted = 0")
    fun getFoldersByParent(parentId: String?): Flow<List<CloudFolderEntity>>

    @Query("SELECT * FROM cloud_folders WHERE folderType = :folderType AND isDeleted = 0")
    fun getFoldersByType(folderType: String): Flow<List<CloudFolderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: CloudFolderEntity)

    @Query("SELECT * FROM cloud_files WHERE folderId = :folderId AND isDeleted = 0")
    fun getFilesByFolder(folderId: String): Flow<List<CloudFileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: CloudFileEntity)

    @Query("UPDATE cloud_files SET isDeleted = 1 WHERE fileId = :fileId")
    suspend fun softDeleteFile(fileId: String)

    @Query("UPDATE cloud_folders SET isDeleted = 1 WHERE folderId = :folderId")
    suspend fun softDeleteFolder(folderId: String)
}
