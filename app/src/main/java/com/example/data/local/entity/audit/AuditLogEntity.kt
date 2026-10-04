package com.example.data.local.entity.audit

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val userId: String,
    val userName: String,
    val userRole: String,
    val action: String, // e.g. "LOGIN", "PAYMENT", "RESULT_PUBLICATION", "MARK_ENTRY", "ROLE_CHANGE"
    val module: String, // e.g. "FINANCE", "ACADEMIC", "ATTENDANCE", "SECURITY"
    val description: String,
    val ipAddress: String = "127.0.0.1",
    val status: String = "SUCCESS" // "SUCCESS", "FAILED", "DENIED"
)
