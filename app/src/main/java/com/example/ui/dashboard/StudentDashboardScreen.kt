package com.example.ui.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.core.datastore.DataStoreManager
import com.example.data.local.AppDatabase
import com.example.data.repository.AuthRepositoryImpl
import com.example.data.repository.StudentDashboardRepositoryImpl
import com.example.domain.usecase.DeleteNotificationUseCase
import com.example.domain.usecase.GetLibraryBooksUseCase
import com.example.domain.usecase.GetNotificationsUseCase
import com.example.domain.usecase.GetStudentCoursesUseCase
import com.example.domain.usecase.GetStudentDashboardUseCase
import com.example.domain.usecase.LogoutUseCase
import com.example.domain.usecase.MarkNotificationReadUseCase
import com.example.domain.usecase.ScanAttendanceQrUseCase
import com.example.ui.dashboard.student.StudentCoursesViewModel
import com.example.ui.dashboard.student.StudentDashboardMainScreen
import com.example.ui.dashboard.student.StudentDashboardViewModel
import com.example.ui.dashboard.student.StudentLibraryViewModel
import com.example.ui.dashboard.student.StudentNotificationsViewModel
import com.example.ui.dashboard.student.StudentProfileViewModel

@Composable
fun StudentDashboardScreen(
    onLogout: () -> Unit,
    onNavigateToFinance: () -> Unit = {},
    onNavigateToAdmission: () -> Unit = {},
    onNavigateToAi: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToCampusServices: () -> Unit = {},
    onNavigateToCareer: () -> Unit = {}
) {
    val context = LocalContext.current

    val dataStoreManager = remember { DataStoreManager(context) }
    val appDatabase = remember { AppDatabase.getInstance(context) }
    val studentDao = remember { appDatabase.studentDao() }

    val studentRepo = remember {
        StudentDashboardRepositoryImpl(studentDao, dataStoreManager)
    }

    val authRepo = remember { AuthRepositoryImpl(dataStoreManager) }

    val dashboardViewModel = remember {
        StudentDashboardViewModel(
            getStudentDashboardUseCase = GetStudentDashboardUseCase(studentRepo),
            scanAttendanceQrUseCase = ScanAttendanceQrUseCase(studentRepo)
        )
    }

    val coursesViewModel = remember {
        StudentCoursesViewModel(
            getStudentCoursesUseCase = GetStudentCoursesUseCase(studentRepo)
        )
    }

    val notificationsViewModel = remember {
        StudentNotificationsViewModel(
            getNotificationsUseCase = GetNotificationsUseCase(studentRepo),
            markNotificationReadUseCase = MarkNotificationReadUseCase(studentRepo),
            deleteNotificationUseCase = DeleteNotificationUseCase(studentRepo)
        )
    }

    val libraryViewModel = remember {
        StudentLibraryViewModel(
            getLibraryBooksUseCase = GetLibraryBooksUseCase(studentRepo)
        )
    }

    val profileViewModel = remember {
        StudentProfileViewModel(
            logoutUseCase = LogoutUseCase(authRepo)
        )
    }

    StudentDashboardMainScreen(
        dashboardViewModel = dashboardViewModel,
        coursesViewModel = coursesViewModel,
        notificationsViewModel = notificationsViewModel,
        libraryViewModel = libraryViewModel,
        profileViewModel = profileViewModel,
        onLogout = onLogout,
        onNavigateToFinance = onNavigateToFinance,
        onNavigateToAdmission = onNavigateToAdmission,
        onNavigateToAi = onNavigateToAi,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToCampusServices = onNavigateToCampusServices,
        onNavigateToCareer = onNavigateToCareer
    )
}
