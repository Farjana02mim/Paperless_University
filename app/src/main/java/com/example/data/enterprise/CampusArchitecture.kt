package com.example.data.enterprise

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Multi-Campus Architecture Models for Enterprise University System.
 * Enables central governance with decentralized campus-level administration.
 */
@Entity(tableName = "campuses")
data class CampusEntity(
    @PrimaryKey val campusId: String = UUID.randomUUID().toString(),
    val tenantId: String = "jstu-bd-001",
    val campusName: String, // e.g. "Main Campus", "Town Academic Extension"
    val campusCode: String, // e.g. "JSTU-MC", "JSTU-EC"
    val locationAddress: String,
    val totalDepartments: Int,
    val totalEnrolledStudents: Int,
    val campusDirectorName: String,
    val emergencyHelpline: String,
    val status: String = "ACTIVE"
)

data class CampusPolicy(
    val campusId: String,
    val minimumAttendancePercentage: Double = 75.0,
    val gradingScale: String = "4.00 CGPA",
    val libraryMaxBorrowLimit: Int = 5,
    val finePerDayLateFeeBdt: Double = 10.0,
    val isBiometricAttendanceMandatory: Boolean = true
)

object CampusManager {
    val JSTU_CAMPUSES = listOf(
        CampusEntity(
            campusId = "jstu-main-campus",
            tenantId = "jstu-bd-001",
            campusName = "JSTU Main Campus",
            campusCode = "JSTU-MC",
            locationAddress = "Jamalpur Sadar, Jamalpur, Bangladesh",
            totalDepartments = 18,
            totalEnrolledStudents = 12500,
            campusDirectorName = "Prof. Dr. Academic Director",
            emergencyHelpline = "+880 1711-000000"
        ),
        CampusEntity(
            campusId = "jstu-city-campus",
            tenantId = "jstu-bd-001",
            campusName = "JSTU Academic Extension Wing",
            campusCode = "JSTU-EC",
            locationAddress = "Town Center, Jamalpur",
            totalDepartments = 4,
            totalEnrolledStudents = 2100,
            campusDirectorName = "Dr. Extension Dean",
            emergencyHelpline = "+880 1711-000001"
        )
    )
}
