package com.example.domain.model.career

/**
 * Enterprise Career Development Platform Models
 */

enum class EmploymentType {
    FULL_TIME, PART_TIME, INTERNSHIP, REMOTE, HYBRID
}

enum class ApplicationStatus {
    SUBMITTED, SHORTLISTED, INTERVIEW_SCHEDULED, OFFER_EXTENDED, REJECTED
}

data class JobPosting(
    val jobId: String = "JOB-101",
    val companyName: String = "Google Brain / DeepMind BD",
    val companyLogoUrl: String = "",
    val title: String = "Junior Machine Learning Engineer",
    val department: String = "Computer Science & Engineering",
    val requiredSkills: List<String> = listOf("Kotlin", "Python", "TensorFlow", "PyTorch", "REST APIs"),
    val minimumCgpa: Double = 3.50,
    val salaryRange: String = "BDT 85,000 - 110,000 / month",
    val location: String = "Gulshan 2, Dhaka (Hybrid)",
    val employmentType: EmploymentType = EmploymentType.FULL_TIME,
    val vacancies: Int = 3,
    val deadline: String = "25 August 2026",
    val isBookmarked: Boolean = false,
    val isApplied: Boolean = true
)

data class InternshipPosting(
    val internshipId: String = "INT-302",
    val companyName: String = "Brain Station 23",
    val title: String = "Mobile Software Engineering Intern",
    val durationMonths: Int = 6,
    val stipendMonthly: Double = 25000.0,
    val location: String = "Dhaka (Remote Option Available)",
    val requiredSkills: List<String> = listOf("Android", "Jetpack Compose", "Git"),
    val deadline: String = "30 August 2026",
    val isApplied: Boolean = false
)

data class JobApplication(
    val applicationId: String = "APP-9912",
    val jobId: String = "JOB-101",
    val jobTitle: String = "Junior Machine Learning Engineer",
    val companyName: String = "Google Brain / DeepMind BD",
    val submittedAt: String = "02 Aug 2026",
    val status: ApplicationStatus = ApplicationStatus.INTERVIEW_SCHEDULED,
    val interviewTime: String = "10 August 2026 at 11:00 AM",
    val interviewLink: String = "https://meet.google.com/xyz-abc-career",
    val remarks: String = "Shortlisted based on CGPA and Android/AI Projects."
)

data class ResumeProfile(
    val studentName: String = "Tanvir Ahmed",
    val studentId: String = "2024-3-60-042",
    val department: String = "Computer Science & Engineering",
    val cgpa: Double = 3.88,
    val headline: String = "Aspiring AI Engineer & Kotlin Android Developer",
    val summary: String = "Final year CSE student with strong experience in building paperless university apps, ML models, and mobile software.",
    val skills: List<String> = listOf("Kotlin", "Jetpack Compose", "Python", "TensorFlow", "Room DB", "Firebase"),
    val projects: List<String> = listOf(
        "Smart Paperless Campus App - Full Android ecosystem for university operations",
        "AI Exam Proctoring Engine - Computer vision algorithm for automated cheating detection"
    ),
    val resumeScore: Int = 92
)

data class AlumniProfile(
    val alumniId: String = "ALM-801",
    val name: String = "Mahmudul Hasan",
    val graduationYear: String = "2022",
    val department: String = "CSE",
    val company: String = "Meta AI (London)",
    val designation: String = "Senior Software Engineer",
    val location: String = "London, UK",
    val isAvailableForMentorship: Boolean = true,
    val skills: List<String> = listOf("Distributed Systems", "AI Infrastructure", "Career Guidance")
)

data class MentorshipSession(
    val sessionId: String = "MNT-402",
    val mentorName: String = "Mahmudul Hasan (Meta AI)",
    val topic: String = "How to Crack International Tech & AI Interviews",
    val scheduledTime: String = "14 August 2026, 08:00 PM (BST)",
    val status: String = "CONFIRMED",
    val meetingLink: String = "https://meet.google.com/alm-mentor-sec"
)

data class PlacementStatistics(
    val totalGraduatingStudents: Int = 850,
    val totalStudentsPlaced: Int = 748,
    val placementRatePercent: Double = 88.0,
    val averageSalaryBdt: Double = 68000.0,
    val highestSalaryBdt: Double = 185000.0,
    val topRecruitingCompaniesCount: Int = 42,
    val departmentPlacementRates: Map<String, Double> = mapOf(
        "CSE" to 95.5,
        "EEE" to 89.0,
        "BBA" to 86.2,
        "Pharmacy" to 82.0
    )
)
