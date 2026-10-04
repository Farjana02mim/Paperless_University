package com.example.ui.attendance

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.security.QRGenerator
import com.example.core.security.QRSecurityEngine
import com.example.domain.model.*
import com.example.domain.repository.AttendanceRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class TeacherAttendanceViewModel(
    private val attendanceRepository: AttendanceRepository,
    private val teacherId: String = "TCH_101",
    private val teacherName: String = "Dr. Robert Vance"
) : ViewModel() {

    private val _currentSession = MutableStateFlow<AttendanceSession?>(null)
    val currentSession: StateFlow<AttendanceSession?> = _currentSession.asStateFlow()

    private val _qrBitmap = MutableStateFlow<Bitmap?>(null)
    val qrBitmap: StateFlow<Bitmap?> = _qrBitmap.asStateFlow()

    private val _qrCountdownSeconds = MutableStateFlow(60)
    val qrCountdownSeconds: StateFlow<Int> = _qrCountdownSeconds.asStateFlow()

    private val _liveRecords = MutableStateFlow<List<AttendanceRecord>>(emptyList())
    val liveRecords: StateFlow<List<AttendanceRecord>> = _liveRecords.asStateFlow()

    val activeSessions: StateFlow<List<AttendanceSession>> = attendanceRepository
        .getActiveSessionsByTeacher(teacherId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var rotationJob: kotlinx.coroutines.Job? = null

    fun startAttendanceSession(
        courseId: String,
        courseName: String,
        classroom: String = "Lab 302",
        expiryMinutes: Int = 60
    ) {
        viewModelScope.launch {
            val sessionId = UUID.randomUUID().toString()
            val now = System.currentTimeMillis()
            val session = AttendanceSession(
                sessionId = sessionId,
                courseId = courseId,
                courseName = courseName,
                teacherId = teacherId,
                teacherName = teacherName,
                department = "Computer Science",
                semester = "Semester 6",
                classroom = classroom,
                latitude = 37.4220,
                longitude = -122.0840,
                allowedRadiusMeters = 30.0,
                startTime = now,
                expireTime = now + (expiryMinutes * 60 * 1000L),
                qrRotationSeconds = 60,
                secretKey = "SmartCampusCampusKey2026",
                isActive = true,
                totalStudentsCount = 45,
                presentCount = 0,
                lateCount = 0,
                absentCount = 45,
                excusedCount = 0
            )

            attendanceRepository.createSession(session)
            _currentSession.value = session

            // Observe live records
            observeSessionRecords(sessionId)

            // Start QR code rotation loop
            startQrRotationLoop(session)
        }
    }

    private fun observeSessionRecords(sessionId: String) {
        viewModelScope.launch {
            attendanceRepository.getSessionAttendanceRecords(sessionId).collect { records ->
                _liveRecords.value = records
                _currentSession.value?.let { sess ->
                    val present = records.count { it.status == AttendanceStatus.PRESENT }
                    val late = records.count { it.status == AttendanceStatus.LATE }
                    val excused = records.count { it.status == AttendanceStatus.EXCUSED || it.status == AttendanceStatus.MEDICAL_LEAVE }
                    val absent = (sess.totalStudentsCount - (present + late + excused)).coerceAtLeast(0)

                    _currentSession.value = sess.copy(
                        presentCount = present,
                        lateCount = late,
                        absentCount = absent,
                        excusedCount = excused
                    )
                }
            }
        }
    }

    private fun startQrRotationLoop(session: AttendanceSession) {
        rotationJob?.cancel()
        rotationJob = viewModelScope.launch {
            var counter = 60
            while (session.isActive) {
                if (counter <= 0 || _qrBitmap.value == null) {
                    counter = 60
                    // Regenerate dynamic encrypted token payload
                    val timestamp = System.currentTimeMillis()
                    val signature = QRSecurityEngine.generateSignature(
                        sessionId = session.sessionId,
                        timestamp = timestamp,
                        secretKey = session.secretKey
                    )

                    val payload = QRCodePayload(
                        sessionId = session.sessionId,
                        courseId = session.courseId,
                        teacherId = session.teacherId,
                        department = session.department,
                        semester = session.semester,
                        classroom = session.classroom,
                        timestamp = timestamp,
                        expiresAt = timestamp + (60 * 1000L),
                        encryptedToken = UUID.randomUUID().toString(),
                        digitalSignature = signature
                    )

                    val encoded = QRSecurityEngine.encodePayload(payload)
                    _qrBitmap.value = QRGenerator.generateQRCodeBitmap(encoded, 600, 600)
                }

                _qrCountdownSeconds.value = counter
                delay(1000L)
                counter--
            }
        }
    }

    fun manualCorrection(recordId: String, studentId: String, newStatus: AttendanceStatus, remarks: String) {
        viewModelScope.launch {
            _currentSession.value?.sessionId?.let { sId ->
                attendanceRepository.manualAttendanceCorrection(
                    recordId = recordId,
                    sessionId = sId,
                    studentId = studentId,
                    newStatus = newStatus,
                    remarks = remarks
                )
            }
        }
    }

    fun endSession() {
        viewModelScope.launch {
            _currentSession.value?.let { sess ->
                attendanceRepository.closeSession(sess.sessionId)
                rotationJob?.cancel()
                _currentSession.value = sess.copy(isActive = false)
            }
        }
    }
}
