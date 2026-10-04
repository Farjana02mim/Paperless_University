package com.example.data.repository.library

import com.example.data.local.library.*
import com.example.domain.model.library.*
import com.example.domain.repository.library.LibraryRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class LibraryRepositoryImpl(
    private val dao: LibraryDao,
    private val firestore: FirebaseFirestore? = null
) : LibraryRepository {

    private val resourcesCollection get() = firestore?.collection("libraryResources")
    private val ratingsCollection get() = firestore?.collection("resourceRatings")
    private val cloudFoldersCollection get() = firestore?.collection("cloudFolders")
    private val cloudFilesCollection get() = firestore?.collection("cloudFiles")

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedSampleDataIfEmpty()
        }
    }

    private suspend fun seedSampleDataIfEmpty() {
        val count = dao.getAllResources().firstOrNull()?.size ?: 0
        if (count == 0) {
            val samples = listOf(
                EnterpriseLibraryResourceEntity(
                    resourceId = "res_101",
                    title = "Database Management Systems (6th Edition)",
                    description = "Comprehensive textbook covering relational algebra, SQL optimization, indexing, transaction management, and Distributed DBMS.",
                    author = "Ramez Elmasri & Shamkant B. Navathe",
                    uploadedBy = "Prof. Alan Turing",
                    uploadedByRole = "Librarian",
                    department = "Computer Science",
                    faculty = "School of Engineering",
                    semester = "Semester 4",
                    courseId = "CS-301",
                    courseCode = "CS301",
                    resourceType = ResourceType.EBOOK.name,
                    language = "English",
                    keywords = "database,sql,relational,indexing,transactions",
                    tags = "DBMS,Core,Reference",
                    coverImage = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400",
                    fileUrl = "https://www.w3.org/W3C/DesignIssues/PDF.pdf",
                    previewUrl = "https://www.w3.org/W3C/DesignIssues/PDF.pdf",
                    fileType = FileType.PDF.name,
                    fileSize = 14200000L,
                    pageCount = 890,
                    durationSeconds = 0,
                    downloadCount = 1240,
                    viewCount = 4320,
                    favoriteCount = 380,
                    rating = 4.9,
                    ratingCount = 84,
                    allowDownload = true,
                    allowOffline = true,
                    visibility = VisibilityPermission.PUBLIC.name,
                    status = "APPROVED",
                    createdAt = System.currentTimeMillis() - 86400000L * 30,
                    updatedAt = System.currentTimeMillis() - 86400000L * 10,
                    isFavorite = true,
                    isBookmarked = true,
                    downloadProgress = 100,
                    downloadState = DownloadState.COMPLETED.name,
                    localFilePath = "/storage/emulated/0/Download/dbms_elmasri.pdf"
                ),
                EnterpriseLibraryResourceEntity(
                    resourceId = "res_102",
                    title = "Algorithms & Data Structures Lecture Notes",
                    description = "Official semester lecture notes covering Dynamic Programming, Graph Traversal Algorithms, Red-Black Trees, and Greedy Strategies.",
                    author = "Dr. Margaret Hamilton",
                    uploadedBy = "Dr. Margaret Hamilton",
                    uploadedByRole = "Teacher",
                    department = "Computer Science",
                    faculty = "School of Engineering",
                    semester = "Semester 3",
                    courseId = "CS-202",
                    courseCode = "CS202",
                    resourceType = ResourceType.LECTURE_NOTES.name,
                    language = "English",
                    keywords = "algo,dp,graphs,trees,sorting",
                    tags = "Lecture Notes,Exams,Must Read",
                    coverImage = "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=400",
                    fileUrl = "https://www.w3.org/W3C/DesignIssues/PDF.pdf",
                    previewUrl = "",
                    fileType = FileType.PDF.name,
                    fileSize = 5800000L,
                    pageCount = 145,
                    durationSeconds = 0,
                    downloadCount = 980,
                    viewCount = 2890,
                    favoriteCount = 210,
                    rating = 4.8,
                    ratingCount = 52,
                    allowDownload = true,
                    allowOffline = true,
                    visibility = VisibilityPermission.STUDENT_ONLY.name,
                    status = "APPROVED",
                    createdAt = System.currentTimeMillis() - 86400000L * 15,
                    updatedAt = System.currentTimeMillis() - 86400000L * 2,
                    isFavorite = false,
                    isBookmarked = false,
                    downloadProgress = 0,
                    downloadState = DownloadState.NOT_DOWNLOADED.name,
                    localFilePath = ""
                ),
                EnterpriseLibraryResourceEntity(
                    resourceId = "res_103",
                    title = "Computer Networks Lab Manual - Socket Programming & Wireshark",
                    description = "Hands-on lab exercises for TCP/UDP socket programming in C/Python, packet analysis with Wireshark, and subnet routing configuration.",
                    author = "Dept. of Computer Science",
                    uploadedBy = "Prof. Robert Kahn",
                    uploadedByRole = "Teacher",
                    department = "Computer Science",
                    faculty = "School of Engineering",
                    semester = "Semester 5",
                    courseId = "CS-305",
                    courseCode = "CS305",
                    resourceType = ResourceType.LAB_MANUAL.name,
                    language = "English",
                    keywords = "networking,wireshark,tcp,udp,sockets",
                    tags = "Lab Manual,Practical",
                    coverImage = "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=400",
                    fileUrl = "https://www.w3.org/W3C/DesignIssues/PDF.pdf",
                    previewUrl = "",
                    fileType = FileType.PDF.name,
                    fileSize = 8200000L,
                    pageCount = 68,
                    durationSeconds = 0,
                    downloadCount = 620,
                    viewCount = 1540,
                    favoriteCount = 95,
                    rating = 4.6,
                    ratingCount = 29,
                    allowDownload = true,
                    allowOffline = true,
                    visibility = VisibilityPermission.PUBLIC.name,
                    status = "APPROVED",
                    createdAt = System.currentTimeMillis() - 86400000L * 45,
                    updatedAt = System.currentTimeMillis() - 86400000L * 5,
                    isFavorite = true,
                    isBookmarked = false,
                    downloadProgress = 0,
                    downloadState = DownloadState.NOT_DOWNLOADED.name,
                    localFilePath = ""
                ),
                EnterpriseLibraryResourceEntity(
                    resourceId = "res_104",
                    title = "Midterm & Final Question Papers Archive (2020-2025)",
                    description = "Solved previous year examination question papers with step-by-step marking schemes for Computer Architecture and Software Engineering.",
                    author = "University Examination Board",
                    uploadedBy = "Librarian Admin",
                    uploadedByRole = "Librarian",
                    department = "Computer Science",
                    faculty = "School of Engineering",
                    semester = "Semester 4",
                    courseId = "CS-204",
                    courseCode = "CS204",
                    resourceType = ResourceType.QUESTION_PAPER.name,
                    language = "English",
                    keywords = "question paper,exams,solutions,past papers",
                    tags = "Past Papers,Exam Prep",
                    coverImage = "https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?w=400",
                    fileUrl = "https://www.w3.org/W3C/DesignIssues/PDF.pdf",
                    previewUrl = "",
                    fileType = FileType.PDF.name,
                    fileSize = 18500000L,
                    pageCount = 210,
                    durationSeconds = 0,
                    downloadCount = 2450,
                    viewCount = 6800,
                    favoriteCount = 610,
                    rating = 5.0,
                    ratingCount = 112,
                    allowDownload = true,
                    allowOffline = true,
                    visibility = VisibilityPermission.PUBLIC.name,
                    status = "APPROVED",
                    createdAt = System.currentTimeMillis() - 86400000L * 60,
                    updatedAt = System.currentTimeMillis() - 86400000L * 1,
                    isFavorite = true,
                    isBookmarked = true,
                    downloadProgress = 100,
                    downloadState = DownloadState.COMPLETED.name,
                    localFilePath = "/storage/emulated/0/Download/past_papers_2020_2025.pdf"
                ),
                EnterpriseLibraryResourceEntity(
                    resourceId = "res_105",
                    title = "Artificial Intelligence & Neural Networks Video Course",
                    description = "High-definition video lecture series explaining Backpropagation, Convolutional Neural Networks, Transformers, and Reinforcement Learning.",
                    author = "Dr. Geoffrey Hinton",
                    uploadedBy = "Dr. Geoffrey Hinton",
                    uploadedByRole = "Teacher",
                    department = "Artificial Intelligence",
                    faculty = "School of Computing",
                    semester = "Semester 6",
                    courseId = "AI-401",
                    courseCode = "AI401",
                    resourceType = ResourceType.TUTORIAL_VIDEO.name,
                    language = "English",
                    keywords = "ai,machine learning,deep learning,neural networks,cnn,transformers",
                    tags = "Video,AI,Deep Learning",
                    coverImage = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=400",
                    fileUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    previewUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    fileType = FileType.MP4.name,
                    fileSize = 145000000L,
                    pageCount = 0,
                    durationSeconds = 3540,
                    downloadCount = 890,
                    viewCount = 3120,
                    favoriteCount = 290,
                    rating = 4.9,
                    ratingCount = 68,
                    allowDownload = true,
                    allowOffline = true,
                    visibility = VisibilityPermission.PUBLIC.name,
                    status = "APPROVED",
                    createdAt = System.currentTimeMillis() - 86400000L * 20,
                    updatedAt = System.currentTimeMillis() - 86400000L * 3,
                    isFavorite = false,
                    isBookmarked = false,
                    downloadProgress = 0,
                    downloadState = DownloadState.NOT_DOWNLOADED.name,
                    localFilePath = ""
                )
            )
            dao.insertResources(samples)

            // Seed sample cloud folders & files
            val folder1 = CloudFolderEntity("folder_1", null, "Private Notes", FolderType.PRIVATE_STUDENT.name, "student_001", "Computer Science", "CS204", System.currentTimeMillis(), false)
            val folder2 = CloudFolderEntity("folder_2", null, "CS301 Shared Course Materials", FolderType.SHARED_COURSE.name, "teacher_101", "Computer Science", "CS301", System.currentTimeMillis(), false)
            val folder3 = CloudFolderEntity("folder_3", null, "Department Syllabus & Forms", FolderType.DEPARTMENT.name, "admin_001", "Computer Science", "", System.currentTimeMillis(), false)
            dao.insertFolder(folder1)
            dao.insertFolder(folder2)
            dao.insertFolder(folder3)

            val file1 = CloudFileEntity("file_1", "folder_1", "Compiler_Design_Summary.pdf", FileType.PDF.name, 2400000L, "https://www.w3.org/W3C/DesignIssues/PDF.pdf", "student_001", System.currentTimeMillis(), false, 5)
            val file2 = CloudFileEntity("file_2", "folder_2", "CS301_Assignment_Solutions.docx", FileType.DOCX.name, 1800000L, "", "teacher_101", System.currentTimeMillis(), false, 42)
            dao.insertFile(file1)
            dao.insertFile(file2)
        }
    }

    override fun getAllResources(): Flow<List<LibraryResourceModel>> {
        return dao.getAllResources().map { list -> list.map { it.toDomainModel() } }
    }

    override fun getFavoriteResources(): Flow<List<LibraryResourceModel>> {
        return dao.getFavoriteResources().map { list -> list.map { it.toDomainModel() } }
    }

    override fun getOfflineResources(): Flow<List<LibraryResourceModel>> {
        return dao.getOfflineDownloadedResources().map { list -> list.map { it.toDomainModel() } }
    }

    override suspend fun getResourceById(id: String): Result<LibraryResourceModel> {
        return try {
            val entity = dao.getResourceById(id)
            if (entity != null) {
                Result.success(entity.toDomainModel())
            } else {
                Result.failure(Exception("Resource not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleFavorite(resourceId: String, currentStatus: Boolean): Result<Boolean> {
        return try {
            val newStatus = !currentStatus
            dao.updateFavoriteStatus(resourceId, newStatus)
            try {
                resourcesCollection?.document(resourceId)?.update("isFavorite", newStatus)?.await()
            } catch (_: Exception) {}
            Result.success(newStatus)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadResource(resource: LibraryResourceModel): Result<String> {
        return try {
            val id = if (resource.resourceId.isBlank()) "res_" + UUID.randomUUID().toString().take(8) else resource.resourceId
            val finalRes = resource.copy(resourceId = id, createdAt = System.currentTimeMillis())
            val entity = EnterpriseLibraryResourceEntity.fromDomainModel(finalRes)
            dao.insertResource(entity)
            try {
                resourcesCollection?.document(id)?.set(finalRes)?.await()
            } catch (_: Exception) {}
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateResource(resource: LibraryResourceModel): Result<Unit> {
        return try {
            val entity = EnterpriseLibraryResourceEntity.fromDomainModel(resource.copy(updatedAt = System.currentTimeMillis()))
            dao.insertResource(entity)
            try {
                resourcesCollection?.document(resource.resourceId)?.set(resource)?.await()
            } catch (_: Exception) {}
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteResource(resourceId: String): Result<Unit> {
        return try {
            val entity = dao.getResourceById(resourceId)
            if (entity != null) {
                dao.deleteResource(entity)
                try {
                    resourcesCollection?.document(resourceId)?.delete()?.await()
                } catch (_: Exception) {}
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getDownloads(): Flow<List<DownloadItem>> {
        return dao.getAllDownloads().map { list ->
            list.map { d ->
                DownloadItem(
                    downloadId = d.downloadId,
                    resourceId = d.resourceId,
                    title = d.title,
                    fileType = try { FileType.valueOf(d.fileType) } catch (_: Exception) { FileType.PDF },
                    fileSize = d.fileSize,
                    downloadedBytes = d.downloadedBytes,
                    progress = d.progress,
                    state = try { DownloadState.valueOf(d.state) } catch (_: Exception) { DownloadState.COMPLETED },
                    localPath = d.localPath,
                    speedKbps = d.speedKbps,
                    timestamp = d.timestamp
                )
            }
        }
    }

    override suspend fun startDownload(resourceId: String): Result<Unit> {
        return try {
            val res = dao.getResourceById(resourceId) ?: return Result.failure(Exception("Resource not found"))
            val downloadId = "dl_" + UUID.randomUUID().toString().take(8)
            val downloadEntity = DownloadEntity(
                downloadId = downloadId,
                resourceId = resourceId,
                title = res.title,
                fileType = res.fileType,
                fileSize = res.fileSize,
                downloadedBytes = res.fileSize,
                progress = 100,
                state = DownloadState.COMPLETED.name,
                localPath = "/storage/emulated/0/Download/${res.title.replace(" ", "_")}.${res.fileType.lowercase()}",
                speedKbps = 2400,
                timestamp = System.currentTimeMillis()
            )
            dao.insertDownload(downloadEntity)
            dao.updateDownloadState(resourceId, DownloadState.COMPLETED.name, 100, downloadEntity.localPath)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun pauseDownload(downloadId: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun resumeDownload(downloadId: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun cancelDownload(downloadId: String): Result<Unit> {
        return try {
            dao.deleteDownload(downloadId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getBookmarks(resourceId: String): Flow<List<BookmarkItem>> {
        return dao.getBookmarksForResource(resourceId).map { list ->
            list.map { b ->
                BookmarkItem(
                    bookmarkId = b.bookmarkId,
                    userId = b.userId,
                    resourceId = b.resourceId,
                    pageNumber = b.pageNumber,
                    title = b.title,
                    note = b.note,
                    timestamp = b.timestamp
                )
            }
        }
    }

    override suspend fun addBookmark(bookmark: BookmarkItem): Result<Unit> {
        return try {
            val id = if (bookmark.bookmarkId.isBlank()) UUID.randomUUID().toString() else bookmark.bookmarkId
            val entity = BookmarkEntity(
                bookmarkId = id,
                userId = bookmark.userId,
                resourceId = bookmark.resourceId,
                pageNumber = bookmark.pageNumber,
                title = bookmark.title,
                note = bookmark.note,
                timestamp = System.currentTimeMillis()
            )
            dao.insertBookmark(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteBookmark(bookmarkId: String): Result<Unit> {
        return try {
            dao.deleteBookmark(bookmarkId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getReadingHistory(): Flow<List<ReadingHistoryItem>> {
        return dao.getReadingHistory().map { list ->
            list.map { r ->
                ReadingHistoryItem(
                    historyId = r.historyId,
                    userId = r.userId,
                    resourceId = r.resourceId,
                    resourceTitle = r.resourceTitle,
                    lastPageRead = r.lastPageRead,
                    totalPages = r.totalPages,
                    progressPercentage = r.progressPercentage,
                    lastReadTimestamp = r.lastReadTimestamp,
                    totalTimeSpentSeconds = r.totalTimeSpentSeconds
                )
            }
        }
    }

    override suspend fun recordReadingProgress(history: ReadingHistoryItem): Result<Unit> {
        return try {
            val id = if (history.historyId.isBlank()) "hist_" + history.resourceId else history.historyId
            val entity = ReadingHistoryEntity(
                historyId = id,
                userId = history.userId,
                resourceId = history.resourceId,
                resourceTitle = history.resourceTitle,
                lastPageRead = history.lastPageRead,
                totalPages = history.totalPages,
                progressPercentage = history.progressPercentage,
                lastReadTimestamp = System.currentTimeMillis(),
                totalTimeSpentSeconds = history.totalTimeSpentSeconds
            )
            dao.insertReadingHistory(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getRatings(resourceId: String): Flow<List<ResourceRating>> = callbackFlow {
        val listener = ratingsCollection?.whereEqualTo("resourceId", resourceId)?.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { it.toObject(ResourceRating::class.java) } ?: emptyList()
            trySend(list)
        }
        awaitClose { listener?.remove() }
    }

    override suspend fun addRating(rating: ResourceRating): Result<Unit> {
        return try {
            val id = if (rating.ratingId.isBlank()) UUID.randomUUID().toString() else rating.ratingId
            val finalRating = rating.copy(ratingId = id, timestamp = System.currentTimeMillis())
            ratingsCollection?.document(id)?.set(finalRating)?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addTeacherReply(ratingId: String, reply: String): Result<Unit> {
        return try {
            ratingsCollection?.document(ratingId)?.update("teacherReply", reply)?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCloudFolders(parentId: String?, folderType: FolderType): Flow<List<CloudFolder>> {
        return dao.getFoldersByParent(parentId).map { list ->
            list.map { f ->
                CloudFolder(
                    folderId = f.folderId,
                    parentFolderId = f.parentFolderId,
                    name = f.name,
                    folderType = try { FolderType.valueOf(f.folderType) } catch (_: Exception) { FolderType.PRIVATE_STUDENT },
                    ownerId = f.ownerId,
                    department = f.department,
                    courseCode = f.courseCode,
                    createdAt = f.createdAt,
                    isDeleted = f.isDeleted
                )
            }
        }
    }

    override fun getCloudFiles(folderId: String): Flow<List<CloudFile>> {
        return dao.getFilesByFolder(folderId).map { list ->
            list.map { f ->
                CloudFile(
                    fileId = f.fileId,
                    folderId = f.folderId,
                    name = f.name,
                    fileType = try { FileType.valueOf(f.fileType) } catch (_: Exception) { FileType.PDF },
                    fileSize = f.fileSize,
                    fileUrl = f.fileUrl,
                    ownerId = f.ownerId,
                    createdAt = f.createdAt,
                    isDeleted = f.isDeleted,
                    downloadCount = f.downloadCount
                )
            }
        }
    }

    override suspend fun createFolder(folder: CloudFolder): Result<String> {
        return try {
            val id = if (folder.folderId.isBlank()) "folder_" + UUID.randomUUID().toString().take(8) else folder.folderId
            val finalFolder = folder.copy(folderId = id)
            val entity = CloudFolderEntity(
                folderId = finalFolder.folderId,
                parentFolderId = finalFolder.parentFolderId,
                name = finalFolder.name,
                folderType = finalFolder.folderType.name,
                ownerId = finalFolder.ownerId,
                department = finalFolder.department,
                courseCode = finalFolder.courseCode,
                createdAt = finalFolder.createdAt,
                isDeleted = finalFolder.isDeleted
            )
            dao.insertFolder(entity)
            try {
                cloudFoldersCollection?.document(id)?.set(finalFolder)?.await()
            } catch (_: Exception) {}
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadCloudFile(file: CloudFile): Result<String> {
        return try {
            val id = if (file.fileId.isBlank()) "file_" + UUID.randomUUID().toString().take(8) else file.fileId
            val finalFile = file.copy(fileId = id)
            val entity = CloudFileEntity(
                fileId = finalFile.fileId,
                folderId = finalFile.folderId,
                name = finalFile.name,
                fileType = finalFile.fileType.name,
                fileSize = finalFile.fileSize,
                fileUrl = finalFile.fileUrl,
                ownerId = finalFile.ownerId,
                createdAt = finalFile.createdAt,
                isDeleted = finalFile.isDeleted,
                downloadCount = finalFile.downloadCount
            )
            dao.insertFile(entity)
            try {
                cloudFilesCollection?.document(id)?.set(finalFile)?.await()
            } catch (_: Exception) {}
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCloudFile(fileId: String): Result<Unit> {
        return try {
            dao.softDeleteFile(fileId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCloudFolder(folderId: String): Result<Unit> {
        return try {
            dao.softDeleteFolder(folderId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getStorageUsage(userId: String): Flow<StorageUsage> {
        return flowOf(
            StorageUsage(
                totalQuotaBytes = 15L * 1024 * 1024 * 1024, // 15 GB
                usedBytes = 3L * 1024 * 1024 * 1024, // 3.2 GB
                documentsUsedBytes = 1800L * 1024 * 1024,
                mediaUsedBytes = 1100L * 1024 * 1024,
                archivesUsedBytes = 300L * 1024 * 1024
            )
        )
    }

    override suspend fun getLibraryAnalytics(): Result<LibraryAnalytics> {
        return try {
            val resources = dao.getAllResources().firstOrNull()?.map { it.toDomainModel() } ?: emptyList()
            Result.success(
                LibraryAnalytics(
                    totalReadingTimeMinutes = 1840,
                    booksReadCount = 34,
                    totalDownloadsCount = 3420,
                    totalFavoritesCount = 1280,
                    storageUsedPercentage = 0.28f,
                    mostPopularResources = resources.sortedByDescending { it.downloadCount }.take(5),
                    departmentResourceCounts = mapOf(
                        "Computer Science" to 42,
                        "Electrical Engineering" to 28,
                        "Business Administration" to 35,
                        "Mathematics & Physics" to 19
                    )
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
