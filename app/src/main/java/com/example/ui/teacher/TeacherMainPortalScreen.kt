package com.example.ui.teacher

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.teacher.ClassScheduleItem
import com.example.ui.teacher.components.*
import com.example.ui.teacher.screens.*
import com.example.ui.theme.SecondaryTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherMainPortalScreen(
    viewModel: TeacherPortalViewModel,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val profile by viewModel.profile.collectAsState()
    val courses by viewModel.courses.collectAsState()
    val todayClasses by viewModel.todayClasses.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val analytics by viewModel.analytics.collectAsState()
    val resources by viewModel.resources.collectAsState()
    val students by viewModel.students.collectAsState()
    val notices by viewModel.notices.collectAsState()
    val calendarEvents by viewModel.calendarEvents.collectAsState()

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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.School, contentDescription = null, tint = SecondaryTeal)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            when (uiState.selectedTab) {
                                0 -> "Faculty Smart Portal"
                                1 -> "Course Management"
                                2 -> "Student Directory"
                                3 -> "Faculty Task Manager"
                                4 -> "Faculty Profile"
                                else -> "Teacher Portal"
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    BadgedBox(
                        badge = {
                            if (notifications.any { !it.isRead }) {
                                Badge { Text("${notifications.count { !it.isRead }}") }
                            }
                        }
                    ) {
                        IconButton(onClick = { viewModel.openModal(TeacherModalType.POST_NOTICE) }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                        }
                    }

                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier.testTag("teacher_logout_button")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.error
                        )
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
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    icon = { Icon(Icons.Default.Book, contentDescription = "Courses") },
                    label = { Text("Courses") }
                )
                NavigationBarItem(
                    selected = uiState.selectedTab == 2,
                    onClick = { viewModel.selectTab(2) },
                    icon = { Icon(Icons.Default.People, contentDescription = "Students") },
                    label = { Text("Students") }
                )
                NavigationBarItem(
                    selected = uiState.selectedTab == 3,
                    onClick = { viewModel.selectTab(3) },
                    icon = { Icon(Icons.Default.Task, contentDescription = "Tasks") },
                    label = { Text("Tasks") }
                )
                NavigationBarItem(
                    selected = uiState.selectedTab == 4,
                    onClick = { viewModel.selectTab(4) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.testTag("teacher_main_portal_screen")
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (uiState.selectedTab) {
                0 -> TeacherHomeScreen(
                    profile = profile,
                    todayClasses = todayClasses,
                    tasks = tasks,
                    analytics = analytics,
                    notifications = notifications,
                    onOpenModal = { modal, item -> viewModel.openModal(modal, item) },
                    onToggleTask = { viewModel.toggleTask(it) }
                )
                1 -> TeacherCoursesScreen(
                    courses = courses,
                    resources = resources,
                    students = students,
                    selectedCourseId = uiState.selectedCourseId,
                    onSelectCourse = { viewModel.selectCourse(it) },
                    onOpenModal = { modal, item -> viewModel.openModal(modal, item) }
                )
                2 -> TeacherStudentsScreen(
                    students = students,
                    searchQuery = uiState.searchQuery,
                    departmentFilter = uiState.departmentFilter,
                    semesterFilter = uiState.semesterFilter,
                    selectedStudent = uiState.selectedStudent,
                    onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                    onFilterChange = { dept, sem -> viewModel.updateFilters(dept, sem) },
                    onSelectStudent = { viewModel.selectStudent(it) }
                )
                3 -> TeacherTasksScreen(
                    tasks = tasks,
                    onToggleTask = { viewModel.toggleTask(it) },
                    onAddTask = { title, course, deadline, type, priority ->
                        viewModel.addNewTask(title, course, deadline, type, priority)
                    }
                )
                4 -> TeacherProfileScreen(
                    profile = profile,
                    onLogout = onLogout
                )
            }
        }
    }

    // Modal / Dialog Router
    when (uiState.activeModal) {
        TeacherModalType.QR_ATTENDANCE -> QrAttendanceGeneratorDialog(
            classItem = uiState.selectedClassItem,
            onDismiss = { viewModel.closeModal() },
            onComplete = { classId -> viewModel.completeAttendanceSession(classId) }
        )
        TeacherModalType.MANUAL_ATTENDANCE -> ManualAttendanceDialog(
            classItem = uiState.selectedClassItem,
            students = students,
            onDismiss = { viewModel.closeModal() },
            onComplete = { classId -> viewModel.completeAttendanceSession(classId) }
        )
        TeacherModalType.POST_NOTICE -> PostNoticeDialog(
            onDismiss = { viewModel.closeModal() },
            onSubmit = { title, content, dept, course, isPinned ->
                viewModel.publishNotice(title, content, dept, course, isPinned)
            }
        )
        TeacherModalType.UPLOAD_RESOURCE -> UploadResourceDialog(
            courses = courses,
            onDismiss = { viewModel.closeModal() },
            onSubmit = { courseId, courseCode, title, type, url ->
                viewModel.uploadResource(courseId, courseCode, title, type, url)
            }
        )
        TeacherModalType.RESCHEDULE_CLASS -> RescheduleClassDialog(
            classItem = uiState.selectedClassItem,
            onDismiss = { viewModel.closeModal() },
            onSubmit = { classId, time, room ->
                viewModel.rescheduleClass(classId, time, room)
            }
        )
        TeacherModalType.ENTER_MARKS -> EnterMarksDialog(
            students = students,
            onDismiss = { viewModel.closeModal() },
            onSubmit = {
                viewModel.closeModal()
                viewModel.clearUserMessage()
            }
        )
        TeacherModalType.ANALYTICS -> AnalyticsDashboardDialog(
            analytics = analytics,
            onDismiss = { viewModel.closeModal() }
        )
        TeacherModalType.CALENDAR -> AcademicCalendarDialog(
            events = calendarEvents,
            onDismiss = { viewModel.closeModal() }
        )
        else -> {}
    }
}
