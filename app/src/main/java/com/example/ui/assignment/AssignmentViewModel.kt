package com.example.ui.assignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.upload.FileUploadEngine
import com.example.core.upload.UploadState
import com.example.domain.model.*
import com.example.domain.repository.AssignmentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AssignmentViewModel(
    private val repository: AssignmentRepository
) : ViewModel() {

    private val _department = MutableStateFlow("Computer Science")
    private val _semester = MutableStateFlow("Semester 6")
    private val _studentId = MutableStateFlow("STD_2026_091")

    val assignments: StateFlow<List<Assignment>> = repository
        .getAssignmentsForStudent(_department.value, _semester.value)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studentSubmissions: StateFlow<List<AssignmentSubmission>> = repository
        .getSubmissionsByStudent(_studentId.value)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dashboardSummary: StateFlow<AssignmentDashboardSummary> = repository
        .getDashboardSummary(_studentId.value, _department.value, _semester.value)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AssignmentDashboardSummary())

    private val _selectedFilterTab = MutableStateFlow("All") // All, Pending, Submitted, Late, Evaluated
    val selectedFilterTab: StateFlow<String> = _selectedFilterTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState.asStateFlow()

    private val _submissionStatusMessage = MutableStateFlow<String?>(null)
    val submissionStatusMessage: StateFlow<String?> = _submissionStatusMessage.asStateFlow()

    val filteredAssignments: StateFlow<List<Assignment>> = combine(
        assignments,
        studentSubmissions,
        selectedFilterTab,
        searchQuery
    ) { list, subs, filter, query ->
        val subMap = subs.associateBy { it.assignmentId }
        var result = list

        if (query.isNotBlank()) {
            result = result.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.courseName.contains(query, ignoreCase = true) ||
                it.subject.contains(query, ignoreCase = true)
            }
        }

        when (filter) {
            "Pending" -> result.filter { subMap[it.assignmentId] == null }
            "Submitted" -> result.filter { subMap[it.assignmentId]?.status == SubmissionStatus.SUBMITTED }
            "Late" -> result.filter { subMap[it.assignmentId]?.isLate == true }
            "Evaluated" -> result.filter { subMap[it.assignmentId]?.status == SubmissionStatus.EVALUATED }
            else -> result
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilterTab(tab: String) {
        _selectedFilterTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun submitAssignment(
        assignment: Assignment,
        attachedFiles: List<AssignmentAttachment>
    ) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Progress(10, 100, 1000)
            val now = System.currentTimeMillis()
            val isLate = now > assignment.deadline

            val submission = AssignmentSubmission(
                assignmentId = assignment.assignmentId,
                studentId = _studentId.value,
                studentName = "Alex Rivera",
                rollNumber = "CS-2026-042",
                submittedFiles = attachedFiles,
                submittedAt = now,
                status = SubmissionStatus.SUBMITTED,
                isLate = isLate
            )

            val result = repository.submitAssignment(submission)
            if (result.isSuccess) {
                _uploadState.value = UploadState.Success(attachedFiles.firstOrNull() ?: AssignmentAttachment())
                _submissionStatusMessage.value = "Assignment submitted successfully!"
            } else {
                _uploadState.value = UploadState.Error(result.exceptionOrNull()?.message ?: "Submission failed")
            }
        }
    }

    fun clearSubmissionMessage() {
        _submissionStatusMessage.value = null
    }
}
