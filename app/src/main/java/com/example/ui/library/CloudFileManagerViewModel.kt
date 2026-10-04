package com.example.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.library.*
import com.example.domain.repository.library.LibraryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CloudFileManagerUiState(
    val currentFolderType: FolderType = FolderType.PRIVATE_STUDENT,
    val currentParentFolderId: String? = null,
    val folderBreadcrumbs: List<CloudFolder> = emptyList(),
    val folders: List<CloudFolder> = emptyList(),
    val files: List<CloudFile> = emptyList(),
    val storageUsage: StorageUsage = StorageUsage(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val userRole: String = "Student",
    val userMessage: String? = null
)

class CloudFileManagerViewModel(
    private val repository: LibraryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CloudFileManagerUiState())
    val uiState: StateFlow<CloudFileManagerUiState> = _uiState.asStateFlow()

    init {
        loadFoldersAndFiles()
        loadStorageUsage()
    }

    private fun loadFoldersAndFiles() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            _uiState.flatMapLatest { state ->
                repository.getCloudFolders(state.currentParentFolderId, state.currentFolderType)
            }.onEach { folderList ->
                _uiState.update { it.copy(folders = folderList, isLoading = false) }
            }.launchIn(viewModelScope)

            _uiState.flatMapLatest { state ->
                val folderId = state.currentParentFolderId ?: "folder_1"
                repository.getCloudFiles(folderId)
            }.onEach { fileList ->
                _uiState.update { it.copy(files = fileList) }
            }.launchIn(viewModelScope)
        }
    }

    private fun loadStorageUsage() {
        viewModelScope.launch {
            repository.getStorageUsage("user_001")
                .onEach { usage ->
                    _uiState.update { it.copy(storageUsage = usage) }
                }
                .launchIn(viewModelScope)
        }
    }

    fun selectFolderType(type: FolderType) {
        _uiState.update {
            it.copy(
                currentFolderType = type,
                currentParentFolderId = null,
                folderBreadcrumbs = emptyList()
            )
        }
    }

    fun openFolder(folder: CloudFolder) {
        _uiState.update {
            it.copy(
                currentParentFolderId = folder.folderId,
                folderBreadcrumbs = it.folderBreadcrumbs + folder
            )
        }
    }

    fun navigateBack() {
        val crumbs = _uiState.value.folderBreadcrumbs
        if (crumbs.isNotEmpty()) {
            val newCrumbs = crumbs.dropLast(1)
            val parentId = newCrumbs.lastOrNull()?.folderId
            _uiState.update {
                it.copy(
                    currentParentFolderId = parentId,
                    folderBreadcrumbs = newCrumbs
                )
            }
        }
    }

    fun createNewFolder(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val state = _uiState.value
            val folder = CloudFolder(
                parentFolderId = state.currentParentFolderId,
                name = name,
                folderType = state.currentFolderType,
                ownerId = "student_001",
                department = "Computer Science"
            )
            val res = repository.createFolder(folder)
            if (res.isSuccess) {
                _uiState.update { it.copy(userMessage = "Folder '$name' created") }
            }
        }
    }

    fun uploadFile(name: String, fileType: FileType) {
        viewModelScope.launch {
            val state = _uiState.value
            val folderId = state.currentParentFolderId ?: "folder_1"
            val file = CloudFile(
                folderId = folderId,
                name = name,
                fileType = fileType,
                fileSize = (1024 * 1024 * 3.5).toLong(),
                fileUrl = "https://www.w3.org/W3C/DesignIssues/PDF.pdf",
                ownerId = "student_001"
            )
            val res = repository.uploadCloudFile(file)
            if (res.isSuccess) {
                _uiState.update { it.copy(userMessage = "File '$name' uploaded successfully") }
            }
        }
    }

    fun deleteFile(fileId: String) {
        viewModelScope.launch {
            repository.deleteCloudFile(fileId)
            _uiState.update { it.copy(userMessage = "File moved to Trash") }
        }
    }

    fun deleteFolder(folderId: String) {
        viewModelScope.launch {
            repository.deleteCloudFolder(folderId)
            _uiState.update { it.copy(userMessage = "Folder deleted") }
        }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
}
