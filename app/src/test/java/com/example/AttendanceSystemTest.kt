package com.example

import com.example.core.location.LocationVerifier
import com.example.core.security.QRSecurityEngine
import com.example.domain.model.QRCodePayload
import org.junit.Assert.*
import org.junit.Test

class AttendanceSystemTest {

    @Test
    fun testQRSignatureGenerationAndValidation() {
        val sessionId = "SESSION_TEST_101"
        val timestamp = System.currentTimeMillis()
        val secretKey = "SmartCampusCampusKey2026"

        val signature = QRSecurityEngine.generateSignature(sessionId, timestamp, secretKey)
        assertNotNull(signature)
        assertTrue(signature.length > 10)

        val payload = QRCodePayload(
            sessionId = sessionId,
            courseId = "CS-301",
            teacherId = "TCH_101",
            department = "CS",
            semester = "Sem 6",
            classroom = "302",
            timestamp = timestamp,
            expiresAt = timestamp + 60000L,
            digitalSignature = signature
        )

        val validation = QRSecurityEngine.validatePayload(
            payload = payload,
            secretKey = secretKey,
            maxRotationSeconds = 60
        )

        assertTrue("Expected valid QR payload", validation is QRSecurityEngine.ValidationResult.Valid)
    }

    @Test
    fun testExpiredQRPayloadValidation() {
        val sessionId = "SESSION_EXPIRED"
        val oldTimestamp = System.currentTimeMillis() - 120000L // 2 minutes ago
        val secretKey = "SmartCampusCampusKey2026"

        val signature = QRSecurityEngine.generateSignature(sessionId, oldTimestamp, secretKey)
        val payload = QRCodePayload(
            sessionId = sessionId,
            timestamp = oldTimestamp,
            expiresAt = oldTimestamp + 60000L, // Expired 1 minute ago
            digitalSignature = signature
        )

        val validation = QRSecurityEngine.validatePayload(
            payload = payload,
            secretKey = secretKey,
            maxRotationSeconds = 60
        )

        assertTrue("Expected expired QR result", validation is QRSecurityEngine.ValidationResult.Expired)
    }

    @Test
    fun testLocationHaversineDistanceCalculation() {
        // Teacher location
        val teacherLat = 37.4220
        val teacherLon = -122.0840

        // Student 1: Inside classroom (~15 meters away)
        val studentInsideLat = 37.4221
        val studentInsideLon = -122.0841

        val checkInside = LocationVerifier.verifyLocation(
            studentLat = studentInsideLat,
            studentLon = studentInsideLon,
            classroomLat = teacherLat,
            classroomLon = teacherLon,
            maxRadiusMeters = 30.0
        )
        assertTrue("Student within 30m should pass location check", checkInside is LocationVerifier.LocationCheckResult.Success)

        // Student 2: Outside classroom (~500 meters away)
        val studentOutsideLat = 37.4260
        val studentOutsideLon = -122.0880

        val checkOutside = LocationVerifier.verifyLocation(
            studentLat = studentOutsideLat,
            studentLon = studentOutsideLon,
            classroomLat = teacherLat,
            classroomLon = teacherLon,
            maxRadiusMeters = 30.0
        )
        assertTrue("Student outside 30m should fail location check", checkOutside is LocationVerifier.LocationCheckResult.OutOfRadius)
    }
}
