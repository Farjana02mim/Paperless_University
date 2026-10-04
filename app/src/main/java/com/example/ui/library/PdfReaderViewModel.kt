package com.example.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.library.BookmarkItem
import com.example.domain.model.library.LibraryResourceModel
import com.example.domain.model.library.ReadingHistoryItem
import com.example.domain.repository.library.LibraryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PdfReaderUiState(
    val resource: LibraryResourceModel? = null,
    val currentPage: Int = 1,
    val totalPages: Int = 120,
    val zoomLevel: Float = 1.0f,
    val isNightMode: Boolean = false,
    val isHorizontalMode: Boolean = false,
    val searchQuery: String = "",
    val bookmarks: List<BookmarkItem> = emptyList(),
    val isBookmarkDrawerOpen: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

class PdfReaderViewModel(
    private val repository: LibraryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PdfReaderUiState())
    val uiState: StateFlow<PdfReaderUiState> = _uiState.asStateFlow()

    fun loadResource(resourceId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val resResult = repository.getResourceById(resourceId)
            if (resResult.isSuccess) {
                val res = resResult.getOrNull()
                _uiState.update {
                    it.copy(
                        resource = res,
                        totalPages = res?.pageCount ?: 120,
                        isLoading = false
                    )
                }

                if (res != null) {
                    repository.getBookmarks(res.resourceId)
                        .onEach { bList ->
                            _uiState.update { state -> state.copy(bookmarks = bList) }
                        }
                        .launchIn(viewModelScope)
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Failed to load document") }
            }
        }
    }

    fun onPageChanged(page: Int) {
        val total = _uiState.value.totalPages
        val validPage = page.coerceIn(1, total)
        _uiState.update { it.copy(currentPage = validPage) }

        val res = _uiState.value.resource ?: return
        viewModelScope.launch {
            val progressPct = ((validPage.toFloat() / total.toFloat()) * 100).toInt()
            val history = ReadingHistoryItem(
                userId = "student_001",
                resourceId = res.resourceId,
                resourceTitle = res.title,
                lastPageRead = validPage,
                totalPages = total,
                progressPercentage = progressPct,
                totalTimeSpentSeconds = 60
            )
            repository.recordReadingProgress(history)
        }
    }

    fun zoomIn() {
        _uiState.update { it.copy(zoomLevel = (it.zoomLevel + 0.25f).coerceAtMost(3.0f)) }
    }

    fun zoomOut() {
        _uiState.update { it.copy(zoomLevel = (it.zoomLevel - 0.25f).coerceAtLeast(0.75f)) }
    }

    fun toggleNightMode() {
        _uiState.update { it.copy(isNightMode = !it.isNightMode) }
    }

    fun toggleScrollMode() {
        _uiState.update { it.copy(isHorizontalMode = !it.isHorizontalMode) }
    }

    fun addBookmark(note: String = "") {
        val res = _uiState.value.resource ?: return
        val currentPage = _uiState.value.currentPage
        viewModelScope.launch {
            val bookmark = BookmarkItem(
                userId = "student_001",
                resourceId = res.resourceId,
                pageNumber = currentPage,
                title = "Page $currentPage",
                note = note.ifBlank { "Bookmark on page $currentPage" }
            )
            repository.addBookmark(bookmark)
        }
    }

    fun deleteBookmark(bookmarkId: String) {
        viewModelScope.launch {
            repository.deleteBookmark(bookmarkId)
        }
    }

    fun toggleBookmarkDrawer(open: Boolean) {
        _uiState.update { it.copy(isBookmarkDrawerOpen = open) }
    }
}
