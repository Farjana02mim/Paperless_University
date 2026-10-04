package com.example.data.enterprise

import com.example.data.security.UserRole

/**
 * Enterprise Multi-Tenant Configuration Model.
 * Supports isolated databases, custom domains, and institution-specific policies
 * for multiple universities (e.g. Jamalpur Science and Technology University - JSTU).
 */
data class Tenant(
    val tenantId: String,
    val institutionName: String,
    val shortName: String,
    val domainName: String,
    val logoResourceName: String,
    val isMultiCampusEnabled: Boolean = true,
    val maxActiveStudents: Int = 50000,
    val isEncryptedAtRest: Boolean = true,
    val primaryColorHex: String = "#1B5E20",
    val secondaryColorHex: String = "#00897B",
    val firestoreProjectRegion: String = "asia-southeast1",
    val status: TenantStatus = TenantStatus.ACTIVE
)

enum class TenantStatus {
    ACTIVE,
    MAINTENANCE,
    SUSPENDED
}

object MultiTenantManager {
    val JSTU_TENANT = Tenant(
        tenantId = "jstu-bd-001",
        institutionName = "Jamalpur Science and Technology University",
        shortName = "JSTU",
        domainName = "jstu.ac.bd",
        logoResourceName = "img_jstu_logo_1786160804214",
        isMultiCampusEnabled = true,
        primaryColorHex = "#1B5E20",
        secondaryColorHex = "#00897B"
    )

    private var currentTenant: Tenant = JSTU_TENANT

    fun getCurrentTenant(): Tenant = currentTenant

    fun setTenant(tenant: Tenant) {
        currentTenant = tenant
    }

    fun resolveTenantIdForUser(userEmail: String): String {
        return when {
            userEmail.endsWith("@jstu.ac.bd") -> JSTU_TENANT.tenantId
            else -> JSTU_TENANT.tenantId // Default fallback
        }
    }
}
