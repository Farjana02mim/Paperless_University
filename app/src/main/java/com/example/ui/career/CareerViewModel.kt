package com.example.ui.career

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.career.CareerRepository
import com.example.domain.model.career.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CareerViewModel(
    private val careerRepository: CareerRepository
) : ViewModel() {

    val jobPostings: StateFlow<List<JobPosting>> = careerRepository.getJobPostings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val internshipPostings: StateFlow<List<InternshipPosting>> = careerRepository.getInternshipPostings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val applications: StateFlow<List<JobApplication>> = careerRepository.getApplications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val resumeProfile: StateFlow<ResumeProfile> = careerRepository.getResumeProfile("2024-3-60-042")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ResumeProfile())

    val alumniDirectory: StateFlow<List<AlumniProfile>> = careerRepository.getAlumniDirectory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mentorshipSessions: StateFlow<List<MentorshipSession>> = careerRepository.getMentorshipSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val placementStats: StateFlow<PlacementStatistics> = careerRepository.getPlacementStatistics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlacementStatistics())

    fun applyForJob(jobId: String, jobTitle: String, companyName: String) {
        viewModelScope.launch {
            careerRepository.applyForJob(jobId, jobTitle, companyName)
        }
    }

    fun toggleBookmark(jobId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            careerRepository.toggleBookmark(jobId, !currentStatus)
        }
    }

    fun updateResume(updatedResume: ResumeProfile) {
        viewModelScope.launch {
            careerRepository.updateResumeProfile(updatedResume)
        }
    }

    fun requestMentorship(mentorName: String, topic: String) {
        viewModelScope.launch {
            careerRepository.requestMentorship(mentorName, topic)
        }
    }
}
