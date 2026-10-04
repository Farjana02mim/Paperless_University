package com.example.data.local.dao.career

import androidx.room.*
import com.example.data.local.entity.career.JobApplicationEntity
import com.example.data.local.entity.career.JobPostingEntity
import com.example.data.local.entity.career.ResumeProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CareerDao {

    @Query("SELECT * FROM career_jobs")
    fun getAllJobs(): Flow<List<JobPostingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobs(jobs: List<JobPostingEntity>)

    @Query("UPDATE career_jobs SET isBookmarked = :isBookmarked WHERE jobId = :jobId")
    suspend fun updateBookmarkStatus(jobId: String, isBookmarked: Boolean)

    @Query("UPDATE career_jobs SET isApplied = 1 WHERE jobId = :jobId")
    suspend fun markJobApplied(jobId: String)

    @Query("SELECT * FROM career_applications")
    fun getAllApplications(): Flow<List<JobApplicationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(app: JobApplicationEntity)

    @Query("SELECT * FROM career_resume WHERE studentId = :studentId LIMIT 1")
    fun getResumeProfile(studentId: String): Flow<ResumeProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateResume(resume: ResumeProfileEntity)
}
