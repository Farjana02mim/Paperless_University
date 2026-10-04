package com.example.data.repository.career

import com.example.domain.model.career.*
import kotlinx.coroutines.flow.Flow

interface CareerRepository {
    fun getJobPostings(): Flow<List<JobPosting>>
    fun getInternshipPostings(): Flow<List<InternshipPosting>>
    fun getApplications(): Flow<List<JobApplication>>
    fun getResumeProfile(studentId: String): Flow<ResumeProfile>
    fun getAlumniDirectory(): Flow<List<AlumniProfile>>
    fun getMentorshipSessions(): Flow<List<MentorshipSession>>
    fun getPlacementStatistics(): Flow<PlacementStatistics>

    suspend fun applyForJob(jobId: String, jobTitle: String, companyName: String)
    suspend fun toggleBookmark(jobId: String, isBookmarked: Boolean)
    suspend fun updateResumeProfile(resume: ResumeProfile)
    suspend fun requestMentorship(mentorName: String, topic: String)
}
