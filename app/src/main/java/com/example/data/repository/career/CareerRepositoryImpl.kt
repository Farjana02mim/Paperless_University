package com.example.data.repository.career

import com.example.data.local.dao.career.CareerDao
import com.example.data.local.entity.career.JobApplicationEntity
import com.example.data.local.entity.career.JobPostingEntity
import com.example.data.local.entity.career.ResumeProfileEntity
import com.example.domain.model.career.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CareerRepositoryImpl(
    private val careerDao: CareerDao,
    private val firestore: FirebaseFirestore? = null
) : CareerRepository {

    override fun getJobPostings(): Flow<List<JobPosting>> {
        return careerDao.getAllJobs().map { entities ->
            if (entities.isEmpty()) {
                val mockJobs = getInitialMockJobs()
                careerDao.insertJobs(mockJobs.map { it.toEntity() })
                mockJobs
            } else {
                entities.map { it.toDomain() }
            }
        }
    }

    override fun getInternshipPostings(): Flow<List<InternshipPosting>> {
        return flow {
            emit(
                listOf(
                    InternshipPosting(
                        internshipId = "INT-101",
                        companyName = "Brain Station 23",
                        title = "Android Development Intern",
                        durationMonths = 6,
                        stipendMonthly = 25000.0,
                        location = "Dhaka (Hybrid)",
                        requiredSkills = listOf("Kotlin", "Jetpack Compose", "Git"),
                        deadline = "28 Aug 2026",
                        isApplied = false
                    ),
                    InternshipPosting(
                        internshipId = "INT-102",
                        companyName = "Therap Services",
                        title = "SQA & Automation Intern",
                        durationMonths = 3,
                        stipendMonthly = 20000.0,
                        location = "Baniashanta / Onsite",
                        requiredSkills = listOf("Java", "Selenium", "JUnit"),
                        deadline = "30 Aug 2026",
                        isApplied = true
                    ),
                    InternshipPosting(
                        internshipId = "INT-103",
                        companyName = "TigerIT Bangladesh",
                        title = "Computer Vision & ML Intern",
                        durationMonths = 6,
                        stipendMonthly = 30000.0,
                        location = "Mohakhali DOHS, Dhaka",
                        requiredSkills = listOf("Python", "OpenCV", "PyTorch"),
                        deadline = "05 Sep 2026",
                        isApplied = false
                    )
                )
            )
        }
    }

    override fun getApplications(): Flow<List<JobApplication>> {
        return careerDao.getAllApplications().map { entities ->
            if (entities.isEmpty()) {
                val initialApps = listOf(
                    JobApplication(
                        applicationId = "APP-991",
                        jobId = "JOB-101",
                        jobTitle = "Junior Machine Learning Engineer",
                        companyName = "Google Brain / DeepMind BD",
                        submittedAt = "02 Aug 2026",
                        status = ApplicationStatus.INTERVIEW_SCHEDULED,
                        interviewTime = "12 Aug 2026, 11:00 AM",
                        interviewLink = "https://meet.google.com/xyz-career-dev",
                        remarks = "Selected for Technical Live Coding Round."
                    ),
                    JobApplication(
                        applicationId = "APP-992",
                        jobId = "JOB-102",
                        jobTitle = "Android Mobile Engineer",
                        companyName = "Pathao Ltd",
                        submittedAt = "28 Jul 2026",
                        status = ApplicationStatus.SHORTLISTED,
                        interviewTime = "TBD",
                        interviewLink = "",
                        remarks = "CV Shortlisted by Lead Engineer."
                    )
                )
                initialApps.forEach { app -> careerDao.insertApplication(app.toEntity()) }
                initialApps
            } else {
                entities.map { it.toDomain() }
            }
        }
    }

    override fun getResumeProfile(studentId: String): Flow<ResumeProfile> {
        return careerDao.getResumeProfile(studentId).map { entity ->
            entity?.toDomain() ?: ResumeProfile(studentId = studentId)
        }
    }

    override fun getAlumniDirectory(): Flow<List<AlumniProfile>> {
        return flow {
            emit(
                listOf(
                    AlumniProfile(
                        alumniId = "ALM-01",
                        name = "Mahmudul Hasan",
                        graduationYear = "2021",
                        department = "CSE",
                        company = "Meta AI (London)",
                        designation = "Senior Software Engineer",
                        location = "London, UK",
                        isAvailableForMentorship = true,
                        skills = listOf("System Design", "Distributed Systems", "AI Infrastructure")
                    ),
                    AlumniProfile(
                        alumniId = "ALM-02",
                        name = "Sabrina Yasmin",
                        graduationYear = "2020",
                        department = "CSE",
                        company = "Google Munich",
                        designation = "Staff Android Engineer",
                        location = "Munich, Germany",
                        isAvailableForMentorship = true,
                        skills = listOf("Jetpack Compose", "Android Internal Architecture", "Code Review")
                    ),
                    AlumniProfile(
                        alumniId = "ALM-03",
                        name = "Fahim Shahriar",
                        graduationYear = "2022",
                        department = "EEE",
                        company = "Samsung R&D BD",
                        designation = "Embedded Systems Lead",
                        location = "Dhaka",
                        isAvailableForMentorship = false,
                        skills = listOf("IoT", "Microcontrollers", "Firmware")
                    )
                )
            )
        }
    }

    override fun getMentorshipSessions(): Flow<List<MentorshipSession>> {
        return flow {
            emit(
                listOf(
                    MentorshipSession(
                        sessionId = "MNT-101",
                        mentorName = "Sabrina Yasmin (Google)",
                        topic = "Mastering Modern Android Architecture & Jetpack Compose",
                        scheduledTime = "15 Aug 2026, 09:00 PM BST",
                        status = "CONFIRMED",
                        meetingLink = "https://meet.google.com/google-alumni-talk"
                    )
                )
            )
        }
    }

    override fun getPlacementStatistics(): Flow<PlacementStatistics> {
        return flow {
            emit(PlacementStatistics())
        }
    }

    override suspend fun applyForJob(jobId: String, jobTitle: String, companyName: String) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val dateStr = dateFormat.format(Date())
        val app = JobApplication(
            applicationId = "APP-${System.currentTimeMillis() % 10000}",
            jobId = jobId,
            jobTitle = jobTitle,
            companyName = companyName,
            submittedAt = dateStr,
            status = ApplicationStatus.SUBMITTED,
            remarks = "Application received by HR team."
        )
        careerDao.insertApplication(app.toEntity())
        careerDao.markJobApplied(jobId)
    }

    override suspend fun toggleBookmark(jobId: String, isBookmarked: Boolean) {
        careerDao.updateBookmarkStatus(jobId, isBookmarked)
    }

    override suspend fun updateResumeProfile(resume: ResumeProfile) {
        careerDao.insertOrUpdateResume(resume.toEntity())
    }

    override suspend fun requestMentorship(mentorName: String, topic: String) {
        // Mock save
    }

    private fun getInitialMockJobs(): List<JobPosting> {
        return listOf(
            JobPosting(
                jobId = "JOB-101",
                companyName = "Google Brain / DeepMind BD",
                title = "Junior Machine Learning Engineer",
                department = "Computer Science & Engineering",
                requiredSkills = listOf("Kotlin", "Python", "TensorFlow", "REST APIs"),
                minimumCgpa = 3.50,
                salaryRange = "BDT 90,000 - 120,000 / mo",
                location = "Dhaka (Hybrid)",
                employmentType = EmploymentType.FULL_TIME,
                vacancies = 3,
                deadline = "25 Aug 2026",
                isBookmarked = true,
                isApplied = true
            ),
            JobPosting(
                jobId = "JOB-102",
                companyName = "Pathao Ltd",
                title = "Android Mobile Engineer",
                department = "CSE / EEE",
                requiredSkills = listOf("Kotlin", "Jetpack Compose", "Coroutines", "Room DB"),
                minimumCgpa = 3.00,
                salaryRange = "BDT 75,000 - 95,000 / mo",
                location = "Gulshan 1, Dhaka",
                employmentType = EmploymentType.FULL_TIME,
                vacancies = 5,
                deadline = "30 Aug 2026",
                isBookmarked = false,
                isApplied = false
            ),
            JobPosting(
                jobId = "JOB-103",
                companyName = "Samsung R&D Bangladesh",
                title = "Software Engineer - AI & Algorithms",
                department = "CSE / Software Engineering",
                requiredSkills = listOf("C++", "Python", "Algorithms", "Data Structures"),
                minimumCgpa = 3.30,
                salaryRange = "BDT 80,000 - 110,000 / mo",
                location = "Wireless Railgate, Mohakhali",
                employmentType = EmploymentType.FULL_TIME,
                vacancies = 8,
                deadline = "02 Sep 2026",
                isBookmarked = true,
                isApplied = false
            )
        )
    }

    private fun JobPosting.toEntity() = JobPostingEntity(
        jobId = jobId,
        companyName = companyName,
        companyLogoUrl = companyLogoUrl,
        title = title,
        department = department,
        requiredSkillsCsv = requiredSkills.joinToString(","),
        minimumCgpa = minimumCgpa,
        salaryRange = salaryRange,
        location = location,
        employmentType = employmentType.name,
        vacancies = vacancies,
        deadline = deadline,
        isBookmarked = isBookmarked,
        isApplied = isApplied
    )

    private fun JobPostingEntity.toDomain() = JobPosting(
        jobId = jobId,
        companyName = companyName,
        companyLogoUrl = companyLogoUrl,
        title = title,
        department = department,
        requiredSkills = requiredSkillsCsv.split(",").map { it.trim() },
        minimumCgpa = minimumCgpa,
        salaryRange = salaryRange,
        location = location,
        employmentType = try { EmploymentType.valueOf(employmentType) } catch (e: Exception) { EmploymentType.FULL_TIME },
        vacancies = vacancies,
        deadline = deadline,
        isBookmarked = isBookmarked,
        isApplied = isApplied
    )

    private fun JobApplication.toEntity() = JobApplicationEntity(
        applicationId = applicationId,
        jobId = jobId,
        jobTitle = jobTitle,
        companyName = companyName,
        submittedAt = submittedAt,
        status = status.name,
        interviewTime = interviewTime,
        interviewLink = interviewLink,
        remarks = remarks
    )

    private fun JobApplicationEntity.toDomain() = JobApplication(
        applicationId = applicationId,
        jobId = jobId,
        jobTitle = jobTitle,
        companyName = companyName,
        submittedAt = submittedAt,
        status = try { ApplicationStatus.valueOf(status) } catch (e: Exception) { ApplicationStatus.SUBMITTED },
        interviewTime = interviewTime,
        interviewLink = interviewLink,
        remarks = remarks
    )

    private fun ResumeProfile.toEntity() = ResumeProfileEntity(
        studentId = studentId,
        studentName = studentName,
        department = department,
        cgpa = cgpa,
        headline = headline,
        summary = summary,
        skillsCsv = skills.joinToString(","),
        projectsCsv = projects.joinToString(";"),
        resumeScore = resumeScore
    )

    private fun ResumeProfileEntity.toDomain() = ResumeProfile(
        studentId = studentId,
        studentName = studentName,
        department = department,
        cgpa = cgpa,
        headline = headline,
        summary = summary,
        skills = skillsCsv.split(",").map { it.trim() },
        projects = projectsCsv.split(";").map { it.trim() },
        resumeScore = resumeScore
    )
}
