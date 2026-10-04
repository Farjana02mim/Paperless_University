package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    @Query("SELECT * FROM local_attendance_records ORDER BY scannedAt DESC")
    fun getAllRecords(): Flow<List<LocalAttendanceRecordEntity>>

    @Query("SELECT * FROM local_attendance_records WHERE studentId = :studentId ORDER BY scannedAt DESC")
    fun getRecordsByStudentId(studentId: String): Flow<List<LocalAttendanceRecordEntity>>

    @Query("SELECT * FROM local_attendance_records WHERE sessionId = :sessionId ORDER BY scannedAt DESC")
    fun getRecordsBySessionId(sessionId: String): Flow<List<LocalAttendanceRecordEntity>>

    @Query("SELECT * FROM local_attendance_records WHERE isSynced = 0")
    suspend fun getUnsyncedRecords(): List<LocalAttendanceRecordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: LocalAttendanceRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<LocalAttendanceRecordEntity>)

    @Query("UPDATE local_attendance_records SET isSynced = 1 WHERE recordId = :recordId")
    suspend fun markRecordAsSynced(recordId: String)

    // Leave Requests
    @Query("SELECT * FROM local_leave_requests WHERE studentId = :studentId ORDER BY createdAt DESC")
    fun getLeaveRequestsByStudentId(studentId: String): Flow<List<LocalLeaveRequestEntity>>

    @Query("SELECT * FROM local_leave_requests ORDER BY createdAt DESC")
    fun getAllLeaveRequests(): Flow<List<LocalLeaveRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaveRequest(request: LocalLeaveRequestEntity)

    @Query("SELECT * FROM local_leave_requests WHERE isSynced = 0")
    suspend fun getUnsyncedLeaveRequests(): List<LocalLeaveRequestEntity>

    @Query("UPDATE local_leave_requests SET isSynced = 1 WHERE requestId = :requestId")
    suspend fun markLeaveRequestAsSynced(requestId: String)
}
