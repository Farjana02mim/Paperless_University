package com.example.ui.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.result.ResultStatus
import com.example.domain.repository.ResultRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminResultPublishViewModel(
    private val repository: ResultRepository
) : ViewModel() {

    private val _selectedDepartment = MutableStateFlow("Computer Science")
    val selectedDepartment: StateFlow<String> = _selectedDepartment.asStateFlow()

    private val _selectedSemester = MutableStateFlow("Semester 6")
    val selectedSemester: StateFlow<String> = _selectedSemester.asStateFlow()

    private val _approvalStatus = MutableStateFlow(ResultStatus.SUBMITTED)
    val approvalStatus: StateFlow<ResultStatus> = _approvalStatus.asStateFlow()

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    fun approveDepartmentResults() {
        viewModelScope.launch {
            val res = repository.approveDepartmentResults(_selectedDepartment.value, _selectedSemester.value)
            if (res.isSuccess) {
                _approvalStatus.value = ResultStatus.APPROVED_BY_ADMIN
                _actionMessage.value = "Results approved for ${_selectedDepartment.value} - ${_selectedSemester.value}"
            }
        }
    }

    fun publishSemesterResultsToStudents() {
        viewModelScope.launch {
            val res = repository.publishSemesterResults(_selectedDepartment.value, _selectedSemester.value)
            if (res.isSuccess) {
                _approvalStatus.value = ResultStatus.PUBLISHED
                _actionMessage.value = "Results published! Student notifications dispatched via Push Engine."
            }
        }
    }

    fun lockPublishedResults() {
        viewModelScope.launch {
            _approvalStatus.value = ResultStatus.LOCKED
            _actionMessage.value = "Result set LOCKED against tampering or edits."
        }
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }
}
