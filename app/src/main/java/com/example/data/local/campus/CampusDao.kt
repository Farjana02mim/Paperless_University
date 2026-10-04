package com.example.data.local.campus

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CampusDao {

    @Query("SELECT * FROM bus_routes")
    fun getAllBusRoutes(): Flow<List<BusRouteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusRoutes(routes: List<BusRouteEntity>)

    @Query("SELECT * FROM hostel_leave_requests")
    fun getLeaveRequests(): Flow<List<HostelLeaveRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaveRequest(request: HostelLeaveRequestEntity)

    @Query("SELECT * FROM emergency_alerts ORDER BY timestamp DESC")
    fun getEmergencyAlerts(): Flow<List<EmergencyAlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmergencyAlert(alert: EmergencyAlertEntity)

    @Query("SELECT * FROM support_tickets ORDER BY createdDate DESC")
    fun getSupportTickets(): Flow<List<SupportTicketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupportTicket(ticket: SupportTicketEntity)
}
