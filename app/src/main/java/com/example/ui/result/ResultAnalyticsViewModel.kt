package com.example.ui.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.result.UniversityResultAnalytics
import com.example.domain.repository.ResultRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResultAnalyticsViewModel(
    private val repository: ResultRepository
) : ViewModel() {

    private val _analyticsData = MutableStateFlow<UniversityResultAnalytics?>(null)
    val analyticsData: StateFlow<UniversityResultAnalytics?> = _analyticsData.asStateFlow()

    init {
        loadAnalytics("Computer Science")
    }

    fun loadAnalytics(dept: String) {
        viewModelScope.launch {
            val res = repository.getUniversityAnalytics(dept)
            if (res.isSuccess) {
                _analyticsData.value = res.getOrNull()
            }
        }
    }
}
