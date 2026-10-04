package com.example.data.security

/**
 * Enterprise Role-Based Access Control (RBAC) System for Smart University Platform.
 */
enum class UserRole(val roleName: String, val level: Int) {
    STUDENT("Student", 1),
    TEACHER("Teacher", 2),
    ADMIN("Admin", 10),
    FINANCE_OFFICER("Finance Officer", 5),
    LIBRARIAN("Librarian", 4),
    PLACEMENT_OFFICER("Placement Officer", 4),
    TRANSPORT_OFFICER("Transport Officer", 3),
    HOSTEL_MANAGER("Hostel Manager", 3),
    MEDICAL_OFFICER("Medical Officer", 4),
    SUPPORT_STAFF("Support Staff", 2);

    companion object {
        fun fromString(role: String): UserRole {
            return values().find { 
                it.name.equals(role, ignoreCase = true) || it.roleName.equals(role, ignoreCase = true) 
            } ?: STUDENT
        }
    }
}

enum class Permission {
    // Academic & Grading
    VIEW_GRADES,
    ENTER_MARKS,
    PUBLISH_RESULTS,
    MANAGE_COURSES,

    // Attendance
    SCAN_ATTENDANCE_QR,
    GENERATE_ATTENDANCE_QR,
    OVERRIDE_ATTENDANCE,

    // Financial
    VIEW_OWN_FEES,
    PAY_FEES,
    COLLECT_PAYMENTS,
    APPROVE_FINANCIAL_AID,
    GENERATE_FINANCIAL_AUDIT,

    // Digital Library
    BORROW_BOOK,
    UPLOAD_LIBRARY_RESOURCE,
    MANAGE_LIBRARY_INVENTORY,

    // Career & Placements
    APPLY_FOR_JOBS,
    POST_JOB_LISTING,
    AUDIT_PLACEMENTS,

    // Campus Services
    MANAGE_TRANSPORT_ROUTES,
    ALLOCATE_HOSTEL_ROOMS,
    LOG_MEDICAL_RECORD,

    // System Administration
    MANAGE_USER_ROLES,
    VIEW_AUDIT_LOGS,
    CONFIGURE_SYSTEM_SETTINGS,
    BACKUP_DATABASE,
    SYSTEM_DIAGNOSTICS
}

object RoleAuthorizationManager {

    private val rolePermissions: Map<UserRole, Set<Permission>> = mapOf(
        UserRole.STUDENT to setOf(
            Permission.VIEW_GRADES,
            Permission.SCAN_ATTENDANCE_QR,
            Permission.VIEW_OWN_FEES,
            Permission.PAY_FEES,
            Permission.BORROW_BOOK,
            Permission.APPLY_FOR_JOBS
        ),
        UserRole.TEACHER to setOf(
            Permission.VIEW_GRADES,
            Permission.ENTER_MARKS,
            Permission.MANAGE_COURSES,
            Permission.GENERATE_ATTENDANCE_QR,
            Permission.UPLOAD_LIBRARY_RESOURCE,
            Permission.BORROW_BOOK
        ),
        UserRole.ADMIN to Permission.values().toSet(), // Admin has all permissions

        UserRole.FINANCE_OFFICER to setOf(
            Permission.VIEW_OWN_FEES,
            Permission.PAY_FEES,
            Permission.COLLECT_PAYMENTS,
            Permission.APPROVE_FINANCIAL_AID,
            Permission.GENERATE_FINANCIAL_AUDIT,
            Permission.VIEW_AUDIT_LOGS
        ),
        UserRole.LIBRARIAN to setOf(
            Permission.BORROW_BOOK,
            Permission.UPLOAD_LIBRARY_RESOURCE,
            Permission.MANAGE_LIBRARY_INVENTORY
        ),
        UserRole.PLACEMENT_OFFICER to setOf(
            Permission.APPLY_FOR_JOBS,
            Permission.POST_JOB_LISTING,
            Permission.AUDIT_PLACEMENTS
        ),
        UserRole.TRANSPORT_OFFICER to setOf(
            Permission.MANAGE_TRANSPORT_ROUTES
        ),
        UserRole.HOSTEL_MANAGER to setOf(
            Permission.ALLOCATE_HOSTEL_ROOMS
        ),
        UserRole.MEDICAL_OFFICER to setOf(
            Permission.LOG_MEDICAL_RECORD
        ),
        UserRole.SUPPORT_STAFF to setOf(
            Permission.BORROW_BOOK
        )
    )

    fun hasPermission(userRole: UserRole, permission: Permission): Boolean {
        if (userRole == UserRole.ADMIN) return true
        return rolePermissions[userRole]?.contains(permission) ?: false
    }

    fun hasAnyPermission(userRole: UserRole, permissions: List<Permission>): Boolean {
        return permissions.any { hasPermission(userRole, it) }
    }

    fun validateRoleAccess(userRoleString: String, requiredPermission: Permission): Boolean {
        val role = UserRole.fromString(userRoleString)
        return hasPermission(role, requiredPermission)
    }
}
