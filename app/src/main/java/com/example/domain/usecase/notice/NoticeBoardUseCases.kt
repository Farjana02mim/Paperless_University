package com.example.domain.usecase.notice

import com.example.core.common.Resource
import com.example.domain.model.Notice
import com.example.domain.model.NoticeComment
import com.example.domain.model.SmartNotification
import com.example.domain.repository.NoticeBoardRepository
import kotlinx.coroutines.flow.Flow

class GetNoticesUseCase(
    private val repository: NoticeBoardRepository
) {
    operator fun invoke(): Flow<Resource<List<Notice>>> = repository.getNotices()
}

class GetNoticeDetailUseCase(
    private val repository: NoticeBoardRepository
) {
    operator fun invoke(noticeId: String): Flow<Resource<Notice?>> = repository.getNoticeById(noticeId)
}

class CreateNoticeUseCase(
    private val repository: NoticeBoardRepository
) {
    suspend operator fun invoke(notice: Notice): Resource<Unit> = repository.createNotice(notice)
}

class UpdateNoticeUseCase(
    private val repository: NoticeBoardRepository
) {
    suspend operator fun invoke(notice: Notice): Resource<Unit> = repository.updateNotice(notice)
}

class DeleteNoticeUseCase(
    private val repository: NoticeBoardRepository
) {
    suspend operator fun invoke(noticeId: String): Resource<Unit> = repository.deleteNotice(noticeId)
}

class ToggleBookmarkNoticeUseCase(
    private val repository: NoticeBoardRepository
) {
    suspend operator fun invoke(noticeId: String): Resource<Boolean> = repository.toggleBookmark(noticeId)
}

class MarkNoticeReadUseCase(
    private val repository: NoticeBoardRepository
) {
    suspend operator fun invoke(noticeId: String): Resource<Unit> = repository.markNoticeAsRead(noticeId)
}

class GetNotificationsUseCase(
    private val repository: NoticeBoardRepository
) {
    operator fun invoke(): Flow<List<SmartNotification>> = repository.getNotifications()
}

class SendNotificationUseCase(
    private val repository: NoticeBoardRepository
) {
    suspend operator fun invoke(notification: SmartNotification): Resource<Unit> = repository.sendNotification(notification)
}

class NoticeBoardUseCases(
    val getNotices: GetNoticesUseCase,
    val getNoticeDetail: GetNoticeDetailUseCase,
    val createNotice: CreateNoticeUseCase,
    val updateNotice: UpdateNoticeUseCase,
    val deleteNotice: DeleteNoticeUseCase,
    val toggleBookmark: ToggleBookmarkNoticeUseCase,
    val markNoticeRead: MarkNoticeReadUseCase,
    val getNotifications: GetNotificationsUseCase,
    val sendNotification: SendNotificationUseCase
)
