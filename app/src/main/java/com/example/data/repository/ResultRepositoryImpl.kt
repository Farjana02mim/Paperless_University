package com.example.data.repository

import com.example.core.result.GpaEngine
import com.example.data.local.result.ResultDao
import com.example.data.local.result.toDomain
import com.example.data.local.result.toEntity
import com.example.domain.model.result.*
import com.example.domain.repository.ResultRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ResultRepositoryImpl(
    private val resultDao: ResultDao,
    private val firestore: FirebaseFirestore? = null
) : ResultRepository {

    private val resultsCollection get() = firestore?.collection("results")
    private val semesterResultsCollection get() = firestore?.collection("semesterResults")
    private val transcriptsCollection get() = firestore?.collection("transcripts")

    override fun getCourseResultsForStudent(studentId: String): Flow<List<CourseResult>> {
        return resultDao.getCourseResultsForStudent(studentId).map { entities ->
            if (entities.isEmpty()) {
                val mockList = generateMockCourseResults(studentId)
                resultDao.insertCourseResults(mockList.map { it.toEntity() })
                mockList
            } else {
                entities.map { it.toDomain() }
            }
        }
    }

    override fun getSemesterResultsForStudent(studentId: String): Flow<List<SemesterResult>> {
        return flow {
            val courses = getCourseResultsForStudent(studentId).firstOrNull() ?: emptyList()
            val groupedBySem = courses.groupBy { it.semester }

            val list = groupedBySem.map { (sem, courseList) ->
                val semGpa = GpaEngine.calculateSemesterGPA(courseList)
                val earned = GpaEngine.calculateEarnedCredits(courseList)
                val reg = courseList.sumOf { it.creditHours }

                SemesterResult(
                    semesterId = "SEM_$sem",
                    studentId = studentId,
                    studentName = courseList.firstOrNull()?.studentName ?: "Alex Rivera",
                    department = courseList.firstOrNull()?.department ?: "Computer Science",
                    semester = sem,
                    academicYear = "2025-2026",
                    courseResults = courseList,
                    registeredCredits = reg,
                    earnedCredits = earned,
                    semesterGPA = semGpa,
                    semesterCGPA = semGpa,
                    academicStanding = GpaEngine.determineAcademicStanding(semGpa),
                    rankInBatch = 3,
                    totalStudentsInBatch = 60,
                    publishStatus = ResultStatus.PUBLISHED
                )
            }
            emit(list)
        }
    }

    override fun getTeacherCourseResults(teacherId: String, courseId: String): Flow<List<CourseResult>> {
        return resultDao.getCourseResultsByTeacher(teacherId, courseId).map { entities ->
            if (entities.isEmpty()) {
                val mock = listOf(
                    createCourseResult("STD_2026_091", "Alex Rivera", "CS-2026-042", "Semester 6", "CS-301", "Algorithm Analysis", 3.0, 9.0, 9.5, 14.0, 26.0, 18.0, 42.0),
                    createCourseResult("STD_2026_092", "Sarah Jenkins", "CS-2026-015", "Semester 6", "CS-301", "Algorithm Analysis", 3.0, 10.0, 10.0, 15.0, 28.0, 20.0, 45.0),
                    createCourseResult("STD_2026_093", "Michael Chen", "CS-2026-088", "Semester 6", "CS-301", "Algorithm Analysis", 3.0, 8.0, 7.5, 12.0, 22.0, 15.0, 36.0)
                )
                resultDao.insertCourseResults(mock.map { it.toEntity() })
                mock
            } else {
                entities.map { it.toDomain() }
            }
        }
    }

    override fun getDepartmentCourseResults(department: String, semester: String): Flow<List<CourseResult>> {
        return resultDao.getCourseResultsByDepartmentAndSemester(department, semester).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun submitMarksByTeacher(courseResults: List<CourseResult>): Result<Unit> {
        return try {
            resultDao.insertCourseResults(courseResults.map { it.toEntity(isSynced = true) })
            Result.success(Unit)
        } catch (e: Exception) {
            resultDao.insertCourseResults(courseResults.map { it.toEntity(isSynced = false) })
            Result.success(Unit)
        }
    }

    override suspend fun approveDepartmentResults(department: String, semester: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun publishSemesterResults(department: String, semester: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun getTranscriptForStudent(studentId: String): Result<TranscriptModel> {
        val sems = getSemesterResultsForStudent(studentId).firstOrNull() ?: emptyList()
        val overallCgpa = GpaEngine.calculateOverallCGPA(sems)
        val overallCredits = sems.sumOf { it.earnedCredits }

        val transcript = TranscriptModel(
            transcriptId = "TR_${UUID.randomUUID().toString().take(8)}",
            studentId = studentId,
            studentName = "Alex Rivera",
            rollNumber = "CS-2026-042",
            department = "Computer Science & Engineering",
            faculty = "Faculty of Science & Technology",
            degreeTitle = "Bachelor of Science in Computer Science & Engineering",
            enrollmentYear = "2023",
            allSemesterResults = sems,
            overallCredits = overallCredits,
            overallCGPA = overallCgpa,
            graduationStatus = if (overallCredits >= 130) "Graduated" else "In Progress (112/140 Credits)",
            generatedAt = System.currentTimeMillis(),
            verificationCode = GpaEngine.generateVerificationCode(studentId, overallCgpa),
            digitalSignature = "SHA256-UNI-SIG-VERIFIED-OFFICIAL"
        )
        return Result.success(transcript)
    }

    override suspend fun getUniversityAnalytics(department: String): Result<UniversityResultAnalytics> {
        val analytics = UniversityResultAnalytics(
            department = department,
            totalStudents = 240,
            passPercentage = 94.5,
            averageCGPA = 3.42,
            topPerformers = listOf(Pair("Sarah Jenkins", 3.98), Pair("Alex Rivera", 3.82), Pair("David Miller", 3.78)),
            gradeDistribution = mapOf("A+" to 45, "A" to 82, "A-" to 60, "B+" to 32, "B" to 14, "F" to 7),
            gpaTrendPerSemester = mapOf("Sem 1" to 3.25, "Sem 2" to 3.38, "Sem 3" to 3.45, "Sem 4" to 3.52, "Sem 5" to 3.65, "Sem 6" to 3.82)
        )
        return Result.success(analytics)
    }

    override suspend fun syncUnsyncedResults(): Result<Int> {
        val unsynced = resultDao.getUnsyncedCourseResults()
        return Result.success(unsynced.size)
    }

    private fun generateMockCourseResults(studentId: String): List<CourseResult> {
        return listOf(
            createCourseResult(studentId, "Alex Rivera", "CS-2026-042", "Semester 6", "CS-301", "Advanced Algorithms", 3.0, 9.5, 9.5, 14.5, 27.0, 19.0, 44.0),
            createCourseResult(studentId, "Alex Rivera", "CS-2026-042", "Semester 6", "CS-302", "Database Systems II", 3.0, 9.0, 9.0, 13.5, 25.0, 18.0, 41.0),
            createCourseResult(studentId, "Alex Rivera", "CS-2026-042", "Semester 6", "CS-303", "Software Architecture", 3.0, 10.0, 10.0, 15.0, 28.0, 20.0, 46.0),
            createCourseResult(studentId, "Alex Rivera", "CS-2026-042", "Semester 6", "CS-304", "Computer Networks", 3.0, 8.5, 8.5, 12.0, 24.0, 16.0, 38.0),
            createCourseResult(studentId, "Alex Rivera", "CS-2026-042", "Semester 5", "CS-201", "Data Structures", 3.0, 9.0, 9.0, 14.0, 26.0, 18.0, 42.0),
            createCourseResult(studentId, "Alex Rivera", "CS-2026-042", "Semester 5", "CS-202", "Operating Systems", 3.0, 8.0, 8.0, 13.0, 25.0, 17.0, 39.0)
        )
    }

    private fun createCourseResult(
        studentId: String,
        studentName: String,
        roll: String,
        semester: String,
        code: String,
        title: String,
        credits: Double,
        att: Double,
        assign: Double,
        quiz: Double,
        mid: Double,
        lab: Double,
        fin: Double
    ): CourseResult {
        val total = att + assign + quiz + mid + lab + fin
        val (grade, point) = GpaEngine.calculateCourseGrade(total)
        return CourseResult(
            resultId = "RES_${UUID.randomUUID().toString().take(8)}",
            studentId = studentId,
            studentName = studentName,
            rollNumber = roll,
            department = "Computer Science",
            faculty = "Faculty of Science & Engineering",
            semester = semester,
            academicYear = "2025-2026",
            courseId = code,
            courseCode = code,
            courseTitle = title,
            creditHours = credits,
            teacherId = "TCH_101",
            teacherName = "Prof. Robert Vance",
            attendanceMarks = att,
            assignmentMarks = assign,
            quizMarks = quiz,
            midMarks = mid,
            labMarks = lab,
            finalMarks = fin,
            totalMarks = total,
            letterGrade = grade,
            gradePoint = point,
            remarks = if (point >= 3.5) "Outstanding Performance" else "Good Effort",
            published = true,
            status = ResultStatus.PUBLISHED,
            publishedAt = System.currentTimeMillis()
        )
    }
}
