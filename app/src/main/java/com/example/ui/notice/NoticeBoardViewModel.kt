package com.example.ui.notice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.Resource
import com.example.domain.model.Notice
import com.example.domain.model.NoticeCategory
import com.example.domain.model.NoticePriority
import com.example.domain.usecase.notice.NoticeBoardUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class NoticeSortOption {
    NEWEST_FIRST,
    OLDEST_FIRST,
    MOST_VIEWED,
    PINNED_FIRST
}

data class NoticeBoardUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val notices: List<Notice> = emptyList(),
    val filteredNotices: List<Notice> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: NoticeCategory = NoticeCategory.ALL,
    val selectedPriority: NoticePriority? = null,
    val selectedDepartment: String = "All Departments",
    val sortOption: NoticeSortOption = NoticeSortOption.PINNED_FIRST,
    val isOnlyBookmarked: Boolean = false,
    val isOnlyUnread: Boolean = false
)

class NoticeBoardViewModel(
    private val useCases: NoticeBoardUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoticeBoardUiState())
    val uiState: StateFlow<NoticeBoardUiState> = _uiState.asStateFlow()

    init {
        loadNotices()
    }

    fun loadNotices() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            useCases.getNotices().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val list = resource.data ?: emptyList()
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                isRefreshing = false,
                                notices = list,
                                filteredNotices = filterAndSortNotices(list, state)
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                error = resource.message ?: "Failed to load notices"
                            )
                        }
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadNotices()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            val newState = state.copy(searchQuery = query)
            newState.copy(filteredNotices = filterAndSortNotices(state.notices, newState))
        }
    }

    fun selectCategory(category: NoticeCategory) {
        _uiState.update { state ->
            val newState = state.copy(selectedCategory = category)
            newState.copy(filteredNotices = filterAndSortNotices(state.notices, newState))
        }
    }

    fun selectPriority(priority: NoticePriority?) {
        _uiState.update { state ->
            val newState = state.copy(selectedPriority = priority)
            newState.copy(filteredNotices = filterAndSortNotices(state.notices, newState))
        }
    }

    fun selectDepartment(dept: String) {
        _uiState.update { state ->
            val newState = state.copy(selectedDepartment = dept)
            newState.copy(filteredNotices = filterAndSortNotices(state.notices, newState))
        }
    }

    fun selectSortOption(sort: NoticeSortOption) {
        _uiState.update { state ->
            val newState = state.copy(sortOption = sort)
            newState.copy(filteredNotices = filterAndSortNotices(state.notices, newState))
        }
    }

    fun toggleOnlyBookmarked(only: Boolean) {
        _uiState.update { state ->
            val newState = state.copy(isOnlyBookmarked = only)
            newState.copy(filteredNotices = filterAndSortNotices(state.notices, newState))
        }
    }

    fun toggleOnlyUnread(only: Boolean) {
        _uiState.update { state ->
            val newState = state.copy(isOnlyUnread = only)
            newState.copy(filteredNotices = filterAndSortNotices(state.notices, newState))
        }
    }

    fun toggleBookmark(noticeId: String) {
        viewModelScope.launch {
            useCases.toggleBookmark(noticeId)
        }
    }

    fun markAsRead(noticeId: String) {
        viewModelScope.launch {
            useCases.markNoticeRead(noticeId)
        }
    }

    private fun filterAndSortNotices(
        all: List<Notice>,
        state: NoticeBoardUiState
    ): List<Notice> {
        var filtered = all

        if (state.searchQuery.isNotBlank()) {
            val q = state.searchQuery.trim().lowercase()
            filtered = filtered.filter {
                it.title.lowercase().contains(q) ||
                        it.summary.lowercase().contains(q) ||
                        it.description.lowercase().contains(q) ||
                        it.tags.any { tag -> tag.lowercase().contains(q) } ||
                        it.department.lowercase().contains(q)
            }
        }

        if (state.selectedCategory != NoticeCategory.ALL) {
            filtered = filtered.filter { it.category == state.selectedCategory }
        }

        if (state.selectedPriority != null) {
            filtered = filtered.filter { it.priority == state.selectedPriority }
        }

        if (state.selectedDepartment != "All Departments") {
            filtered = filtered.filter { it.department.equals(state.selectedDepartment, ignoreCase = true) || it.department.equals("All Departments", ignoreCase = true) }
        }

        if (state.isOnlyBookmarked) {
            filtered = filtered.filter { it.isBookmarked }
        }

        if (state.isOnlyUnread) {
            filtered = filtered.filter { !it.isRead }
        }

        return when (state.sortOption) {
            NoticeSortOption.NEWEST_FIRST -> filtered.sortedByDescending { it.publishDate }
            NoticeSortOption.OLDEST_FIRST -> filtered.sortedBy { it.publishDate }
            NoticeSortOption.MOST_VIEWED -> filtered.sortedByDescending { it.viewCount }
            NoticeSortOption.PINNED_FIRST -> filtered.sortedWith(
                compareByDescending<Notice> { it.isPinned }
                    .thenByDescending { it.priority.weight }
                    .thenByDescending { it.publishDate }
            )
        }
    }
}
