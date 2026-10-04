package com.example.ui.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.teacher.*
import com.example.domain.repository.teacher.TeacherRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

enum class TeacherModalType {
    NONE,
    QR_ATTENDANCE,
    MANUAL_ATTENDANCE,
    CREATE_ASSIGNMENT,
    ENTER_MARKS,
    POST_NOTICE,
    UPLOAD_RESOURCE,
    RESCHEDULE_CLASS,
    SEND_MESSAGE,
    ANALYTICS,
    CALENDAR
}

data class TeacherUiState(
    val selectedTab: Int = 0,
    val searchQuery: String = "",
    val departmentFilter: String = "All",
    val semesterFilter: String = "All",
    val selectedCourseId: String? = null,
    val selectedStudent: TeacherStudentItem? = null,
    val selectedClassItem: ClassScheduleItem? = null,
    val activeModal: TeacherModalType = TeacherModalType.NONE,
    val userMessage: String? = null,
    val isLoading: Boolean = false
)

class TeacherPortalViewModel(
    private val repository: TeacherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeacherUiState())
    val uiState: StateFlow<TeacherUiState> = _uiState.asStateFlow()

    val profile: StateFlow<TeacherProfile> = repository.getTeacherProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TeacherProfile())

    val courses: StateFlow<List<TeacherCourse>> = repository.getAssignedCourses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayClasses: StateFlow<List<ClassScheduleItem>> = repository.getTodayClasses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<TeacherTask>> = repository.getTeacherTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<TeacherNotification>> = repository.getTeacherNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val analytics: StateFlow<TeachingAnalytics> = repository.getTeachingAnalytics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TeachingAnalytics())

    val notices: StateFlow<List<TeacherNotice>> = repository.getTeacherNotices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val messages: StateFlow<List<TeacherMessage>> = repository.getTeacherMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val calendarEvents: StateFlow<List<AcademicCalendarEvent>> = repository.getAcademicCalendarEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val resources: StateFlow<List<CourseResource>> = _uiState
        .flatMapLatest { state -> repository.getCourseResources(state.selectedCourseId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val students: StateFlow<List<TeacherStudentItem>> = _uiState
        .flatMapLatest { state ->
            repository.getEnrolledStudents(
                courseId = state.selectedCourseId,
                searchQuery = state.searchQuery,
                deptFilter = state.departmentFilter,
                semFilter = state.semesterFilter
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun updateFilters(dept: String, sem: String) {
        _uiState.update { it.copy(departmentFilter = dept, semesterFilter = sem) }
    }

    fun selectCourse(courseId: String?) {
        _uiState.update { it.copy(selectedCourseId = courseId) }
    }

    fun selectStudent(student: TeacherStudentItem?) {
        _uiState.update { it.copy(selectedStudent = student) }
    }

    fun openModal(modalType: TeacherModalType, classItem: ClassScheduleItem? = null) {
        _uiState.update { it.copy(activeModal = modalType, selectedClassItem = classItem) }
    }

    fun closeModal() {
        _uiState.update { it.copy(activeModal = TeacherModalType.NONE, selectedClassItem = null) }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    fun toggleTask(taskId: String) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(taskId)
        }
    }

    fun addNewTask(title: String, courseName: String, deadline: String, type: TaskType, priority: String) {
        viewModelScope.launch {
            val task = TeacherTask(
                taskId = "tsk_${UUID.randomUUID().toString().take(6)}",
                title = title,
                description = "Added by teacher",
                courseName = courseName,
                deadline = deadline,
                type = type,
                isCompleted = false,
                priority = priority
            )
            repository.addTask(task)
            _uiState.update { it.copy(userMessage = "Task created successfully!") }
        }
    }

    fun rescheduleClass(classId: String, newTime: String, newRoom: String) {
        viewModelScope.launch {
            repository.rescheduleClass(classId, newTime, newRoom)
            closeModal()
            _uiState.update { it.copy(userMessage = "Class rescheduled to $newTime in $newRoom") }
        }
    }

    fun publishNotice(title: String, content: String, dept: String, course: String, isPinned: Boolean) {
        viewModelScope.launch {
            val notice = TeacherNotice(
                noticeId = "ntc_${UUID.randomUUID().toString().take(6)}",
                title = title,
                content = content,
                targetDepartment = dept,
                targetCourse = course,
                isPinned = isPinned,
                publishedAt = "Just Now",
                authorName = profile.value.fullName
            )
            repository.publishNotice(notice)
            closeModal()
            _uiState.update { it.copy(userMessage = "Notice published to $dept!") }
        }
    }

    fun uploadResource(courseId: String, courseCode: String, title: String, type: ResourceType, fileUrl: String) {
        viewModelScope.launch {
            val res = CourseResource(
                resourceId = "res_${UUID.randomUUID().toString().take(6)}",
                courseId = courseId,
                courseCode = courseCode,
                title = title,
                resourceType = type,
                fileUrl = if (fileUrl.isBlank()) "https://www.w3.org/W3C/DesignIssues/PDF.pdf" else fileUrl,
                uploadDate = "Today",
                sizeKb = (1500..4500).random().toLong(),
                downloadsCount = 0
            )
            repository.uploadCourseResource(res)
            closeModal()
            _uiState.update { it.copy(userMessage = "Course resource uploaded successfully!") }
        }
    }

    fun sendMessage(receiverName: String, content: String) {
        viewModelScope.launch {
            val msg = TeacherMessage(
                messageId = "msg_${UUID.randomUUID().toString().take(6)}",
                senderId = profile.value.teacherId,
                senderName = profile.value.fullName,
                receiverId = "STU-1001",
                receiverName = receiverName,
                content = content,
                timestamp = "Just Now",
                isRead = true
            )
            repository.sendMessage(msg)
            closeModal()
            _uiState.update { it.copy(userMessage = "Message sent to $receiverName") }
        }
    }

    fun completeAttendanceSession(classId: String) {
        viewModelScope.launch {
            repository.updateAttendanceStatus(classId, true)
            closeModal()
            _uiState.update { it.copy(userMessage = "Attendance session marked complete!") }
        }
    }
}
