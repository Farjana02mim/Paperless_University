package com.example.domain.usecase.library

import com.example.domain.model.library.*
import com.example.domain.repository.library.LibraryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetLibraryHomeUseCase(private val repository: LibraryRepository) {
    operator fun invoke(): Flow<List<LibraryResourceModel>> {
        return repository.getAllResources()
    }
}

class SearchResourcesUseCase(private val repository: LibraryRepository) {
    operator fun invoke(
        query: String,
        departmentFilter: String? = null,
        semesterFilter: String? = null,
        typeFilter: ResourceType? = null,
        fileTypeFilter: FileType? = null
    ): Flow<List<LibraryResourceModel>> {
        return repository.getAllResources().map { list ->
            list.filter { item ->
                val matchesQuery = query.isBlank() ||
                        item.title.contains(query, ignoreCase = true) ||
                        item.author.contains(query, ignoreCase = true) ||
                        item.courseCode.contains(query, ignoreCase = true) ||
                        item.keywords.any { it.contains(query, ignoreCase = true) } ||
                        item.tags.any { it.contains(query, ignoreCase = true) }

                val matchesDept = departmentFilter.isNullOrBlank() || item.department.equals(departmentFilter, ignoreCase = true)
                val matchesSem = semesterFilter.isNullOrBlank() || item.semester.equals(semesterFilter, ignoreCase = true)
                val matchesType = typeFilter == null || item.resourceType == typeFilter
                val matchesFileType = fileTypeFilter == null || item.fileType == fileTypeFilter

                matchesQuery && matchesDept && matchesSem && matchesType && matchesFileType
            }
        }
    }
}

class DownloadResourceUseCase(private val repository: LibraryRepository) {
    suspend operator fun invoke(resourceId: String): Result<Unit> {
        return repository.startDownload(resourceId)
    }
}

class ManageCloudStorageUseCase(private val repository: LibraryRepository) {
    fun getFolders(parentId: String?, folderType: FolderType): Flow<List<CloudFolder>> {
        return repository.getCloudFolders(parentId, folderType)
    }

    fun getFiles(folderId: String): Flow<List<CloudFile>> {
        return repository.getCloudFiles(folderId)
    }

    suspend fun createFolder(folder: CloudFolder): Result<String> {
        return repository.createFolder(folder)
    }

    suspend fun uploadFile(file: CloudFile): Result<String> {
        return repository.uploadCloudFile(file)
    }

    suspend fun deleteFile(fileId: String): Result<Unit> {
        return repository.deleteCloudFile(fileId)
    }
}

class RecordReadingProgressUseCase(private val repository: LibraryRepository) {
    suspend operator fun invoke(history: ReadingHistoryItem): Result<Unit> {
        return repository.recordReadingProgress(history)
    }
}
