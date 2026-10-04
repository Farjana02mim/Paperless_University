package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.core.datastore.DataStoreManager
import com.example.core.common.FirebaseUtil
import com.example.data.local.AppDatabase
import com.example.data.repository.AuthRepositoryImpl
import com.example.data.repository.NoticeBoardRepositoryImpl
import com.example.domain.usecase.CheckEmailVerificationUseCase
import com.example.domain.usecase.ForgotPasswordUseCase
import com.example.domain.usecase.LoginUseCase
import com.example.domain.usecase.LogoutUseCase
import com.example.domain.usecase.RegisterUseCase
import com.example.domain.usecase.SendEmailVerificationUseCase
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
import com.example.ui.auth.emailverification.EmailVerificationScreen
import com.example.ui.auth.emailverification.EmailVerificationViewModel
import com.example.ui.auth.forgotpassword.ForgotPasswordScreen
import com.example.ui.auth.forgotpassword.ForgotPasswordViewModel
import com.example.ui.auth.login.LoginScreen
import com.example.ui.auth.login.LoginViewModel
import com.example.ui.auth.register.RegisterScreen
import com.example.ui.auth.register.RegisterViewModel
import com.example.ui.dashboard.AdminDashboardScreen
import com.example.ui.dashboard.StudentDashboardScreen
import com.example.ui.dashboard.TeacherDashboardScreen
import com.example.ui.notice.AdminNoticeViewModel
import com.example.ui.notice.NoticeBoardViewModel
import com.example.ui.notice.NoticeDetailViewModel
import com.example.ui.notice.NotificationCenterViewModel
import com.example.ui.notice.admin.CreateEditNoticeScreen
import com.example.ui.notice.admin.NoticeAnalyticsScreen
import com.example.ui.notice.screens.NoticeBoardScreen
import com.example.ui.notice.screens.NoticeDetailScreen
import com.example.ui.notice.screens.NotificationCenterScreen
import com.example.ui.onboarding.OnboardingScreen
import com.example.ui.onboarding.OnboardingViewModel
import com.example.ui.splash.SplashScreen
import com.example.ui.splash.SplashViewModel
import com.example.ui.welcome.WelcomeScreen
import com.example.data.repository.AttendanceRepositoryImpl
import com.example.data.repository.AssignmentRepositoryImpl
import com.example.data.repository.ResultRepositoryImpl
import com.example.data.repository.FinanceRepositoryImpl
import com.example.ui.assignment.*
import com.example.ui.result.*
import com.example.ui.finance.*
import com.example.ui.attendance.AdminAttendanceAnalyticsScreen
import com.example.ui.attendance.AdminAttendanceViewModel
import com.example.ui.attendance.AttendanceViewModel
import com.example.ui.attendance.LeaveRequestScreen
import com.example.ui.attendance.StudentAttendanceScreen
import com.example.ui.attendance.StudentQRScannerScreen
import com.example.ui.attendance.TeacherAttendanceDashboardScreen
import com.example.ui.attendance.TeacherAttendanceViewModel
import com.example.ui.attendance.TeacherQRGeneratorScreen
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current

    // Service container
    val dataStoreManager = remember { DataStoreManager(context) }
    val authRepository = remember { AuthRepositoryImpl(dataStoreManager = dataStoreManager) }

    // Database & Repository setup
    val db = remember { AppDatabase.getInstance(context) }
    val noticeBoardDao = remember { db.noticeBoardDao() }
    val attendanceDao = remember { db.attendanceDao() }
    val assignmentDao = remember { db.assignmentDao() }
    val resultDao = remember { db.resultDao() }
    val financeDao = remember { db.financeDao() }
    val libraryDao = remember { db.libraryDao() }
    val admissionDao = remember { db.admissionDao() }
    val aiDao = remember { db.aiDao() }
    val identityDao = remember { db.identityDao() }
    val campusDao = remember { db.campusDao() }
    val careerDao = remember { db.careerDao() }
    val auditLogDao = remember { db.auditLogDao() }
    val firestore = remember { FirebaseUtil.getSafeFirestore(context) }
    val noticeBoardRepository = remember { NoticeBoardRepositoryImpl(noticeBoardDao, firestore) }
    val attendanceRepository = remember { AttendanceRepositoryImpl(attendanceDao, firestore) }
    val assignmentRepository = remember { AssignmentRepositoryImpl(assignmentDao, firestore) }
    val resultRepository = remember { ResultRepositoryImpl(resultDao, firestore) }
    val financeRepository = remember { FinanceRepositoryImpl(financeDao) }
    val libraryRepository = remember { com.example.data.repository.library.LibraryRepositoryImpl(libraryDao, firestore) }
    val admissionRepository = remember { com.example.data.repository.admission.AdmissionRepositoryImpl(admissionDao, firestore) }
    val aiRepository = remember { com.example.data.repository.ai.AIRepositoryImpl(aiDao = aiDao, firestore = firestore) }
    val chatRepository = remember { com.example.data.repository.ai.ChatRepositoryImpl(aiDao = aiDao) }
    val recommendationRepository = remember { com.example.data.repository.ai.RecommendationRepositoryImpl(aiDao = aiDao) }
    val predictionRepository = remember { com.example.data.repository.ai.PredictionRepositoryImpl() }
    val voiceRepository = remember { com.example.data.repository.ai.VoiceRepositoryImpl() }
    val identityRepository = remember { com.example.data.repository.identity.IdentityRepositoryImpl(identityDao = identityDao, firestore = firestore) }
    val campusRepository = remember { com.example.data.repository.campus.CampusRepositoryImpl(campusDao = campusDao, firestore = firestore) }
    val careerRepository = remember { com.example.data.repository.career.CareerRepositoryImpl(careerDao = careerDao, firestore = firestore) }
    val noticeBoardUseCases = remember {
        NoticeBoardUseCases(
            GetNoticesUseCase(noticeBoardRepository),
            GetNoticeDetailUseCase(noticeBoardRepository),
            CreateNoticeUseCase(noticeBoardRepository),
            UpdateNoticeUseCase(noticeBoardRepository),
            DeleteNoticeUseCase(noticeBoardRepository),
            ToggleBookmarkNoticeUseCase(noticeBoardRepository),
            MarkNoticeReadUseCase(noticeBoardRepository),
            GetNotificationsUseCase(noticeBoardRepository),
            SendNotificationUseCase(noticeBoardRepository)
        )
    }

    // Auth Use cases
    val loginUseCase = remember { LoginUseCase(authRepository) }
    val registerUseCase = remember { RegisterUseCase(authRepository) }
    val forgotPasswordUseCase = remember { ForgotPasswordUseCase(authRepository) }
    val sendEmailVerificationUseCase = remember { SendEmailVerificationUseCase(authRepository) }
    val checkEmailVerificationUseCase = remember { CheckEmailVerificationUseCase(authRepository) }
    val logoutUseCase = remember { LogoutUseCase(authRepository) }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Splash Screen
        composable(Screen.Splash.route) {
            val viewModel = remember { SplashViewModel(dataStoreManager, authRepository) }
            SplashScreen(
                viewModel = viewModel,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Onboarding Screen
        composable(Screen.Onboarding.route) {
            val viewModel = remember { OnboardingViewModel(dataStoreManager) }
            OnboardingScreen(
                viewModel = viewModel,
                onNavigateToWelcome = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // Welcome Screen
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToGuestDashboard = { navController.navigate(Screen.StudentDashboard.route) }
            )
        }

        // Login Screen
        composable(Screen.Login.route) {
            val viewModel = remember { LoginViewModel(loginUseCase) }
            LoginScreen(
                viewModel = viewModel,
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                onNavigateToDashboard = { dashboardRoute ->
                    navController.navigate(dashboardRoute) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }

        // Register Screen
        composable(Screen.Register.route) {
            val viewModel = remember { RegisterViewModel(registerUseCase) }
            RegisterScreen(
                viewModel = viewModel,
                onNavigateToEmailVerification = {
                    navController.navigate(Screen.EmailVerification.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        // Forgot Password Screen
        composable(Screen.ForgotPassword.route) {
            val viewModel = remember { ForgotPasswordViewModel(forgotPasswordUseCase) }
            ForgotPasswordScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Email Verification Screen
        composable(Screen.EmailVerification.route) {
            val viewModel = remember {
                EmailVerificationViewModel(
                    checkEmailVerificationUseCase,
                    sendEmailVerificationUseCase,
                    logoutUseCase,
                    dataStoreManager
                )
            }
            EmailVerificationScreen(
                viewModel = viewModel,
                onVerifiedNavigateToDashboard = { dashboardRoute ->
                    navController.navigate(dashboardRoute) {
                        popUpTo(Screen.EmailVerification.route) { inclusive = true }
                    }
                },
                onNavigateToWelcome = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Student Dashboard
        composable(Screen.StudentDashboard.route) {
            StudentDashboardScreen(
                onLogout = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToFinance = {
                    navController.navigate(Screen.StudentFinanceDashboard.route)
                },
                onNavigateToAdmission = {
                    navController.navigate(Screen.ApplicantDashboard.route)
                },
                onNavigateToAi = {
                    navController.navigate(Screen.AiStudentAssistant.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.UserProfile.route)
                },
                onNavigateToCampusServices = {
                    navController.navigate(Screen.CampusServicesHub.route)
                },
                onNavigateToCareer = {
                    navController.navigate(Screen.CareerDashboard.route)
                }
            )
        }

        // Teacher Dashboard
        composable(Screen.TeacherDashboard.route) {
            TeacherDashboardScreen(
                onLogout = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Admin Dashboard
        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                onLogout = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Digital Notice Board Screen
        composable(Screen.NoticeBoard.route) {
            val viewModel = remember { NoticeBoardViewModel(noticeBoardUseCases) }
            NoticeBoardScreen(
                viewModel = viewModel,
                onNoticeClick = { noticeId ->
                    navController.navigate(Screen.NoticeDetail.createRoute(noticeId))
                },
                onNavigateToNotifications = {
                    navController.navigate(Screen.NotificationCenter.route)
                },
                onNavigateToCreateNotice = {
                    navController.navigate(Screen.CreateNotice.route)
                },
                onNavigateBack = { navController.popBackStack() },
                userRole = "Administrator"
            )
        }

        // Notice Detail Screen
        composable(
            route = Screen.NoticeDetail.route,
            arguments = listOf(navArgument("noticeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val noticeId = backStackEntry.arguments?.getString("noticeId") ?: ""
            val viewModel = remember(noticeId) {
                NoticeDetailViewModel(noticeBoardUseCases, noticeBoardRepository, noticeId)
            }
            NoticeDetailScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Smart Notification Center
        composable(Screen.NotificationCenter.route) {
            val viewModel = remember { NotificationCenterViewModel(noticeBoardUseCases, noticeBoardRepository) }
            NotificationCenterScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNotificationClick = { noticeId ->
                    navController.navigate(Screen.NoticeDetail.createRoute(noticeId))
                }
            )
        }

        // Create / Publish Notice Screen
        composable(Screen.CreateNotice.route) {
            val viewModel = remember { AdminNoticeViewModel(noticeBoardUseCases) }
            CreateEditNoticeScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Notice Analytics Screen
        composable(Screen.NoticeAnalytics.route) {
            NoticeAnalyticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // --- Smart Attendance Routes ---
        composable(Screen.StudentAttendance.route) {
            val viewModel = remember { AttendanceViewModel(attendanceRepository) }
            StudentAttendanceScreen(
                viewModel = viewModel,
                onNavigateToScan = { navController.navigate(Screen.StudentQRScanner.route) },
                onNavigateToLeaveRequest = { navController.navigate(Screen.LeaveRequest.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.StudentQRScanner.route) {
            val viewModel = remember { AttendanceViewModel(attendanceRepository) }
            StudentQRScannerScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.TeacherAttendanceDashboard.route) {
            val viewModel = remember { TeacherAttendanceViewModel(attendanceRepository) }
            TeacherAttendanceDashboardScreen(
                viewModel = viewModel,
                onNavigateToQrGenerator = { navController.navigate(Screen.TeacherQRGenerator.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.TeacherQRGenerator.route) {
            val viewModel = remember { TeacherAttendanceViewModel(attendanceRepository) }
            TeacherQRGeneratorScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminAttendanceAnalytics.route) {
            val viewModel = remember { AdminAttendanceViewModel(attendanceRepository) }
            AdminAttendanceAnalyticsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.LeaveRequest.route) {
            val viewModel = remember { AttendanceViewModel(attendanceRepository) }
            LeaveRequestScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // --- Enterprise Assignment Management Routes ---
        composable(Screen.StudentAssignmentDashboard.route) {
            val viewModel = remember { AssignmentViewModel(assignmentRepository) }
            StudentAssignmentDashboardScreen(
                viewModel = viewModel,
                onNavigateToDetail = { id -> navController.navigate(Screen.AssignmentDetail.createRoute(id)) },
                onNavigateToCalendar = { navController.navigate(Screen.AssignmentCalendar.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AssignmentDetail.route) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("assignmentId") ?: ""
            val viewModel = remember { AssignmentViewModel(assignmentRepository) }
            AssignmentDetailScreen(
                assignmentId = id,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.TeacherAssignmentControl.route) {
            val viewModel = remember { TeacherAssignmentViewModel(assignmentRepository) }
            TeacherAssignmentControlScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AssignmentAnalytics.route) {
            val viewModel = remember { AssignmentAnalyticsViewModel(assignmentRepository) }
            AssignmentAnalyticsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AssignmentCalendar.route) {
            val viewModel = remember { AssignmentViewModel(assignmentRepository) }
            AssignmentCalendarScreen(
                viewModel = viewModel,
                onNavigateToDetail = { id -> navController.navigate(Screen.AssignmentDetail.createRoute(id)) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // --- Enterprise Result Management Routes ---
        composable(Screen.StudentAcademicDashboard.route) {
            val viewModel = remember { StudentResultViewModel(resultRepository) }
            StudentAcademicDashboardScreen(
                viewModel = viewModel,
                onNavigateToTranscript = { navController.navigate(Screen.TranscriptView.route) },
                onNavigateToAnalytics = { navController.navigate(Screen.ResultAnalytics.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.TeacherMarkEntry.route) {
            val viewModel = remember { TeacherMarkEntryViewModel(resultRepository) }
            TeacherMarkEntryScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminResultPublish.route) {
            val viewModel = remember { AdminResultPublishViewModel(resultRepository) }
            AdminResultPublishScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.TranscriptView.route) {
            val viewModel = remember { StudentResultViewModel(resultRepository) }
            TranscriptViewScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ResultAnalytics.route) {
            val viewModel = remember { ResultAnalyticsViewModel(resultRepository) }
            ResultAnalyticsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // --- University Financial Management & Fee Payment System Routes ---
        composable(Screen.StudentFinanceDashboard.route) {
            val viewModel = remember { StudentFinanceViewModel(financeRepository) }
            StudentFinanceDashboardScreen(
                viewModel = viewModel,
                onNavigateToCheckout = { invoice ->
                    navController.navigate(Screen.OnlinePaymentCheckout.createRoute(invoice.invoiceId))
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.PaymentHistoryReceipt.route)
                },
                onNavigateToAnalytics = {
                    navController.navigate(Screen.AdminFinanceAnalytics.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.OnlinePaymentCheckout.route,
            arguments = listOf(navArgument("invoiceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val invoiceId = backStackEntry.arguments?.getString("invoiceId") ?: ""
            val studentVm = remember { StudentFinanceViewModel(financeRepository) }
            val checkoutVm = remember { PaymentCheckoutViewModel(financeRepository) }
            val studentState = studentVm.uiState.collectAsState().value
            val targetInvoice = studentState.invoices.find { it.invoiceId == invoiceId } ?: studentState.selectedInvoice ?: com.example.domain.model.finance.InvoiceModel(invoiceId = invoiceId, dueAmount = 32300.0)

            OnlinePaymentCheckoutScreen(
                invoice = targetInvoice,
                viewModel = checkoutVm,
                onPaymentSuccess = { payment ->
                    navController.navigate(Screen.PaymentHistoryReceipt.route) {
                        popUpTo(Screen.StudentFinanceDashboard.route)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.PaymentHistoryReceipt.route) {
            val studentVm = remember { StudentFinanceViewModel(financeRepository) }
            val studentState = studentVm.uiState.collectAsState().value
            PaymentHistoryReceiptScreen(
                payment = studentState.payments.firstOrNull(),
                invoice = studentState.invoices.firstOrNull(),
                allPayments = studentState.payments,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminFinanceAnalytics.route) {
            val viewModel = remember { AdminFinanceAnalyticsViewModel(financeRepository) }
            AdminFinanceAnalyticsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // --- Enterprise Digital Library & Cloud Storage Routes ---
        composable(Screen.DigitalLibraryHome.route) {
            val viewModel = remember { com.example.ui.library.DigitalLibraryViewModel(libraryRepository) }
            com.example.ui.library.screens.DigitalLibraryMainScreen(
                viewModel = viewModel,
                onResourceClick = { id -> navController.navigate(Screen.ResourceDetail.createRoute(id)) },
                onReadResourceClick = { id -> navController.navigate(Screen.PdfReader.createRoute(id)) },
                onPlayMediaClick = { id -> navController.navigate(Screen.MediaViewer.createRoute(id)) },
                onOpenCloudStorageClick = { navController.navigate(Screen.CloudFileManager.route) },
                onOpenDownloadsClick = { navController.navigate(Screen.DownloadManager.route) },
                onOpenAnalyticsClick = { navController.navigate(Screen.LibraryAnalytics.route) }
            )
        }

        composable(
            route = Screen.ResourceDetail.route,
            arguments = listOf(navArgument("resourceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val resourceId = backStackEntry.arguments?.getString("resourceId") ?: ""
            val viewModel = remember { com.example.ui.library.DigitalLibraryViewModel(libraryRepository) }
            val state = viewModel.uiState.collectAsState().value
            val targetRes = state.resources.find { it.resourceId == resourceId } ?: com.example.domain.model.library.LibraryResourceModel(resourceId = resourceId, title = "Academic Document")

            com.example.ui.library.screens.ResourceDetailScreen(
                resource = targetRes,
                onBackClick = { navController.popBackStack() },
                onReadClick = { id -> navController.navigate(Screen.PdfReader.createRoute(id)) },
                onPlayMediaClick = { id -> navController.navigate(Screen.MediaViewer.createRoute(id)) },
                onDownloadClick = { id -> viewModel.startDownload(id) },
                onToggleFavorite = { id, currentStatus -> viewModel.toggleFavorite(id, currentStatus) }
            )
        }

        composable(
            route = Screen.PdfReader.route,
            arguments = listOf(navArgument("resourceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val resourceId = backStackEntry.arguments?.getString("resourceId") ?: ""
            val viewModel = remember(resourceId) {
                com.example.ui.library.PdfReaderViewModel(libraryRepository).apply {
                    loadResource(resourceId)
                }
            }
            com.example.ui.library.screens.PdfReaderScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.MediaViewer.route,
            arguments = listOf(navArgument("resourceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val resourceId = backStackEntry.arguments?.getString("resourceId") ?: ""
            val viewModel = remember(resourceId) {
                com.example.ui.library.MediaViewerViewModel(libraryRepository).apply {
                    loadMediaResource(resourceId)
                }
            }
            com.example.ui.library.screens.VideoAudioPlayerScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.DownloadManager.route) {
            val viewModel = remember { com.example.ui.library.DigitalLibraryViewModel(libraryRepository) }
            com.example.ui.library.screens.DownloadManagerScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onOpenResource = { id -> navController.navigate(Screen.PdfReader.createRoute(id)) }
            )
        }

        composable(Screen.CloudFileManager.route) {
            val viewModel = remember { com.example.ui.library.CloudFileManagerViewModel(libraryRepository) }
            com.example.ui.library.screens.CloudFileManagerScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.LibraryAnalytics.route) {
            val viewModel = remember { com.example.ui.library.LibraryAnalyticsViewModel(libraryRepository) }
            com.example.ui.library.screens.LibraryAnalyticsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // --- Enterprise Online Admission System Routes ---
        composable(Screen.ApplicantDashboard.route) {
            val viewModel = remember { com.example.ui.admission.ApplicantDashboardViewModel(admissionRepository) }
            com.example.ui.admission.screens.ApplicantDashboardScreen(
                viewModel = viewModel,
                onNavigateToForm = { navController.navigate(Screen.MultiStepAdmissionForm.route) },
                onNavigateToAdmitCard = { id -> navController.navigate(Screen.AdmitCardView.createRoute(id)) },
                onNavigateToMeritList = { navController.navigate(Screen.MeritListView.route) },
                onNavigateToAdmin = { navController.navigate(Screen.AdmissionAdminPortal.route) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.MultiStepAdmissionForm.route) {
            val viewModel = remember { com.example.ui.admission.AdmissionFormViewModel(admissionRepository) }
            com.example.ui.admission.screens.MultiStepAdmissionFormScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onSubmitSuccess = { _ ->
                    navController.navigate(Screen.ApplicantDashboard.route) {
                        popUpTo(Screen.MultiStepAdmissionForm.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.AdmitCardView.route,
            arguments = listOf(navArgument("applicationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val appId = backStackEntry.arguments?.getString("applicationId") ?: ""
            val viewModel = remember { com.example.ui.admission.ApplicantDashboardViewModel(admissionRepository) }
            val state = viewModel.uiState.collectAsState().value

            com.example.ui.admission.screens.AdmitCardViewScreen(
                admitCard = state.admitCard,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.MeritListView.route) {
            val viewModel = remember { com.example.ui.admission.MeritListViewModel(admissionRepository) }
            com.example.ui.admission.screens.MeritListScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.AdmissionAdminPortal.route) {
            val viewModel = remember { com.example.ui.admission.AdmissionAdminViewModel(admissionRepository) }
            com.example.ui.admission.screens.AdmissionAdminScreen(
                viewModel = viewModel,
                onNavigateToAnalytics = { navController.navigate(Screen.AdmissionAnalytics.route) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.AdmissionAnalytics.route) {
            val viewModel = remember { com.example.ui.admission.AdmissionAdminViewModel(admissionRepository) }
            com.example.ui.admission.screens.AdmissionAnalyticsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // --- Enterprise AI Platform Routes ---
        composable(Screen.AiChatHub.route) {
            val viewModel = remember {
                com.example.ui.ai.AiChatViewModel(
                    aiRepository = aiRepository,
                    chatRepository = chatRepository,
                    voiceRepository = voiceRepository
                )
            }
            com.example.ui.ai.screens.AiChatHubScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.AiDashboard.route) {
            val viewModel = remember {
                com.example.ui.ai.AiDashboardViewModel(
                    aiRepository = aiRepository,
                    predictionRepository = predictionRepository
                )
            }
            com.example.ui.ai.screens.AiAnalyticsDashboardScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.AiStudentAssistant.route) {
            val viewModel = remember {
                com.example.ui.ai.AiStudentAssistantViewModel(
                    recommendationRepository = recommendationRepository,
                    predictionRepository = predictionRepository
                )
            }
            com.example.ui.ai.screens.AiStudentAssistantScreen(
                viewModel = viewModel,
                onNavigateToChat = { navController.navigate(Screen.AiChatHub.route) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.AiTeacherAssistant.route) {
            com.example.ui.ai.screens.AiTeacherAssistantScreen(
                onNavigateToChat = { navController.navigate(Screen.AiChatHub.route) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.AiAdminAssistant.route) {
            com.example.ui.ai.screens.AiAdminAssistantScreen(
                onNavigateToAnalytics = { navController.navigate(Screen.AiDashboard.route) },
                onBackClick = { navController.popBackStack() }
            )
        }

        // --- Digital Identity & Account Management Platform Routes ---
        composable(Screen.UserProfile.route) {
            val viewModel = remember {
                com.example.ui.identity.UserProfileViewModel(identityRepository = identityRepository)
            }
            com.example.ui.identity.screens.UserProfileScreen(
                viewModel = viewModel,
                onNavigateToDigitalId = { navController.navigate(Screen.DigitalIdCard.route) },
                onNavigateToSettings = { navController.navigate(Screen.AppSettings.route) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.DigitalIdCard.route) {
            val viewModel = remember {
                com.example.ui.identity.DigitalIdViewModel(identityRepository = identityRepository)
            }
            com.example.ui.identity.screens.DigitalIdCardScreen(
                viewModel = viewModel,
                onNavigateToQrVerify = { navController.navigate(Screen.QrPassVerification.route) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.QrPassVerification.route) {
            val viewModel = remember {
                com.example.ui.identity.DigitalIdViewModel(identityRepository = identityRepository)
            }
            com.example.ui.identity.screens.QrPassVerificationScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.SecurityAndDevices.route) {
            val viewModel = remember {
                com.example.ui.identity.SecurityAndDeviceViewModel(identityRepository = identityRepository)
            }
            com.example.ui.identity.screens.SecurityAndDevicesScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.AppSettings.route) {
            val viewModel = remember {
                com.example.ui.identity.SettingsViewModel(identityRepository = identityRepository)
            }
            com.example.ui.identity.screens.AppSettingsScreen(
                viewModel = viewModel,
                onNavigateToSecurityDevices = { navController.navigate(Screen.SecurityAndDevices.route) },
                onBackClick = { navController.popBackStack() }
            )
        }

        // --- Smart Campus Services Platform Routes ---
        composable(Screen.CampusServicesHub.route) {
            val viewModel = remember {
                com.example.ui.campus.CampusServicesViewModel(campusRepository = campusRepository)
            }
            com.example.ui.campus.screens.CampusServicesHubScreen(
                viewModel = viewModel,
                onNavigateToTransport = { navController.navigate(Screen.SmartTransport.route) },
                onNavigateToHostel = { navController.navigate(Screen.HostelManagement.route) },
                onNavigateToEmergency = { navController.navigate(Screen.EmergencySos.route) },
                onNavigateToMedical = { navController.navigate(Screen.MedicalCenter.route) },
                onNavigateToMap = { navController.navigate(Screen.CampusMap.route) },
                onNavigateToEvents = { navController.navigate(Screen.EventsAndClubs.route) },
                onNavigateToEco = { navController.navigate(Screen.EcoMonitoring.route) },
                onNavigateToResearch = { navController.navigate(Screen.JstuResearchHub.route) },
                onNavigateToExamController = { navController.navigate(Screen.ExamControllerPortal.route) },
                onNavigateToDining = { navController.navigate(Screen.DiningTokenPass.route) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.SmartTransport.route) {
            val viewModel = remember {
                com.example.ui.campus.CampusServicesViewModel(campusRepository = campusRepository)
            }
            com.example.ui.campus.screens.SmartTransportScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.HostelManagement.route) {
            val viewModel = remember {
                com.example.ui.campus.CampusServicesViewModel(campusRepository = campusRepository)
            }
            com.example.ui.campus.screens.HostelManagementScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.EmergencySos.route) {
            val viewModel = remember {
                com.example.ui.campus.CampusServicesViewModel(campusRepository = campusRepository)
            }
            com.example.ui.campus.screens.EmergencySosScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.MedicalCenter.route) {
            val viewModel = remember {
                com.example.ui.campus.CampusServicesViewModel(campusRepository = campusRepository)
            }
            com.example.ui.campus.screens.MedicalCenterScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.CampusMap.route) {
            val viewModel = remember {
                com.example.ui.campus.CampusServicesViewModel(campusRepository = campusRepository)
            }
            com.example.ui.campus.screens.CampusMapScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.EventsAndClubs.route) {
            val viewModel = remember {
                com.example.ui.campus.CampusServicesViewModel(campusRepository = campusRepository)
            }
            com.example.ui.campus.screens.EventsAndClubsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.EcoMonitoring.route) {
            val viewModel = remember {
                com.example.ui.campus.CampusServicesViewModel(campusRepository = campusRepository)
            }
            com.example.ui.campus.screens.EcoMonitoringScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // --- Career Development Platform Routes ---
        composable(Screen.CareerDashboard.route) {
            val viewModel = remember {
                com.example.ui.career.CareerViewModel(careerRepository = careerRepository)
            }
            com.example.ui.career.screens.CareerDashboardScreen(
                viewModel = viewModel,
                onNavigateToJobs = { navController.navigate(Screen.JobsAndInternships.route) },
                onNavigateToResume = { navController.navigate(Screen.ResumeBuilder.route) },
                onNavigateToMentorship = { navController.navigate(Screen.AlumniAndMentorship.route) },
                onNavigateToAnalytics = { navController.navigate(Screen.PlacementAnalytics.route) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.JobsAndInternships.route) {
            val viewModel = remember {
                com.example.ui.career.CareerViewModel(careerRepository = careerRepository)
            }
            com.example.ui.career.screens.JobsAndInternshipsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.ResumeBuilder.route) {
            val viewModel = remember {
                com.example.ui.career.CareerViewModel(careerRepository = careerRepository)
            }
            com.example.ui.career.screens.ResumeBuilderScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.AlumniAndMentorship.route) {
            val viewModel = remember {
                com.example.ui.career.CareerViewModel(careerRepository = careerRepository)
            }
            com.example.ui.career.screens.AlumniAndMentorshipScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.PlacementAnalytics.route) {
            val viewModel = remember {
                com.example.ui.career.CareerViewModel(careerRepository = careerRepository)
            }
            com.example.ui.career.screens.PlacementAnalyticsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.EnterpriseSecurityDashboard.route) {
            val viewModel = remember {
                com.example.ui.security.SecurityViewModel(auditLogDao = auditLogDao)
            }
            com.example.ui.security.EnterpriseSecurityDashboardScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.JstuResearchHub.route) {
            com.example.ui.research.JstuResearchScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.ExamControllerPortal.route) {
            com.example.ui.exam.ExamControllerScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.DiningTokenPass.route) {
            com.example.ui.hostel.DiningTokenScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
