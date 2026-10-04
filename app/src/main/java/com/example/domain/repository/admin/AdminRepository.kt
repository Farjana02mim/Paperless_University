package com.example.domain.repository.admin

import com.example.domain.model.admin.*
import kotlinx.coroutines.flow.Flow

interface AdminRepository {
    fun getAdminProfile(): Flow<AdminProfile>
    fun getSystemMetrics(): Flow<AdminSystemMetrics>
    fun getUsers(searchQuery: String, roleFilter: String, deptFilter: String): Flow<List<AdminUserItem>>
    fun getRoles(): Flow<List<RolePermission>>
    fun getDepartments(): Flow<List<AdminDepartment>>
    fun getCourses(): Flow<List<AdminCourse>>
    fun getAdmissionApplications(): Flow<List<AdmissionApplication>>
    fun getFinanceInvoices(): Flow<List<AdminFinanceInvoice>>
    fun getAuditLogs(): Flow<List<AdminAuditLog>>
    fun getSystemSettings(): Flow<SystemSettings>
    fun getAiInsights(): Flow<List<AiInsightsArchitecture>>
    fun getGeneratedReports(): Flow<List<AdminReportItem>>

    suspend fun createUser(user: AdminUserItem)
    suspend fun updateUserStatus(userId: String, status: UserStatus)
    suspend fun deleteUser(userId: String)
    suspend fun createDepartment(dept: AdminDepartment)
    suspend fun createCourse(course: AdminCourse)
    suspend fun updateAdmissionStatus(appId: String, status: String, seat: String)
    suspend fun updateInvoiceStatus(invoiceId: String, status: String)
    suspend fun updateSystemSettings(settings: SystemSettings)
    suspend fun triggerCloudBackup(): String
    suspend fun generateReport(title: String, category: String, format: String): AdminReportItem
}
