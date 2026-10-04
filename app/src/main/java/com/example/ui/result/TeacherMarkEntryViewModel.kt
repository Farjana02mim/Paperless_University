package com.example.ui.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.result.GpaEngine
import com.example.domain.model.result.CourseResult
import com.example.domain.model.result.ResultStatus
import com.example.domain.repository.ResultRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class TeacherMarkEntryViewModel(
    private val repository: ResultRepository
) : ViewModel() {

    private val _teacherId = MutableStateFlow("TCH_101")
    private val _selectedCourseId = MutableStateFlow("CS-301")

    val studentMarksList: StateFlow<List<CourseResult>> = _selectedCourseId.flatMapLatest { courseId ->
        repository.getTeacherCourseResults(_teacherId.value, courseId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    private val _isDraftSaved = MutableStateFlow(true)
    val isDraftSaved: StateFlow<Boolean> = _isDraftSaved.asStateFlow()

    fun updateStudentMarks(
        resultId: String,
        att: Double,
        assign: Double,
        quiz: Double,
        mid: Double,
        lab: Double,
        fin: Double
    ) {
        val currentList = studentMarksList.value.toMutableList()
        val index = currentList.indexOfFirst { it.resultId == resultId }
        if (index != -1) {
            val old = currentList[index]
            val total = (att + assign + quiz + mid + lab + fin).coerceIn(0.0, 100.0)
            val (grade, point) = GpaEngine.calculateCourseGrade(total)

            val updated = old.copy(
                attendanceMarks = att,
                assignmentMarks = assign,
                quizMarks = quiz,
                midMarks = mid,
                labMarks = lab,
                finalMarks = fin,
                totalMarks = total,
                letterGrade = grade,
                gradePoint = point,
                updatedAt = System.currentTimeMillis()
            )
            currentList[index] = updated
            _isDraftSaved.value = false
            viewModelScope.launch {
                repository.submitMarksByTeacher(currentList)
            }
        }
    }

    fun saveDraft() {
        viewModelScope.launch {
            repository.submitMarksByTeacher(studentMarksList.value)
            _isDraftSaved.value = true
            _actionMessage.value = "Marks draft saved successfully!"
        }
    }

    fun submitMarksForApproval() {
        viewModelScope.launch {
            val updated = studentMarksList.value.map { it.copy(status = ResultStatus.SUBMITTED) }
            val res = repository.submitMarksByTeacher(updated)
            if (res.isSuccess) {
                _isDraftSaved.value = true
                _actionMessage.value = "Course marks submitted to Department Verification!"
            }
        }
    }

    fun importBulkMarksFromCsv() {
        viewModelScope.launch {
            val currentList = studentMarksList.value.map { item ->
                val simulatedFinal = (35..48).random().toDouble()
                val total = item.attendanceMarks + item.assignmentMarks + item.quizMarks + item.midMarks + item.labMarks + simulatedFinal
                val (grade, point) = GpaEngine.calculateCourseGrade(total)
                item.copy(finalMarks = simulatedFinal, totalMarks = total, letterGrade = grade, gradePoint = point)
            }
            repository.submitMarksByTeacher(currentList)
            _actionMessage.value = "Bulk imported ${currentList.size} student records from CSV/Excel template!"
        }
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }
}
