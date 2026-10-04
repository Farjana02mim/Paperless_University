package com.example.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.admin.AdminRepositoryImpl
import com.example.ui.admin.components.*
import com.example.ui.admin.screens.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMainPortalScreen(
    onNavigateBack: () -> Unit = {}
) {
    val repository = remember { AdminRepositoryImpl() }
    val viewModel = remember { AdminPortalViewModel(repository) }

    val uiState by viewModel.uiState.collectAsState()
    val profile by viewModel.profile.collectAsState()
    val systemMetrics by viewModel.systemMetrics.collectAsState()
    val users by viewModel.users.collectAsState()
    val roles by viewModel.roles.collectAsState()
    val departments by viewModel.departments.collectAsState()
    val courses by viewModel.courses.collectAsState()
    val applications by viewModel.applications.collectAsState()
    val invoices by viewModel.invoices.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val aiInsights by viewModel.aiInsights.collectAsState()
    val reports by viewModel.reports.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("University Admin Control Center", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("${settings.universityName} • Fall 2026", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.openModal(AdminModalType.AI_INSIGHTS_VIEW) }) {
                        Icon(Icons.Default.Psychology, contentDescription = "AI Insights", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = { viewModel.openModal(AdminModalType.SYSTEM_SETTINGS) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Main", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    icon = { Icon(Icons.Default.People, contentDescription = "Users") },
                    label = { Text("Users", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = uiState.selectedTab == 2,
                    onClick = { viewModel.selectTab(2) },
                    icon = { Icon(Icons.Default.AccountTree, contentDescription = "Depts") },
                    label = { Text("Academic", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = uiState.selectedTab == 3,
                    onClick = { viewModel.selectTab(3) },
                    icon = { Icon(Icons.Default.HowToReg, contentDescription = "Admission") },
                    label = { Text("Admit", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = uiState.selectedTab == 4,
                    onClick = { viewModel.selectTab(4) },
                    icon = { Icon(Icons.Default.Payments, contentDescription = "Finance") },
                    label = { Text("Finance", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = uiState.selectedTab == 7,
                    onClick = { viewModel.selectTab(7) },
                    icon = { Icon(Icons.Default.Assessment, contentDescription = "Reports") },
                    label = { Text("Reports", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = uiState.selectedTab == 8,
                    onClick = { viewModel.selectTab(8) },
                    icon = { Icon(Icons.Default.Shield, contentDescription = "Audit") },
                    label = { Text("System", fontSize = 10.sp) }
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.selectedTab) {
                0 -> AdminDashboardTab(
                    profile = profile,
                    metrics = systemMetrics,
                    onOpenModal = { viewModel.openModal(it) },
                    onNavigateTab = { viewModel.selectTab(it) }
                )
                1 -> AdminUserManagementScreen(
                    users = users,
                    roles = roles,
                    searchQuery = uiState.searchQuery,
                    roleFilter = uiState.roleFilter,
                    deptFilter = uiState.deptFilter,
                    selectedUser = uiState.selectedUser,
                    onSearchChange = { viewModel.updateSearchQuery(it) },
                    onFilterChange = { r, d -> viewModel.updateFilters(r, d) },
                    onSelectUser = { viewModel.selectUser(it) },
                    onToggleStatus = { id, st -> viewModel.toggleUserStatus(id, st) },
                    onDeleteUser = { viewModel.deleteUser(it) },
                    onOpenModal = { viewModel.openModal(it) }
                )
                2 -> AdminDepartmentCourseScreen(
                    departments = departments,
                    courses = courses,
                    onOpenModal = { viewModel.openModal(it) }
                )
                3 -> AdminAdmissionScreen(
                    applications = applications,
                    onSelectApplication = { viewModel.selectApplication(it) },
                    onOpenModal = { viewModel.openModal(it) }
                )
                4 -> AdminFinanceScreen(
                    invoices = invoices,
                    onUpdateInvoiceStatus = { id, st -> viewModel.updateInvoiceStatus(id, st) }
                )
                5 -> AdminAttendanceResultsScreen()
                6 -> AdminNoticeCalendarScreen()
                7 -> AdminAnalyticsReportsScreen(
                    reports = reports,
                    aiInsights = aiInsights,
                    onOpenModal = { viewModel.openModal(it) }
                )
                8 -> AdminSettingsSecurityScreen(
                    settings = settings,
                    auditLogs = auditLogs,
                    onOpenModal = { viewModel.openModal(it) }
                )
                else -> AdminDashboardTab(
                    profile = profile,
                    metrics = systemMetrics,
                    onOpenModal = { viewModel.openModal(it) },
                    onNavigateTab = { viewModel.selectTab(it) }
                )
            }

            // Dialog Modals
            when (uiState.activeModal) {
                AdminModalType.CREATE_USER -> CreateUserDialog(
                    onDismiss = { viewModel.closeModal() },
                    onSubmit = { name, email, role, dept, phone ->
                        viewModel.createNewUser(name, email, role, dept, phone)
                    }
                )
                AdminModalType.CREATE_DEPARTMENT -> CreateDepartmentDialog(
                    onDismiss = { viewModel.closeModal() },
                    onSubmit = { code, name, hod, budget ->
                        viewModel.createNewDepartment(code, name, hod, budget)
                    }
                )
                AdminModalType.CREATE_COURSE -> CreateCourseDialog(
                    departments = departments,
                    onDismiss = { viewModel.closeModal() },
                    onSubmit = { code, name, dept, credits, teacher, prereq ->
                        viewModel.createNewCourse(code, name, dept, credits, teacher, prereq)
                    }
                )
                AdminModalType.ADMISSION_ACTION -> AdmissionActionDialog(
                    application = uiState.selectedApplication,
                    onDismiss = { viewModel.closeModal() },
                    onSubmit = { id, st, seat ->
                        viewModel.updateAdmissionApplication(id, st, seat)
                    }
                )
                AdminModalType.GENERATE_REPORT -> GenerateReportDialog(
                    onDismiss = { viewModel.closeModal() },
                    onSubmit = { title, category, format ->
                        viewModel.generateNewReport(title, category, format)
                    }
                )
                AdminModalType.CLOUD_BACKUP -> CloudBackupDialog(
                    onDismiss = { viewModel.closeModal() },
                    onConfirm = { viewModel.triggerBackup() }
                )
                AdminModalType.SYSTEM_SETTINGS -> SystemSettingsDialog(
                    settings = settings,
                    onDismiss = { viewModel.closeModal() },
                    onSubmit = { name, sem, attnd, mfa ->
                        viewModel.saveSystemSettings(name, sem, attnd, mfa)
                    }
                )
                AdminModalType.AI_INSIGHTS_VIEW -> AiInsightsViewDialog(
                    insights = aiInsights,
                    onDismiss = { viewModel.closeModal() }
                )
                else -> {}
            }
        }
    }
}
