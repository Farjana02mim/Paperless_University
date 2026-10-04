package com.example

import com.example.core.common.Resource
import com.example.domain.model.Notice
import com.example.domain.model.NoticeCategory
import com.example.domain.model.NoticeComment
import com.example.domain.model.NoticePriority
import com.example.domain.model.SmartNotification
import com.example.domain.repository.NoticeBoardRepository
import com.example.domain.usecase.notice.CreateNoticeUseCase
import com.example.domain.usecase.notice.DeleteNoticeUseCase
import com.example.domain.usecase.notice.GetNoticeDetailUseCase
import com.example.domain.usecase.notice.GetNotificationsUseCase
import com.example.domain.usecase.notice.GetNoticesUseCase
import com.example.domain.usecase.notice.MarkNoticeReadUseCase
import com.example.domain.usecase.notice.NoticeBoardUseCases
import com.example.domain.usecase.notice.SendNotificationUseCase
import com.example.domain.usecase.notice.ToggleBookmarkNoticeUseCase
import com.example.domain.usecase.notice.UpdateNoticeUseCase
import com.example.ui.notice.NoticeBoardViewModel
import com.example.ui.notice.NoticeSortOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

class FakeNoticeBoardRepository : NoticeBoardRepository {
    private val notices = mutableListOf(
        Notice(
            noticeId = "test-1",
            title = "Alpha Exam Notice",
            category = NoticeCategory.EXAM,
            priority = NoticePriority.HIGH,
            isPinned = true,
            publishDate = 2000L
        ),
        Notice(
            noticeId = "test-2",
            title = "Beta Scholarship Notice",
            category = NoticeCategory.SCHOLARSHIP,
            priority = NoticePriority.NORMAL,
            isPinned = false,
            publishDate = 1000L
        )
    )

    override fun getNotices(): Flow<Resource<List<Notice>>> = flowOf(Resource.Success(notices.toList()))
    override fun getNoticeById(noticeId: String): Flow<Resource<Notice?>> = flowOf(Resource.Success(notices.find { it.noticeId == noticeId }))
    override suspend fun createNotice(notice: Notice): Resource<Unit> {
        notices.add(notice)
        return Resource.Success(Unit)
    }
    override suspend fun updateNotice(notice: Notice): Resource<Unit> = Resource.Success(Unit)
    override suspend fun deleteNotice(noticeId: String): Resource<Unit> = Resource.Success(Unit)
    override suspend fun togglePinNotice(noticeId: String, isPinned: Boolean): Resource<Unit> = Resource.Success(Unit)
    override suspend fun toggleBookmark(noticeId: String): Resource<Boolean> = Resource.Success(true)
    override suspend fun markNoticeAsRead(noticeId: String): Resource<Unit> = Resource.Success(Unit)
    override fun getNotifications(): Flow<List<SmartNotification>> = flowOf(emptyList())
    override suspend fun sendNotification(notification: SmartNotification): Resource<Unit> = Resource.Success(Unit)
    override suspend fun markNotificationRead(id: String): Resource<Unit> = Resource.Success(Unit)
    override suspend fun markAllNotificationsRead(): Resource<Unit> = Resource.Success(Unit)
    override suspend fun addComment(noticeId: String, content: String, authorName: String): Resource<Unit> = Resource.Success(Unit)
    override fun getComments(noticeId: String): Flow<List<NoticeComment>> = flowOf(emptyList())
    override suspend fun incrementViewCount(noticeId: String): Resource<Unit> = Resource.Success(Unit)
    override suspend fun incrementDownloadCount(noticeId: String): Resource<Unit> = Resource.Success(Unit)
    override suspend fun incrementShareCount(noticeId: String): Resource<Unit> = Resource.Success(Unit)
}

@OptIn(ExperimentalCoroutinesApi::class)
class NoticeBoardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeNoticeBoardRepository
    private lateinit var useCases: NoticeBoardUseCases
    private lateinit var viewModel: NoticeBoardViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeNoticeBoardRepository()
        useCases = NoticeBoardUseCases(
            GetNoticesUseCase(fakeRepository),
            GetNoticeDetailUseCase(fakeRepository),
            CreateNoticeUseCase(fakeRepository),
            UpdateNoticeUseCase(fakeRepository),
            DeleteNoticeUseCase(fakeRepository),
            ToggleBookmarkNoticeUseCase(fakeRepository),
            MarkNoticeReadUseCase(fakeRepository),
            GetNotificationsUseCase(fakeRepository),
            SendNotificationUseCase(fakeRepository)
        )
        viewModel = NoticeBoardViewModel(useCases)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadNotices_populatesFilteredNotices() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.notices.size)
        assertEquals(2, state.filteredNotices.size)
    }

    @Test
    fun selectCategory_filtersNoticesCorrectly() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectCategory(NoticeCategory.EXAM)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.filteredNotices.size)
        assertEquals("test-1", state.filteredNotices.first().noticeId)
    }

    @Test
    fun onSearchQueryChange_filtersByTitleKeyword() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSearchQueryChange("Scholarship")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.filteredNotices.size)
        assertEquals("test-2", state.filteredNotices.first().noticeId)
    }

    @Test
    fun selectSortOption_newestFirstSortsByPublishDate() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectSortOption(NoticeSortOption.NEWEST_FIRST)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("test-1", state.filteredNotices[0].noticeId)
        assertEquals("test-2", state.filteredNotices[1].noticeId)
    }
}
