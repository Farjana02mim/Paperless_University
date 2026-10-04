package com.example.ui.assignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.*
import com.example.domain.repository.AssignmentRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class TeacherAssignmentViewModel(
    private val repository: AssignmentRepository
) : ViewModel() {

    private val _teacherId = MutableStateFlow("TCH_2026_101")

    val teacherAssignments: StateFlow<List<Assignment>> = repository
        .getAssignmentsForTeacher(_teacherId.value)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedAssignmentId = MutableStateFlow<String?>(null)
    val selectedAssignmentId: StateFlow<String?> = _selectedAssignmentId.asStateFlow()

    val currentSubmissions: StateFlow<List<AssignmentSubmission>> = selectedAssignmentId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.getSubmissionsForAssignment(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    fun selectAssignment(id: String) {
        _selectedAssignmentId.value = id
    }

    fun createAssignment(
        title: String,
        description: String,
        instructions: String,
        subject: String,
        courseId: String,
        courseName: String,
        department: String,
        semester: String,
        maximumMarks: Double,
        passingMarks: Double,
        deadlineDays: Int,
        allowLate: Boolean,
        rubricCriteria: List<RubricCriterion>
    ) {
        viewModelScope.launch {
            val deadlineTime = System.currentTimeMillis() + (deadlineDays * 86400000L)
            val assignment = Assignment(
                courseId = courseId,
                courseName = courseName,
                teacherId = _teacherId.value,
                teacherName = "Prof. Robert Vance",
                title = title,
                description = description,
                instructions = instructions,
                subject = subject,
                department = department,
                semester = semester,
                deadline = deadlineTime,
                allowLateSubmission = allowLate,
                maximumMarks = maximumMarks,
                passingMarks = passingMarks,
                rubric = AssignmentRubric(title = "Grading Rubric", criteria = rubricCriteria)
            )

            val result = repository.createAssignment(assignment)
            if (result.isSuccess) {
                _actionMessage.value = "Assignment created and published to students!"
            } else {
                _actionMessage.value = "Failed to create assignment: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun evaluateSubmission(
        submissionId: String,
        obtainedMarks: Double,
        feedback: String,
        remarks: String,
        rubricGrades: List<RubricCriterion>
    ) {
        viewModelScope.launch {
            val result = repository.evaluateSubmission(
                submissionId = submissionId,
                obtainedMarks = obtainedMarks,
                teacherFeedback = feedback,
                teacherRemarks = remarks,
                rubricGrades = rubricGrades,
                annotatedFiles = emptyList()
            )
            if (result.isSuccess) {
                _actionMessage.value = "Grade and feedback saved successfully!"
            } else {
                _actionMessage.value = "Error saving grade: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }
}
