package com.example

import com.example.core.result.GpaEngine
import com.example.domain.model.result.AcademicStanding
import com.example.domain.model.result.CourseResult
import com.example.domain.model.result.GradingPolicy
import com.example.domain.model.result.SemesterResult
import org.junit.Assert.assertEquals
import org.junit.Test

class GpaEngineTest {

    @Test
    fun testGradingScaleCalculation() {
        val policy = GradingPolicy()
        val (gradeA, pointA) = GpaEngine.calculateCourseGrade(85.0, policy)
        assertEquals("A+", gradeA)
        assertEquals(4.00, pointA, 0.01)

        val (gradeB, pointB) = GpaEngine.calculateCourseGrade(62.0, policy)
        assertEquals("B", gradeB)
        assertEquals(3.00, pointB, 0.01)

        val (gradeF, pointF) = GpaEngine.calculateCourseGrade(35.0, policy)
        assertEquals("F", gradeF)
        assertEquals(0.00, pointF, 0.01)
    }

    @Test
    fun testSemesterGpaCalculation() {
        val courses = listOf(
            CourseResult(courseCode = "CS-101", creditHours = 3.0, gradePoint = 4.00), // 12
            CourseResult(courseCode = "CS-102", creditHours = 3.0, gradePoint = 3.50), // 10.5
            CourseResult(courseCode = "CS-103", creditHours = 3.0, gradePoint = 3.00)  // 9
        )
        // Total points = 31.5, Total credits = 9 => GPA = 3.50
        val gpa = GpaEngine.calculateSemesterGPA(courses)
        assertEquals(3.50, gpa, 0.01)
    }

    @Test
    fun testEarnedCreditsCalculation() {
        val courses = listOf(
            CourseResult(courseCode = "CS-101", creditHours = 3.0, gradePoint = 4.00),
            CourseResult(courseCode = "CS-102", creditHours = 3.0, gradePoint = 0.00) // Failed
        )
        val earned = GpaEngine.calculateEarnedCredits(courses)
        assertEquals(3.0, earned, 0.01)
    }

    @Test
    fun testAcademicStandingDetermination() {
        assertEquals(AcademicStanding.DEANS_LIST, GpaEngine.determineAcademicStanding(3.85))
        assertEquals(AcademicStanding.EXCELLENT, GpaEngine.determineAcademicStanding(3.60))
        assertEquals(AcademicStanding.GOOD, GpaEngine.determineAcademicStanding(3.20))
        assertEquals(AcademicStanding.SATISFACTORY, GpaEngine.determineAcademicStanding(2.50))
        assertEquals(AcademicStanding.PROBATION, GpaEngine.determineAcademicStanding(1.80))
    }
}
