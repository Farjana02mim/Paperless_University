package com.example.ui.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.result.GpaEngine
import com.example.domain.model.result.*
import com.example.domain.repository.ResultRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StudentResultViewModel(
    private val repository: ResultRepository
) : ViewModel() {

    private val _studentId = MutableStateFlow("STD_2026_091")

    val courseResults: StateFlow<List<CourseResult>> = repository
        .getCourseResultsForStudent(_studentId.value)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val semesterResults: StateFlow<List<SemesterResult>> = repository
        .getSemesterResultsForStudent(_studentId.value)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedTab = MutableStateFlow(0) // 0: Current Semester, 1: History, 2: Course Wise, 3: Transcript, 4: Analytics
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _selectedSemesterFilter = MutableStateFlow("Semester 6")
    val selectedSemesterFilter: StateFlow<String> = _selectedSemesterFilter.asStateFlow()

    private val _transcriptState = MutableStateFlow<TranscriptModel?>(null)
    val transcriptState: StateFlow<TranscriptModel?> = _transcriptState.asStateFlow()

    val overallCGPA: StateFlow<Double> = semesterResults.map { list ->
        GpaEngine.calculateOverallCGPA(list)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val completedCredits: StateFlow<Double> = courseResults.map { list ->
        GpaEngine.calculateEarnedCredits(list)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val currentAcademicStanding: StateFlow<AcademicStanding> = overallCGPA.map { cgpa ->
        GpaEngine.determineAcademicStanding(cgpa)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AcademicStanding.SATISFACTORY)

    val filteredCourseResults: StateFlow<List<CourseResult>> = combine(
        courseResults,
        selectedSemesterFilter
    ) { results, filter ->
        if (filter == "All") results else results.filter { it.semester.equals(filter, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadTranscript()
    }

    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun setSemesterFilter(semester: String) {
        _selectedSemesterFilter.value = semester
    }

    fun loadTranscript() {
        viewModelScope.launch {
            val res = repository.getTranscriptForStudent(_studentId.value)
            if (res.isSuccess) {
                _transcriptState.value = res.getOrNull()
            }
        }
    }
}
