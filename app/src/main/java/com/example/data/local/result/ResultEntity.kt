package com.example.data.local.result

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.result.*

@Entity(tableName = "course_results")
data class CourseResultEntity(
    @PrimaryKey val resultId: String,
    val studentId: String,
    val studentName: String,
    val rollNumber: String,
    val department: String,
    val faculty: String,
    val semester: String,
    val academicYear: String,
    val courseId: String,
    val courseCode: String,
    val courseTitle: String,
    val creditHours: Double,
    val teacherId: String,
    val teacherName: String,
    val attendanceMarks: Double,
    val assignmentMarks: Double,
    val quizMarks: Double,
    val midMarks: Double,
    val labMarks: Double,
    val finalMarks: Double,
    val totalMarks: Double,
    val letterGrade: String,
    val gradePoint: Double,
    val remarks: String,
    val isRetake: Boolean,
    val isImprovement: Boolean,
    val published: Boolean,
    val status: String,
    val publishedAt: Long,
    val updatedAt: Long,
    val isSynced: Boolean = true
)

fun CourseResultEntity.toDomain(): CourseResult {
    return CourseResult(
        resultId = resultId,
        studentId = studentId,
        studentName = studentName,
        rollNumber = rollNumber,
        department = department,
        faculty = faculty,
        semester = semester,
        academicYear = academicYear,
        courseId = courseId,
        courseCode = courseCode,
        courseTitle = courseTitle,
        creditHours = creditHours,
        teacherId = teacherId,
        teacherName = teacherName,
        attendanceMarks = attendanceMarks,
        assignmentMarks = assignmentMarks,
        quizMarks = quizMarks,
        midMarks = midMarks,
        labMarks = labMarks,
        finalMarks = finalMarks,
        totalMarks = totalMarks,
        letterGrade = letterGrade,
        gradePoint = gradePoint,
        remarks = remarks,
        isRetake = isRetake,
        isImprovement = isImprovement,
        published = published,
        status = try { ResultStatus.valueOf(status) } catch (e: Exception) { ResultStatus.DRAFT },
        publishedAt = publishedAt,
        updatedAt = updatedAt
    )
}

fun CourseResult.toEntity(isSynced: Boolean = true): CourseResultEntity {
    return CourseResultEntity(
        resultId = resultId,
        studentId = studentId,
        studentName = studentName,
        rollNumber = rollNumber,
        department = department,
        faculty = faculty,
        semester = semester,
        academicYear = academicYear,
        courseId = courseId,
        courseCode = courseCode,
        courseTitle = courseTitle,
        creditHours = creditHours,
        teacherId = teacherId,
        teacherName = teacherName,
        attendanceMarks = attendanceMarks,
        assignmentMarks = assignmentMarks,
        quizMarks = quizMarks,
        midMarks = midMarks,
        labMarks = labMarks,
        finalMarks = finalMarks,
        totalMarks = totalMarks,
        letterGrade = letterGrade,
        gradePoint = gradePoint,
        remarks = remarks,
        isRetake = isRetake,
        isImprovement = isImprovement,
        published = published,
        status = status.name,
        publishedAt = publishedAt,
        updatedAt = updatedAt,
        isSynced = isSynced
    )
}

@Entity(tableName = "semester_results")
data class SemesterResultEntity(
    @PrimaryKey val semesterId: String,
    val studentId: String,
    val studentName: String,
    val department: String,
    val semester: String,
    val academicYear: String,
    val registeredCredits: Double,
    val earnedCredits: Double,
    val semesterGPA: Double,
    val semesterCGPA: Double,
    val academicStanding: String,
    val rankInBatch: Int,
    val totalStudentsInBatch: Int,
    val publishStatus: String,
    val publishedAt: Long
)

fun SemesterResultEntity.toDomain(courses: List<CourseResult> = emptyList()): SemesterResult {
    return SemesterResult(
        semesterId = semesterId,
        studentId = studentId,
        studentName = studentName,
        department = department,
        semester = semester,
        academicYear = academicYear,
        courseResults = courses,
        registeredCredits = registeredCredits,
        earnedCredits = earnedCredits,
        semesterGPA = semesterGPA,
        semesterCGPA = semesterCGPA,
        academicStanding = try { AcademicStanding.valueOf(academicStanding) } catch (e: Exception) { AcademicStanding.SATISFACTORY },
        rankInBatch = rankInBatch,
        totalStudentsInBatch = totalStudentsInBatch,
        publishStatus = try { ResultStatus.valueOf(publishStatus) } catch (e: Exception) { ResultStatus.PUBLISHED },
        publishedAt = publishedAt
    )
}

fun SemesterResult.toEntity(): SemesterResultEntity {
    return SemesterResultEntity(
        semesterId = semesterId,
        studentId = studentId,
        studentName = studentName,
        department = department,
        semester = semester,
        academicYear = academicYear,
        registeredCredits = registeredCredits,
        earnedCredits = earnedCredits,
        semesterGPA = semesterGPA,
        semesterCGPA = semesterCGPA,
        academicStanding = academicStanding.name,
        rankInBatch = rankInBatch,
        totalStudentsInBatch = totalStudentsInBatch,
        publishStatus = publishStatus.name,
        publishedAt = publishedAt
    )
}
