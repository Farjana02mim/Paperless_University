package com.example.ui.navigation

/**
 * Type-safe navigation screen destinations for Smart Campus app.
 */
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object EmailVerification : Screen("email_verification")
    object StudentDashboard : Screen("student_dashboard")
    object TeacherDashboard : Screen("teacher_dashboard")
    object AdminDashboard : Screen("admin_dashboard")

    // Digital Notice Board & Notification System Routes
    object NoticeBoard : Screen("notice_board")
    object NoticeDetail : Screen("notice_detail/{noticeId}") {
        fun createRoute(noticeId: String) = "notice_detail/$noticeId"
    }
    object NotificationCenter : Screen("notification_center")
    object CreateNotice : Screen("create_notice")
    object NoticeAnalytics : Screen("notice_analytics")

    // Smart QR Code Attendance System Routes
    object StudentAttendance : Screen("student_attendance")
    object StudentQRScanner : Screen("student_qr_scanner")
    object TeacherQRGenerator : Screen("teacher_qr_generator")
    object TeacherAttendanceDashboard : Screen("teacher_attendance_dashboard")
    object AdminAttendanceAnalytics : Screen("admin_attendance_analytics")
    object LeaveRequest : Screen("leave_request")

    // Enterprise Assignment Management System Routes
    object StudentAssignmentDashboard : Screen("student_assignment_dashboard")
    object AssignmentDetail : Screen("assignment_detail/{assignmentId}") {
        fun createRoute(assignmentId: String) = "assignment_detail/$assignmentId"
    }
    object TeacherAssignmentControl : Screen("teacher_assignment_control")
    object AssignmentAnalytics : Screen("assignment_analytics")
    object AssignmentCalendar : Screen("assignment_calendar")

    // Enterprise Result Management System Routes
    object StudentAcademicDashboard : Screen("student_academic_dashboard")
    object TeacherMarkEntry : Screen("teacher_mark_entry")
    object AdminResultPublish : Screen("admin_result_publish")
    object TranscriptView : Screen("transcript_view")
    object ResultAnalytics : Screen("result_analytics")

    // University Financial Management & Fee Payment System Routes
    object StudentFinanceDashboard : Screen("student_finance_dashboard")
    object OnlinePaymentCheckout : Screen("online_payment_checkout/{invoiceId}") {
        fun createRoute(invoiceId: String) = "online_payment_checkout/$invoiceId"
    }
    object PaymentHistoryReceipt : Screen("payment_history_receipt")
    object AdminFinanceAnalytics : Screen("admin_finance_analytics")

    // Enterprise Digital Library & Cloud Storage System Routes
    object DigitalLibraryHome : Screen("digital_library_home")
    object ResourceDetail : Screen("resource_detail/{resourceId}") {
        fun createRoute(resourceId: String) = "resource_detail/$resourceId"
    }
    object PdfReader : Screen("pdf_reader/{resourceId}") {
        fun createRoute(resourceId: String) = "pdf_reader/$resourceId"
    }
    object MediaViewer : Screen("media_viewer/{resourceId}") {
        fun createRoute(resourceId: String) = "media_viewer/$resourceId"
    }
    object DownloadManager : Screen("download_manager")
    object CloudFileManager : Screen("cloud_file_manager")
    object LibraryAnalytics : Screen("library_analytics")

    // Enterprise Online Admission System Routes
    object ApplicantDashboard : Screen("applicant_dashboard")
    object MultiStepAdmissionForm : Screen("multi_step_admission_form")
    object AdmitCardView : Screen("admit_card_view/{applicationId}") {
        fun createRoute(applicationId: String) = "admit_card_view/$applicationId"
    }
    object MeritListView : Screen("merit_list_view")
    object AdmissionAdminPortal : Screen("admission_admin_portal")
    object AdmissionAnalytics : Screen("admission_analytics")

    // Enterprise AI Platform Routes
    object AiChatHub : Screen("ai_chat_hub")
    object AiDashboard : Screen("ai_dashboard")
    object AiStudentAssistant : Screen("ai_student_assistant")
    object AiTeacherAssistant : Screen("ai_teacher_assistant")
    object AiAdminAssistant : Screen("ai_admin_assistant")

    // Digital Identity & Account Management Platform Routes
    object UserProfile : Screen("user_profile")
    object DigitalIdCard : Screen("digital_id_card")
    object QrPassVerification : Screen("qr_pass_verification")
    object SecurityAndDevices : Screen("security_and_devices")
    object AppSettings : Screen("app_settings")

    // Smart Campus Services Platform Routes
    object CampusServicesHub : Screen("campus_services_hub")
    object SmartTransport : Screen("smart_transport")
    object HostelManagement : Screen("hostel_management")
    object EmergencySos : Screen("emergency_sos")
    object MedicalCenter : Screen("medical_center")
    object CampusMap : Screen("campus_map")
    object EventsAndClubs : Screen("events_and_clubs")
    object EcoMonitoring : Screen("eco_monitoring")

    // Career Development Platform Routes
    object CareerDashboard : Screen("career_dashboard")
    object JobsAndInternships : Screen("jobs_and_internships")
    object ResumeBuilder : Screen("resume_builder")
    object AlumniAndMentorship : Screen("alumni_and_mentorship")
    object PlacementAnalytics : Screen("placement_analytics")

    // Enterprise Security & Observability Routes
    object EnterpriseSecurityDashboard : Screen("enterprise_security_dashboard")

    // JSTU Specialized University Modules
    object JstuResearchHub : Screen("jstu_research_hub")
    object ExamControllerPortal : Screen("exam_controller_portal")
    object DiningTokenPass : Screen("dining_token_pass")
}
