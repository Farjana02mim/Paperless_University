package com.example

import com.example.core.common.Resource
import com.example.domain.model.CourseItem
import com.example.domain.model.LibraryBook
import com.example.domain.model.NotificationItem
import com.example.domain.model.StudentDashboardData
import com.example.domain.repository.StudentDashboardRepository
import com.example.domain.usecase.GetStudentDashboardUseCase
import com.example.domain.usecase.ScanAttendanceQrUseCase
import com.example.ui.dashboard.student.StudentDashboardViewModel
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

@OptIn(ExperimentalCoroutinesApi::class)
class StudentDashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeStudentDashboardRepository : StudentDashboardRepository {
        override suspend fun getStudentDashboardData(): Resource<StudentDashboardData> {
            return Resource.Success(
                StudentDashboardData(
                    studentName = "Alex Mercer",
                    cgpa = 3.85f
                )
            )
        }

        override fun observeNotifications(): Flow<List<NotificationItem>> = flowOf(emptyList())

        override suspend fun markNotificationAsRead(id: String): Resource<Unit> = Resource.Success(Unit)

        override suspend fun deleteNotification(id: String): Resource<Unit> = Resource.Success(Unit)

        override suspend fun scanAttendanceQr(qrCodeData: String): Resource<String> {
            return Resource.Success("Attendance logged successfully!")
        }

        override suspend fun getStudentCourses(): Resource<List<CourseItem>> = Resource.Success(emptyList())

        override suspend fun getLibraryBooks(): Resource<List<LibraryBook>> = Resource.Success(emptyList())
    }

    private lateinit var viewModel: StudentDashboardViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val fakeRepo = FakeStudentDashboardRepository()
        viewModel = StudentDashboardViewModel(
            getStudentDashboardUseCase = GetStudentDashboardUseCase(fakeRepo),
            scanAttendanceQrUseCase = ScanAttendanceQrUseCase(fakeRepo)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadDashboardData_success_updatesUiState() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Alex Mercer", state.data.studentName)
        assertEquals(3.85f, state.data.cgpa)
    }

    @Test
    fun scanQrCode_success_updatesScanMessage() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.scanQrCode("ATTENDANCE_123")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Attendance logged successfully!", viewModel.uiState.value.qrScanMessage)
    }
}
