package com.example.ui.attendance

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.location.LocationVerifier
import com.example.core.security.QRSecurityEngine
import com.example.domain.model.*
import com.example.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class ScanState {
    object Idle : ScanState()
    object Validating : ScanState()
    data class Success(val record: AttendanceRecord) : ScanState()
    data class Error(val message: String) : ScanState()
}

class AttendanceViewModel(
    private val attendanceRepository: AttendanceRepository,
    private val studentId: String = "STU_2026_1001",
    private val studentName: String = "Alex Rivera",
    private val rollNumber: String = "2026-CS-042"
) : ViewModel() {

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    private val _userLocation = MutableStateFlow<Pair<Double, Double>>(Pair(0.0, 0.0))
    val userLocation: StateFlow<Pair<Double, Double>> = _userLocation.asStateFlow()

    val studentSummary: StateFlow<AttendanceSummary> = attendanceRepository
        .getStudentSummary(studentId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AttendanceSummary()
        )

    val attendanceHistory: StateFlow<List<AttendanceRecord>> = attendanceRepository
        .getStudentAttendanceRecords(studentId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val leaveRequests: StateFlow<List<LeaveRequest>> = attendanceRepository
        .getStudentLeaveRequests(studentId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateUserLocation(lat: Double, lng: Double) {
        _userLocation.value = Pair(lat, lng)
    }

    fun processScannedQR(
        rawQrContent: String,
        context: Context
    ) {
        viewModelScope.launch {
            _scanState.value = ScanState.Validating

            // 1. Decode QR payload
            val payload = QRSecurityEngine.decodePayload(rawQrContent)
            if (payload == null) {
                _scanState.value = ScanState.Error("Invalid QR Code payload. Make sure you are scanning the official classroom QR code.")
                return@launch
            }

            // 2. Security & Anti-tamper validation
            val validationResult = QRSecurityEngine.validatePayload(
                payload = payload,
                secretKey = "SmartCampusCampusKey2026",
                maxRotationSeconds = 60
            )

            when (validationResult) {
                is QRSecurityEngine.ValidationResult.Expired -> {
                    _scanState.value = ScanState.Error(validationResult.reason)
                    return@launch
                }
                is QRSecurityEngine.ValidationResult.Tampered -> {
                    _scanState.value = ScanState.Error(validationResult.reason)
                    return@launch
                }
                is QRSecurityEngine.ValidationResult.Invalid -> {
                    _scanState.value = ScanState.Error(validationResult.reason)
                    return@launch
                }
                is QRSecurityEngine.ValidationResult.Valid -> {
                    // Proceed to location check
                }
            }

            // 3. Location Verification Check
            // Simulated default classroom coords if session has not set exact GPS
            val classroomLat = 37.4220
            val classroomLng = -122.0840

            val currentLoc = _userLocation.value
            val currentLat = if (currentLoc.first != 0.0) currentLoc.first else 37.4221
            val currentLng = if (currentLoc.second != 0.0) currentLoc.second else -122.0841

            val locCheck = LocationVerifier.verifyLocation(
                studentLat = currentLat,
                studentLon = currentLng,
                classroomLat = classroomLat,
                classroomLon = classroomLng,
                maxRadiusMeters = 30.0
            )

            val distanceMeters = when (locCheck) {
                is LocationVerifier.LocationCheckResult.Success -> locCheck.distanceMeters
                is LocationVerifier.LocationCheckResult.OutOfRadius -> {
                    _scanState.value = ScanState.Error(locCheck.message)
                    return@launch
                }
                is LocationVerifier.LocationCheckResult.LocationUnavailable -> 0.0
            }

            // 4. Mark Attendance
            val deviceId = QRSecurityEngine.getDeviceId(context)
            val isLate = System.currentTimeMillis() > (payload.timestamp + (15 * 60 * 1000)) // 15 mins late threshold

            val record = AttendanceRecord(
                sessionId = payload.sessionId,
                studentId = studentId,
                studentName = studentName,
                rollNumber = rollNumber,
                courseId = payload.courseId,
                courseName = if (payload.courseId.isNotBlank()) "CS-${payload.courseId}" else "Computer Science",
                status = if (isLate) AttendanceStatus.LATE else AttendanceStatus.PRESENT,
                scannedAt = System.currentTimeMillis(),
                verifiedLatitude = currentLat,
                verifiedLongitude = currentLng,
                distanceFromClassroomMeters = distanceMeters,
                deviceId = deviceId,
                isSynced = true
            )

            val result = attendanceRepository.markAttendance(record)
            result.onSuccess {
                _scanState.value = ScanState.Success(it)
            }.onFailure {
                _scanState.value = ScanState.Error(it.localizedMessage ?: "Failed to record attendance")
            }
        }
    }

    fun submitLeave(leaveType: String, reason: String, startDate: Long, endDate: Long) {
        viewModelScope.launch {
            val req = LeaveRequest(
                studentId = studentId,
                studentName = studentName,
                courseId = "CS-301",
                courseName = "Advanced Algorithms",
                leaveType = leaveType,
                reason = reason,
                startDate = startDate,
                endDate = endDate,
                status = "PENDING",
                createdAt = System.currentTimeMillis()
            )
            attendanceRepository.submitLeaveRequest(req)
        }
    }

    fun resetScanState() {
        _scanState.value = ScanState.Idle
    }
}
