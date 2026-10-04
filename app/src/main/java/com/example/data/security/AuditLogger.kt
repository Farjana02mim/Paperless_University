package com.example.data.security

import com.example.data.local.dao.audit.AuditLogDao
import com.example.data.local.entity.audit.AuditLogEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Enterprise Audit Logger for recording security events, authentication,
 * administrative actions, financial transactions, and grading changes.
 * Syncs locally with Room and remotely with Firestore 'audit_logs' collection.
 */
class AuditLogger(
    private val auditLogDao: AuditLogDao,
    private val firestore: FirebaseFirestore? = null
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    fun logEvent(
        userId: String,
        userName: String,
        userRole: String,
        action: String,
        module: String,
        description: String,
        status: String = "SUCCESS",
        ipAddress: String = "192.168.1.1"
    ) {
        val auditEntity = AuditLogEntity(
            userId = userId,
            userName = userName,
            userRole = userRole,
            action = action,
            module = module,
            description = description,
            status = status,
            ipAddress = ipAddress
        )

        scope.launch {
            try {
                auditLogDao.insertAuditLog(auditEntity)
                
                // Firestore Remote Mirroring
                firestore?.collection("audit_logs")
                    ?.document(auditEntity.id)
                    ?.set(auditEntity)
            } catch (e: Exception) {
                // Fail-safe silently for offline or restricted environments
            }
        }
    }

    fun getLogs(): Flow<List<AuditLogEntity>> {
        return auditLogDao.getAllAuditLogs()
    }
}
