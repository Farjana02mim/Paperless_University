package com.example.data.local.entity.career

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "career_jobs")
data class JobPostingEntity(
    @PrimaryKey val jobId: String,
    val companyName: String,
    val companyLogoUrl: String,
    val title: String,
    val department: String,
    val requiredSkillsCsv: String,
    val minimumCgpa: Double,
    val salaryRange: String,
    val location: String,
    val employmentType: String,
    val vacancies: Int,
    val deadline: String,
    val isBookmarked: Boolean,
    val isApplied: Boolean
)

@Entity(tableName = "career_applications")
data class JobApplicationEntity(
    @PrimaryKey val applicationId: String,
    val jobId: String,
    val jobTitle: String,
    val companyName: String,
    val submittedAt: String,
    val status: String,
    val interviewTime: String,
    val interviewLink: String,
    val remarks: String
)

@Entity(tableName = "career_resume")
data class ResumeProfileEntity(
    @PrimaryKey val studentId: String,
    val studentName: String,
    val department: String,
    val cgpa: Double,
    val headline: String,
    val summary: String,
    val skillsCsv: String,
    val projectsCsv: String,
    val resumeScore: Int
)
