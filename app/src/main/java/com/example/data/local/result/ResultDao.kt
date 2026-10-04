package com.example.data.local.result

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultDao {

    @Query("SELECT * FROM course_results WHERE studentId = :studentId ORDER BY semester ASC, courseCode ASC")
    fun getCourseResultsForStudent(studentId: String): Flow<List<CourseResultEntity>>

    @Query("SELECT * FROM course_results WHERE studentId = :studentId AND semester = :semester")
    fun getCourseResultsBySemester(studentId: String, semester: String): Flow<List<CourseResultEntity>>

    @Query("SELECT * FROM course_results WHERE teacherId = :teacherId AND courseId = :courseId")
    fun getCourseResultsByTeacher(teacherId: String, courseId: String): Flow<List<CourseResultEntity>>

    @Query("SELECT * FROM course_results WHERE department = :dept AND semester = :semester")
    fun getCourseResultsByDepartmentAndSemester(dept: String, semester: String): Flow<List<CourseResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourseResult(result: CourseResultEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourseResults(results: List<CourseResultEntity>)

    @Query("SELECT * FROM semester_results WHERE studentId = :studentId ORDER BY semester ASC")
    fun getSemesterResultsForStudent(studentId: String): Flow<List<SemesterResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemesterResult(result: SemesterResultEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemesterResults(results: List<SemesterResultEntity>)

    @Query("SELECT * FROM course_results WHERE isSynced = 0")
    suspend fun getUnsyncedCourseResults(): List<CourseResultEntity>
}
