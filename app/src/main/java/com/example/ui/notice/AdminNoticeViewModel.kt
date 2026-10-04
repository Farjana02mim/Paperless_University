package com.example.ui.notice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.Resource
import com.example.domain.model.Notice
import com.example.domain.model.NoticeAttachment
import com.example.domain.model.NoticeCategory
import com.example.domain.model.NoticePriority
import com.example.domain.model.NotificationType
import com.example.domain.model.SmartNotification
import com.example.domain.usecase.notice.NoticeBoardUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class AdminNoticeFormState(
    val title: String = "",
    val summary: String = "",
    val description: String = "",
    val category: NoticeCategory = NoticeCategory.ACADEMIC,
    val priority: NoticePriority = NoticePriority.NORMAL,
    val department: String = "All Departments",
    val targetRole: String = "All",
    val isPinned: Boolean = false,
    val isPublished: Boolean = true,
    val allowComments: Boolean = true,
    val tagsInput: String = "",
    val attachments: List<NoticeAttachment> = emptyList(),
    val coverImage: String = "",
    val isSubmitting: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class AdminNoticeViewModel(
    private val useCases: NoticeBoardUseCases
) : ViewModel() {

    private val _formState = MutableStateFlow(AdminNoticeFormState())
    val formState: StateFlow<AdminNoticeFormState> = _formState.asStateFlow()

    fun onTitleChanged(v: String) = _formState.update { it.copy(title = v) }
    fun onSummaryChanged(v: String) = _formState.update { it.copy(summary = v) }
    fun onDescriptionChanged(v: String) = _formState.update { it.copy(description = v) }
    fun onCategorySelected(v: NoticeCategory) = _formState.update { it.copy(category = v) }
    fun onPrioritySelected(v: NoticePriority) = _formState.update { it.copy(priority = v) }
    fun onDepartmentSelected(v: String) = _formState.update { it.copy(department = v) }
    fun onTargetRoleSelected(v: String) = _formState.update { it.copy(targetRole = v) }
    fun onPinnedToggled(v: Boolean) = _formState.update { it.copy(isPinned = v) }
    fun onPublishedToggled(v: Boolean) = _formState.update { it.copy(isPublished = v) }
    fun onAllowCommentsToggled(v: Boolean) = _formState.update { it.copy(allowComments = v) }
    fun onTagsInputChanged(v: String) = _formState.update { it.copy(tagsInput = v) }

    fun addAttachment(fileName: String, fileType: String = "PDF") {
        val newAtt = NoticeAttachment(
            id = UUID.randomUUID().toString(),
            fileName = fileName,
            fileUrl = "https://example.com/files/$fileName",
            fileType = fileType,
            fileSizeFormatted = "1.5 MB"
        )
        _formState.update { it.copy(attachments = it.attachments + newAtt) }
    }

    fun removeAttachment(attachmentId: String) {
        _formState.update { it.copy(attachments = it.attachments.filter { a -> a.id != attachmentId }) }
    }

    fun submitNotice(authorName: String = "University Administration") {
        val s = _formState.value
        if (s.title.isBlank()) {
            _formState.update { it.copy(errorMessage = "Title cannot be empty") }
            return
        }
        if (s.description.isBlank()) {
            _formState.update { it.copy(errorMessage = "Description cannot be empty") }
            return
        }

        viewModelScope.launch {
            _formState.update { it.copy(isSubmitting = true, errorMessage = null) }

            val notice = Notice(
                noticeId = UUID.randomUUID().toString(),
                title = s.title.trim(),
                summary = s.summary.ifEmpty { s.description.take(120) },
                description = s.description.trim(),
                category = s.category,
                priority = s.priority,
                department = s.department,
                targetRole = s.targetRole,
                authorName = authorName,
                attachments = s.attachments,
                coverImage = s.coverImage,
                publishDate = System.currentTimeMillis(),
                isPinned = s.isPinned,
                isPublished = s.isPublished,
                allowComments = s.allowComments,
                tags = if (s.tagsInput.isBlank()) emptyList() else s.tagsInput.split(",").map { it.trim() }
            )

            when (val result = useCases.createNotice(notice)) {
                is Resource.Success -> {
                    _formState.update {
                        AdminNoticeFormState(
                            successMessage = "Notice Published Successfully and Broadcasted to Users!"
                        )
                    }
                }
                is Resource.Error -> {
                    _formState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = result.message ?: "Failed to publish notice"
                        )
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun sendEmergencyBroadcast(title: String, alertMessage: String) {
        viewModelScope.launch {
            val notification = SmartNotification(
                notificationId = UUID.randomUUID().toString(),
                title = "🚨 EMERGENCY: $title",
                message = alertMessage,
                type = NotificationType.EMERGENCY_ALERT,
                receiverRole = "All",
                createdAt = System.currentTimeMillis()
            )
            useCases.sendNotification(notification)
            _formState.update { it.copy(successMessage = "Emergency Broadcast Sent To All Active Devices!") }
        }
    }

    fun clearMessages() {
        _formState.update { it.copy(successMessage = null, errorMessage = null) }
    }
}
