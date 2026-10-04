package com.example.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.library.*
import com.example.domain.repository.library.LibraryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DigitalLibraryUiState(
    val resources: List<LibraryResourceModel> = emptyList(),
    val featuredResources: List<LibraryResourceModel> = emptyList(),
    val popularResources: List<LibraryResourceModel> = emptyList(),
    val continueReading: List<ReadingHistoryItem> = emptyList(),
    val favorites: List<LibraryResourceModel> = emptyList(),
    val offlineDownloads: List<LibraryResourceModel> = emptyList(),
    val downloadsQueue: List<DownloadItem> = emptyList(),
    val searchQuery: String = "",
    val selectedDepartment: String? = null,
    val selectedSemester: String? = null,
    val selectedResourceType: ResourceType? = null,
    val selectedFileType: FileType? = null,
    val selectedTab: Int = 0, // 0: Home, 1: Search/Browse, 2: Favorites, 3: Downloads, 4: History
    val isLoading: Boolean = false,
    val userRole: String = "Student", // Student, Teacher, Administrator, Librarian
    val userMessage: String? = null
)

class DigitalLibraryViewModel(
    private val repository: LibraryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DigitalLibraryUiState())
    val uiState: StateFlow<DigitalLibraryUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.getAllResources()
                .onEach { list ->
                    val featured = list.filter { it.rating >= 4.7 }.take(5)
                    val popular = list.sortedByDescending { it.downloadCount }.take(5)
                    _uiState.update { state ->
                        state.copy(
                            resources = list,
                            featuredResources = featured,
                            popularResources = popular,
                            isLoading = false
                        )
                    }
                }
                .launchIn(viewModelScope)

            repository.getFavoriteResources()
                .onEach { list ->
                    _uiState.update { it.copy(favorites = list) }
                }
                .launchIn(viewModelScope)

            repository.getOfflineResources()
                .onEach { list ->
                    _uiState.update { it.copy(offlineDownloads = list) }
                }
                .launchIn(viewModelScope)

            repository.getDownloads()
                .onEach { list ->
                    _uiState.update { it.copy(downloadsQueue = list) }
                }
                .launchIn(viewModelScope)

            repository.getReadingHistory()
                .onEach { history ->
                    _uiState.update { it.copy(continueReading = history) }
                }
                .launchIn(viewModelScope)
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onDepartmentFilterSelect(dept: String?) {
        _uiState.update { it.copy(selectedDepartment = if (it.selectedDepartment == dept) null else dept) }
    }

    fun onSemesterFilterSelect(sem: String?) {
        _uiState.update { it.copy(selectedSemester = if (it.selectedSemester == sem) null else sem) }
    }

    fun onResourceTypeSelect(type: ResourceType?) {
        _uiState.update { it.copy(selectedResourceType = if (it.selectedResourceType == type) null else type) }
    }

    fun onFileTypeSelect(fileType: FileType?) {
        _uiState.update { it.copy(selectedFileType = if (it.selectedFileType == fileType) null else fileType) }
    }

    fun setSelectedTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun toggleFavorite(resourceId: String, currentFav: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(resourceId, currentFav)
        }
    }

    fun startDownload(resourceId: String) {
        viewModelScope.launch {
            val result = repository.startDownload(resourceId)
            if (result.isSuccess) {
                _uiState.update { it.copy(userMessage = "Download started and saved to offline library") }
            } else {
                _uiState.update { it.copy(userMessage = "Failed to download resource") }
            }
        }
    }

    fun cancelDownload(downloadId: String) {
        viewModelScope.launch {
            repository.cancelDownload(downloadId)
        }
    }

    fun setUserRole(role: String) {
        _uiState.update { it.copy(userRole = role) }
    }

    fun uploadResource(resource: LibraryResourceModel) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.uploadResource(resource)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, userMessage = "Resource uploaded successfully!") }
            } else {
                _uiState.update { it.copy(isLoading = false, userMessage = "Error uploading resource") }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
}
