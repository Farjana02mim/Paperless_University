package com.example.core.location

import kotlin.math.*

object LocationVerifier {

    /**
     * Calculates distance in meters between two geographical coordinates using the Haversine formula.
     */
    fun calculateDistanceMeters(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val earthRadiusMeters = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadiusMeters * c
    }

    /**
     * Verifies if a student is within the classroom radius.
     */
    fun verifyLocation(
        studentLat: Double,
        studentLon: Double,
        classroomLat: Double,
        classroomLon: Double,
        maxRadiusMeters: Double = 30.0
    ): LocationCheckResult {
        if (studentLat == 0.0 && studentLon == 0.0) {
            return LocationCheckResult.LocationUnavailable("Unable to acquire accurate GPS location.")
        }

        if (classroomLat == 0.0 && classroomLon == 0.0) {
            // Classroom location not set by teacher -> default pass with warning
            return LocationCheckResult.Success(0.0)
        }

        val distance = calculateDistanceMeters(studentLat, studentLon, classroomLat, classroomLon)
        return if (distance <= maxRadiusMeters) {
            LocationCheckResult.Success(distance)
        } else {
            LocationCheckResult.OutOfRadius(
                distanceMeters = distance,
                maxAllowedMeters = maxRadiusMeters,
                message = "You are ${distance.roundToInt()}m away from classroom (Maximum allowed: ${maxRadiusMeters.roundToInt()}m)."
            )
        }
    }

    sealed class LocationCheckResult {
        data class Success(val distanceMeters: Double) : LocationCheckResult()
        data class OutOfRadius(
            val distanceMeters: Double,
            val maxAllowedMeters: Double,
            val message: String
        ) : LocationCheckResult()
        data class LocationUnavailable(val reason: String) : LocationCheckResult()
    }
}
