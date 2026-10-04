package com.example.domain.repository

import com.example.domain.model.result.*
import kotlinx.coroutines.flow.Flow

interface ResultRepository {
    fun getCourseResultsForStudent(studentId: String): Flow<List<CourseResult>>
    fun getSemesterResultsForStudent(studentId: String): Flow<List<SemesterResult>>
    fun getTeacherCourseResults(teacherId: String, courseId: String): Flow<List<CourseResult>>
    fun getDepartmentCourseResults(department: String, semester: String): Flow<List<CourseResult>>

    suspend fun submitMarksByTeacher(courseResults: List<CourseResult>): Result<Unit>
    suspend fun approveDepartmentResults(department: String, semester: String): Result<Unit>
    suspend fun publishSemesterResults(department: String, semester: String): Result<Unit>
    suspend fun getTranscriptForStudent(studentId: String): Result<TranscriptModel>
    suspend fun getUniversityAnalytics(department: String): Result<UniversityResultAnalytics>
    suspend fun syncUnsyncedResults(): Result<Int>
}
