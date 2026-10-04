package com.example.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.admin.*
import com.example.domain.repository.admin.AdminRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

enum class AdminModalType {
    NONE,
    CREATE_USER,
    CREATE_DEPARTMENT,
    CREATE_COURSE,
    ADMISSION_ACTION,
    GENERATE_REPORT,
    CLOUD_BACKUP,
    SYSTEM_SETTINGS,
    SECURITY_MFA,
    AI_INSIGHTS_VIEW
}

data class AdminUiState(
    val selectedTab: Int = 0,
    val searchQuery: String = "",
    val roleFilter: String = "All",
    val deptFilter: String = "All",
    val selectedUser: AdminUserItem? = null,
    val selectedApplication: AdmissionApplication? = null,
    val activeModal: AdminModalType = AdminModalType.NONE,
    val userMessage: String? = null,
    val isLoading: Boolean = false
)

class AdminPortalViewModel(
    private val repository: AdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    val profile: StateFlow<AdminProfile> = repository.getAdminProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminProfile())

    val systemMetrics: StateFlow<AdminSystemMetrics> = repository.getSystemMetrics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminSystemMetrics())

    val users: StateFlow<List<AdminUserItem>> = _uiState
        .flatMapLatest { state ->
            repository.getUsers(state.searchQuery, state.roleFilter, state.deptFilter)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val roles: StateFlow<List<RolePermission>> = repository.getRoles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val departments: StateFlow<List<AdminDepartment>> = repository.getDepartments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val courses: StateFlow<List<AdminCourse>> = repository.getCourses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val applications: StateFlow<List<AdmissionApplication>> = repository.getAdmissionApplications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val invoices: StateFlow<List<AdminFinanceInvoice>> = repository.getFinanceInvoices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<AdminAuditLog>> = repository.getAuditLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<SystemSettings> = repository.getSystemSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SystemSettings())

    val aiInsights: StateFlow<List<AiInsightsArchitecture>> = repository.getAiInsights()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reports: StateFlow<List<AdminReportItem>> = repository.getGeneratedReports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun updateFilters(role: String, dept: String) {
        _uiState.update { it.copy(roleFilter = role, deptFilter = dept) }
    }

    fun selectUser(user: AdminUserItem?) {
        _uiState.update { it.copy(selectedUser = user) }
    }

    fun selectApplication(app: AdmissionApplication?) {
        _uiState.update { it.copy(selectedApplication = app) }
    }

    fun openModal(modalType: AdminModalType) {
        _uiState.update { it.copy(activeModal = modalType) }
    }

    fun closeModal() {
        _uiState.update { it.copy(activeModal = AdminModalType.NONE) }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    fun createNewUser(name: String, email: String, role: UserRole, department: String, phone: String) {
        viewModelScope.launch {
            val user = AdminUserItem(
                userId = "USR-${(1000..9999).random()}",
                name = name,
                email = email,
                role = role,
                department = department,
                status = UserStatus.ACTIVE,
                permissions = listOf("DEFAULT_ROLE_PERM"),
                phone = phone
            )
            repository.createUser(user)
            closeModal()
            _uiState.update { it.copy(userMessage = "Created user ${user.name} (${user.userId})") }
        }
    }

    fun toggleUserStatus(userId: String, currentStatus: UserStatus) {
        viewModelScope.launch {
            val newStatus = if (currentStatus == UserStatus.ACTIVE) UserStatus.DISABLED else UserStatus.ACTIVE
            repository.updateUserStatus(userId, newStatus)
            _uiState.update { it.copy(userMessage = "User status updated to $newStatus") }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            repository.deleteUser(userId)
            _uiState.update { it.copy(userMessage = "User $userId deleted from university records.") }
        }
    }

    fun createNewDepartment(code: String, name: String, hod: String, budget: Double) {
        viewModelScope.launch {
            val dept = AdminDepartment(
                deptId = "DPT-${(100..999).random()}",
                code = code,
                name = name,
                headOfDepartment = hod,
                totalFaculty = 1,
                totalStudents = 0,
                totalCourses = 0,
                budget = budget
            )
            repository.createDepartment(dept)
            closeModal()
            _uiState.update { it.copy(userMessage = "Department $name ($code) created!") }
        }
    }

    fun createNewCourse(code: String, name: String, dept: String, credits: Int, teacher: String, prereq: String) {
        viewModelScope.launch {
            val course = AdminCourse(
                courseId = "CRS-${(100..999).random()}",
                code = code,
                name = name,
                department = dept,
                credits = credits,
                assignedTeacherName = teacher,
                totalEnrolledStudents = 0,
                prerequisites = if (prereq.isBlank()) "None" else prereq
            )
            repository.createCourse(course)
            closeModal()
            _uiState.update { it.copy(userMessage = "Course $code added to $dept!") }
        }
    }

    fun updateAdmissionApplication(appId: String, status: String, seat: String) {
        viewModelScope.launch {
            repository.updateAdmissionStatus(appId, status, seat)
            closeModal()
            _uiState.update { it.copy(userMessage = "Application $appId set to $status") }
        }
    }

    fun updateInvoiceStatus(invoiceId: String, status: String) {
        viewModelScope.launch {
            repository.updateInvoiceStatus(invoiceId, status)
            _uiState.update { it.copy(userMessage = "Invoice $invoiceId marked as $status") }
        }
    }

    fun saveSystemSettings(universityName: String, semester: String, attendanceMin: Int, mfa: Boolean) {
        viewModelScope.launch {
            val current = settings.value.copy(
                universityName = universityName,
                currentSemester = semester,
                minAttendancePercentage = attendanceMin,
                mfaRequired = mfa
            )
            repository.updateSystemSettings(current)
            closeModal()
            _uiState.update { it.copy(userMessage = "System settings updated successfully!") }
        }
    }

    fun triggerBackup() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val message = repository.triggerCloudBackup()
            closeModal()
            _uiState.update { it.copy(isLoading = false, userMessage = message) }
        }
    }

    fun generateNewReport(title: String, category: String, format: String) {
        viewModelScope.launch {
            val report = repository.generateReport(title, category, format)
            closeModal()
            _uiState.update { it.copy(userMessage = "Report '${report.title}' (${report.format}) generated!") }
        }
    }
}
