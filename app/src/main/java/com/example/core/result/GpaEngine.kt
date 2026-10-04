package com.example.core.result

import com.example.domain.model.result.*
import kotlin.math.roundToInt

object GpaEngine {

    fun calculateCourseGrade(totalMarks: Double, policy: GradingPolicy = GradingPolicy()): Pair<String, Double> {
        val roundedMarks = (totalMarks * 100.0).roundToInt() / 100.0
        val rule = policy.rules.firstOrNull { roundedMarks >= it.minPercentage && roundedMarks <= it.maxPercentage }
            ?: policy.rules.last()
        return Pair(rule.letterGrade, rule.gradePoint)
    }

    fun calculateSemesterGPA(courseResults: List<CourseResult>): Double {
        if (courseResults.isEmpty()) return 0.0
        var totalPoints = 0.0
        var totalCredits = 0.0

        for (result in courseResults) {
            totalPoints += result.gradePoint * result.creditHours
            totalCredits += result.creditHours
        }

        if (totalCredits == 0.0) return 0.0
        val gpa = totalPoints / totalCredits
        return (gpa * 100.0).roundToInt() / 100.0
    }

    fun calculateOverallCGPA(semesterResults: List<SemesterResult>): Double {
        if (semesterResults.isEmpty()) return 0.0
        var totalPoints = 0.0
        var totalEarnedCredits = 0.0

        for (sem in semesterResults) {
            for (c in sem.courseResults) {
                totalPoints += c.gradePoint * c.creditHours
                totalEarnedCredits += c.creditHours
            }
        }

        if (totalEarnedCredits == 0.0) return 0.0
        val cgpa = totalPoints / totalEarnedCredits
        return (cgpa * 100.0).roundToInt() / 100.0
    }

    fun calculateEarnedCredits(courseResults: List<CourseResult>): Double {
        return courseResults.filter { it.gradePoint > 0.0 }.sumOf { it.creditHours }
    }

    fun calculateFailedCredits(courseResults: List<CourseResult>): Double {
        return courseResults.filter { it.gradePoint == 0.0 }.sumOf { it.creditHours }
    }

    fun determineAcademicStanding(cgpa: Double): AcademicStanding {
        return when {
            cgpa >= 3.75 -> AcademicStanding.DEANS_LIST
            cgpa >= 3.50 -> AcademicStanding.EXCELLENT
            cgpa >= 3.00 -> AcademicStanding.GOOD
            cgpa >= 2.25 -> AcademicStanding.SATISFACTORY
            cgpa >= 2.00 -> AcademicStanding.ACADEMIC_WARNING
            else -> AcademicStanding.PROBATION
        }
    }

    fun generateVerificationCode(studentId: String, cgpa: Double): String {
        val timestamp = System.currentTimeMillis()
        val raw = "UNI-RES-$studentId-$cgpa-$timestamp"
        return "VERIFIED-${raw.hashCode().toString(16).uppercase()}"
    }
}
