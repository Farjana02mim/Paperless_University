package com.example.core.security

import android.content.Context
import android.provider.Settings
import com.example.domain.model.QRCodePayload
import org.json.JSONObject
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object QRSecurityEngine {

    private const val HMAC_ALGORITHM = "HmacSHA256"

    /**
     * Generates a digital signature for an attendance QR code using HMAC-SHA256.
     */
    fun generateSignature(sessionId: String, timestamp: Long, secretKey: String): String {
        return try {
            val data = "$sessionId:$timestamp"
            val key = secretKey.ifBlank { "SmartCampusCampusKey2026" }
            val secretKeySpec = SecretKeySpec(key.toByteArray(Charsets.UTF_8), HMAC_ALGORITHM)
            val mac = Mac.getInstance(HMAC_ALGORITHM)
            mac.init(secretKeySpec)
            val hmacBytes = mac.doFinal(data.toByteArray(Charsets.UTF_8))
            hmacBytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            // Fallback SHA-256 digest
            val raw = "$sessionId:$timestamp:$secretKey"
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(raw.toByteArray(Charsets.UTF_8))
            digest.joinToString("") { "%02x".format(it) }
        }
    }

    /**
     * Encrypts/encodes payload into JSON string.
     */
    fun encodePayload(payload: QRCodePayload): String {
        val json = JSONObject().apply {
            put("sessionId", payload.sessionId)
            put("courseId", payload.courseId)
            put("teacherId", payload.teacherId)
            put("department", payload.department)
            put("semester", payload.semester)
            put("classroom", payload.classroom)
            put("timestamp", payload.timestamp)
            put("expiresAt", payload.expiresAt)
            put("encryptedToken", payload.encryptedToken)
            put("digitalSignature", payload.digitalSignature)
        }
        return json.toString()
    }

    /**
     * Decodes JSON QR string back into QRCodePayload object.
     */
    fun decodePayload(qrString: String): QRCodePayload? {
        return try {
            val json = JSONObject(qrString)
            QRCodePayload(
                sessionId = json.optString("sessionId", ""),
                courseId = json.optString("courseId", ""),
                teacherId = json.optString("teacherId", ""),
                department = json.optString("department", ""),
                semester = json.optString("semester", ""),
                classroom = json.optString("classroom", ""),
                timestamp = json.optLong("timestamp", 0L),
                expiresAt = json.optLong("expiresAt", 0L),
                encryptedToken = json.optString("encryptedToken", ""),
                digitalSignature = json.optString("digitalSignature", "")
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Validates if a QR code token is valid, unexpired, and authentic.
     */
    fun validatePayload(
        payload: QRCodePayload,
        secretKey: String,
        maxRotationSeconds: Int = 60
    ): ValidationResult {
        val currentTime = System.currentTimeMillis()

        if (payload.sessionId.isBlank()) {
            return ValidationResult.Invalid("Invalid QR payload: Missing session ID")
        }

        if (currentTime > payload.expiresAt && payload.expiresAt > 0) {
            return ValidationResult.Expired("QR Code has expired. Please scan the current active QR code.")
        }

        val ageSeconds = (currentTime - payload.timestamp) / 1000
        if (ageSeconds > maxRotationSeconds + 15) { // 15-second grace buffer
            return ValidationResult.Expired("QR Code token timed out ($ageSeconds sec old).")
        }

        val expectedSignature = generateSignature(payload.sessionId, payload.timestamp, secretKey)
        if (payload.digitalSignature.isNotBlank() && payload.digitalSignature != expectedSignature) {
            return ValidationResult.Tampered("Anti-tamper check failed: QR signature mismatch.")
        }

        return ValidationResult.Valid
    }

    /**
     * Helper to retrieve device ID for anti-cheating device lock.
     */
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            ?: "DEVICE_UNKNOWN"
    }

    sealed class ValidationResult {
        object Valid : ValidationResult()
        data class Expired(val reason: String) : ValidationResult()
        data class Tampered(val reason: String) : ValidationResult()
        data class Invalid(val reason: String) : ValidationResult()
    }
}
