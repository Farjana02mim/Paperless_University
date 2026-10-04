package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import com.example.data.local.admission.AdmissionDao
import com.example.data.local.admission.AdmissionApplicationEntity
import com.example.data.local.admission.AdmissionSessionEntity
import com.example.data.local.ai.*
import com.example.data.local.campus.*
import com.example.data.local.dao.career.CareerDao
import com.example.data.local.dao.audit.AuditLogDao
import com.example.data.local.entity.audit.AuditLogEntity
import com.example.data.local.entity.career.JobApplicationEntity
import com.example.data.local.entity.career.JobPostingEntity
import com.example.data.local.entity.career.ResumeProfileEntity
import com.example.data.local.finance.FinanceDao
import com.example.data.local.finance.InvoiceEntity
import com.example.data.local.finance.PaymentEntity
import com.example.data.local.identity.*
import com.example.data.local.library.*
import com.example.data.local.result.CourseResultEntity
import com.example.data.local.result.ResultDao
import com.example.data.local.result.SemesterResultEntity

@Database(
    entities = [
        NoticeEntity::class,
        ScheduleEntity::class,
        AssignmentEntity::class,
        NotificationEntity::class,
        CourseEntity::class,
        LibraryEntity::class,
        NoticeBoardEntity::class,
        NoticeBookmarkEntity::class,
        NoticeReadEntity::class,
        NotificationFcmEntity::class,
        LocalAttendanceRecordEntity::class,
        LocalLeaveRequestEntity::class,
        AssignmentSubmissionEntity::class,
        EnterpriseAssignmentEntity::class,
        CourseResultEntity::class,
        SemesterResultEntity::class,
        InvoiceEntity::class,
        PaymentEntity::class,
        EnterpriseLibraryResourceEntity::class,
        DownloadEntity::class,
        BookmarkEntity::class,
        ReadingHistoryEntity::class,
        CloudFolderEntity::class,
        CloudFileEntity::class,
        AdmissionSessionEntity::class,
        AdmissionApplicationEntity::class,
        AiChatMessageEntity::class,
        ConversationSessionEntity::class,
        AiRecommendationEntity::class,
        PendingAiRequestEntity::class,
        UserProfileEntity::class,
        DigitalIdCardEntity::class,
        UserDeviceEntity::class,
        LoginHistoryEntity::class,
        UserSettingsEntity::class,
        BusRouteEntity::class,
        HostelLeaveRequestEntity::class,
        EmergencyAlertEntity::class,
        SupportTicketEntity::class,
        JobPostingEntity::class,
        JobApplicationEntity::class,
        ResumeProfileEntity::class,
        AuditLogEntity::class
    ],
    version = 13,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun noticeBoardDao(): NoticeBoardDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun assignmentDao(): AssignmentDao
    abstract fun resultDao(): ResultDao
    abstract fun financeDao(): FinanceDao
    abstract fun libraryDao(): LibraryDao
    abstract fun admissionDao(): AdmissionDao
    abstract fun aiDao(): AiDao
    abstract fun identityDao(): IdentityDao
    abstract fun campusDao(): CampusDao
    abstract fun careerDao(): CareerDao
    abstract fun auditLogDao(): AuditLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_campus_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
