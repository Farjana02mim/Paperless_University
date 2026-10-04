package com.example.ui.dashboard.student

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.dashboard.student.screens.StudentCoursesScreen
import com.example.ui.dashboard.student.screens.StudentHomeScreen
import com.example.ui.dashboard.student.screens.StudentLibraryScreen
import com.example.ui.dashboard.student.screens.StudentNotificationsScreen
import com.example.ui.dashboard.student.screens.StudentProfileScreen
import com.example.ui.dashboard.student.widgets.GlobalSearchSheet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class StudentNavTab(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    COURSES("Courses", Icons.Default.School),
    NOTIFICATIONS("Alerts", Icons.Default.Notifications),
    LIBRARY("Library", Icons.Default.Book),
    PROFILE("Profile", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardMainScreen(
    dashboardViewModel: StudentDashboardViewModel,
    coursesViewModel: StudentCoursesViewModel,
    notificationsViewModel: StudentNotificationsViewModel,
    libraryViewModel: StudentLibraryViewModel,
    profileViewModel: StudentProfileViewModel,
    onLogout: () -> Unit,
    onNavigateToFinance: () -> Unit = {},
    onNavigateToAdmission: () -> Unit = {},
    onNavigateToAi: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToCampusServices: () -> Unit = {},
    onNavigateToCareer: () -> Unit = {}
) {
    val uiState by dashboardViewModel.uiState.collectAsState()
    val notifications by notificationsViewModel.notifications.collectAsState()
    val unreadCount = notifications.count { !it.isRead }

    var selectedTab by rememberSaveable { mutableStateOf(StudentNavTab.HOME) }

    val currentDate = remember {
        SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault()).format(Date())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .clickable { onNavigateToProfile() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Hello, ${uiState.data.studentName.substringBefore(" ")} 👋",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = currentDate,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToAi) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Assistant",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { dashboardViewModel.toggleSearchSheet(true) }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { selectedTab = StudentNavTab.NOTIFICATIONS }) {
                        BadgedBox(
                            badge = {
                                if (unreadCount > 0) {
                                    Badge { Text("$unreadCount") }
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            if (selectedTab == StudentNavTab.HOME) {
                FloatingActionButton(
                    onClick = { dashboardViewModel.toggleQrScannerModal(true) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = "Quick Scan Attendance")
                }
            }
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                StudentNavTab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = {
                            if (tab == StudentNavTab.NOTIFICATIONS && unreadCount > 0) {
                                BadgedBox(badge = { Badge { Text("$unreadCount") } }) {
                                    Icon(imageVector = tab.icon, contentDescription = tab.title)
                                }
                            } else {
                                Icon(imageVector = tab.icon, contentDescription = tab.title)
                            }
                        },
                        label = { Text(tab.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = selectedTab, label = "tab_crossfade") { tab ->
                when (tab) {
                    StudentNavTab.HOME -> StudentHomeScreen(
                        uiState = uiState,
                        onRefresh = { dashboardViewModel.refreshDashboard() },
                        onQuickActionClick = { actionKey ->
                            if (actionKey == "digital_id" || actionKey == "profile") {
                                onNavigateToProfile()
                            } else if (actionKey == "qr_scanner") {
                                dashboardViewModel.toggleQrScannerModal(true)
                            } else if (actionKey == "fee_payment") {
                                onNavigateToFinance()
                            } else if (actionKey == "admission") {
                                onNavigateToAdmission()
                            } else if (actionKey == "ai_assistant" || actionKey == "ai_chat") {
                                onNavigateToAi()
                            } else if (actionKey == "campus_services" || actionKey == "bus" || actionKey == "hostel" || actionKey == "emergency" || actionKey == "medical" || actionKey == "transport") {
                                onNavigateToCampusServices()
                            } else if (actionKey == "career" || actionKey == "job" || actionKey == "internship" || actionKey == "placement" || actionKey == "resume") {
                                onNavigateToCareer()
                            } else {
                                dashboardViewModel.showQuickActionDialog(actionKey)
                            }
                        },
                        onNoticeClick = { notice -> dashboardViewModel.selectNotice(notice) },
                        onDismissNoticeDialog = { dashboardViewModel.selectNotice(null) },
                        onDismissDigitalIdModal = { dashboardViewModel.toggleDigitalIdModal(false) },
                        onDismissQrModal = { dashboardViewModel.toggleQrScannerModal(false) },
                        onDismissQuickActionModal = { dashboardViewModel.showQuickActionDialog(null) },
                        onScanQrCode = { code -> dashboardViewModel.scanQrCode(code) }
                    )

                    StudentNavTab.COURSES -> StudentCoursesScreen(viewModel = coursesViewModel)

                    StudentNavTab.NOTIFICATIONS -> StudentNotificationsScreen(viewModel = notificationsViewModel)

                    StudentNavTab.LIBRARY -> StudentLibraryScreen(viewModel = libraryViewModel)

                    StudentNavTab.PROFILE -> StudentProfileScreen(
                        data = uiState.data,
                        viewModel = profileViewModel,
                        onLogoutClick = onLogout
                    )
                }
            }
        }

        // Search Bottom Sheet
        if (uiState.isSearchOpen) {
            GlobalSearchSheet(
                data = uiState.data,
                searchQuery = uiState.searchQuery,
                onQueryChange = { dashboardViewModel.onSearchQueryChange(it) },
                onDismiss = { dashboardViewModel.toggleSearchSheet(false) }
            )
        }
    }
}
