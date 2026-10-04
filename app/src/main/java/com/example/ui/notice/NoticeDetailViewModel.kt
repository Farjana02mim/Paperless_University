package com.example.ui.notice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.Resource
import com.example.domain.model.Notice
import com.example.domain.model.NoticeComment
import com.example.domain.repository.NoticeBoardRepository
import com.example.domain.usecase.notice.NoticeBoardUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NoticeDetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val notice: Notice? = null,
    val comments: List<NoticeComment> = emptyList(),
    val isCommentSubmitting: Boolean = false,
    val newCommentText: String = "",
    val downloadProgressMap: Map<String, Int> = emptyMap(),
    val actionMessage: String? = null
)

class NoticeDetailViewModel(
    private val useCases: NoticeBoardUseCases,
    private val repository: NoticeBoardRepository,
    private val noticeId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoticeDetailUiState())
    val uiState: StateFlow<NoticeDetailUiState> = _uiState.asStateFlow()

    init {
        loadNoticeDetail()
        loadComments()
        incrementViewCount()
    }

    fun loadNoticeDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            useCases.getNoticeDetail(noticeId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                notice = resource.data
                            )
                        }
                        if (resource.data != null) {
                            useCases.markNoticeRead(noticeId)
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = resource.message ?: "Notice not found"
                            )
                        }
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    private fun loadComments() {
        viewModelScope.launch {
            repository.getComments(noticeId).collect { list ->
                _uiState.update { it.copy(comments = list) }
            }
        }
    }

    private fun incrementViewCount() {
        viewModelScope.launch {
            repository.incrementViewCount(noticeId)
        }
    }

    fun toggleBookmark() {
        viewModelScope.launch {
            val res = useCases.toggleBookmark(noticeId)
            if (res is Resource.Success) {
                val isBookmarked = res.data ?: false
                _uiState.update { state ->
                    state.copy(
                        notice = state.notice?.copy(isBookmarked = isBookmarked),
                        actionMessage = if (isBookmarked) "Notice Bookmarked" else "Bookmark Removed"
                    )
                }
            }
        }
    }

    fun onCommentTextChanged(text: String) {
        _uiState.update { it.copy(newCommentText = text) }
    }

    fun submitComment(authorName: String = "Student User") {
        val text = _uiState.value.newCommentText.trim()
        if (text.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isCommentSubmitting = true) }
            repository.addComment(noticeId, text, authorName)
            _uiState.update { it.copy(isCommentSubmitting = false, newCommentText = "") }
        }
    }

    fun downloadAttachment(attachmentId: String) {
        viewModelScope.launch {
            repository.incrementDownloadCount(noticeId)
            _uiState.update {
                it.copy(
                    downloadProgressMap = it.downloadProgressMap + (attachmentId to 100),
                    actionMessage = "Attachment downloaded successfully"
                )
            }
        }
    }

    fun shareNotice() {
        viewModelScope.launch {
            repository.incrementShareCount(noticeId)
            _uiState.update { it.copy(actionMessage = "Notice link copied to clipboard") }
        }
    }

    fun clearActionMessage() {
        _uiState.update { it.copy(actionMessage = null) }
    }
}
