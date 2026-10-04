package com.example.data.local.campus

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bus_routes")
data class BusRouteEntity(
    @PrimaryKey val routeId: String,
    val routeName: String,
    val startLocation: String,
    val destination: String,
    val totalStops: Int,
    val regularTime: String,
    val busNumber: String,
    val driverName: String,
    val driverPhone: String,
    val availableSeats: Int,
    val totalCapacity: Int,
    val isRunningLive: Boolean,
    val currentEstimatedArrivalMinutes: Int
)

@Entity(tableName = "hostel_leave_requests")
data class HostelLeaveRequestEntity(
    @PrimaryKey val requestId: String,
    val studentName: String,
    val reason: String,
    val startDate: String,
    val endDate: String,
    val status: String
)

@Entity(tableName = "emergency_alerts")
data class EmergencyAlertEntity(
    @PrimaryKey val alertId: String,
    val senderName: String,
    val alertType: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isBroadcasting: Boolean,
    val emergencyHotline: String
)

@Entity(tableName = "support_tickets")
data class SupportTicketEntity(
    @PrimaryKey val ticketId: String,
    val category: String,
    val subject: String,
    val description: String,
    val priority: String,
    val status: String,
    val createdDate: String
)
