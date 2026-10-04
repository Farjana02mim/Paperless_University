package com.example.domain.usecase.result

import com.example.domain.model.result.*
import com.example.domain.repository.ResultRepository
import kotlinx.coroutines.flow.Flow

class GetStudentResultUseCase(private val repository: ResultRepository) {
    operator fun invoke(studentId: String): Flow<List<CourseResult>> =
        repository.getCourseResultsForStudent(studentId)
}

class GetSemesterResultsUseCase(private val repository: ResultRepository) {
    operator fun invoke(studentId: String): Flow<List<SemesterResult>> =
        repository.getSemesterResultsForStudent(studentId)
}

class SubmitTeacherMarksUseCase(private val repository: ResultRepository) {
    suspend operator fun invoke(courseResults: List<CourseResult>): Result<Unit> =
        repository.submitMarksByTeacher(courseResults)
}

class ApproveAndPublishResultUseCase(private val repository: ResultRepository) {
    suspend operator fun invoke(department: String, semester: String): Result<Unit> =
        repository.publishSemesterResults(department, semester)
}

class GetTranscriptUseCase(private val repository: ResultRepository) {
    suspend operator fun invoke(studentId: String): Result<TranscriptModel> =
        repository.getTranscriptForStudent(studentId)
}

class GetResultAnalyticsUseCase(private val repository: ResultRepository) {
    suspend operator fun invoke(department: String): Result<UniversityResultAnalytics> =
        repository.getUniversityAnalytics(department)
}
