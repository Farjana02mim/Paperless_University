package com.example.ui.campus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.campus.*
import com.example.domain.repository.campus.CampusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CampusServicesViewModel(
    private val campusRepository: CampusRepository
) : ViewModel() {

    private val _busRoutes = MutableStateFlow<List<BusRoute>>(emptyList())
    val busRoutes: StateFlow<List<BusRoute>> = _busRoutes.asStateFlow()

    private val _transportPass = MutableStateFlow<TransportPass?>(null)
    val transportPass: StateFlow<TransportPass?> = _transportPass.asStateFlow()

    private val _hostelAllocation = MutableStateFlow<HostelRoomAllocation?>(null)
    val hostelAllocation: StateFlow<HostelRoomAllocation?> = _hostelAllocation.asStateFlow()

    private val _messMenu = MutableStateFlow<MessMenuItem?>(null)
    val messMenu: StateFlow<MessMenuItem?> = _messMenu.asStateFlow()

    private val _leaveRequests = MutableStateFlow<List<HostelLeaveRequest>>(emptyList())
    val leaveRequests: StateFlow<List<HostelLeaveRequest>> = _leaveRequests.asStateFlow()

    private val _emergencyAlerts = MutableStateFlow<List<EmergencyAlert>>(emptyList())
    val emergencyAlerts: StateFlow<List<EmergencyAlert>> = _emergencyAlerts.asStateFlow()

    private val _doctors = MutableStateFlow<List<DoctorSchedule>>(emptyList())
    val doctors: StateFlow<List<DoctorSchedule>> = _doctors.asStateFlow()

    private val _medicalAppointments = MutableStateFlow<List<MedicalAppointment>>(emptyList())
    val medicalAppointments: StateFlow<List<MedicalAppointment>> = _medicalAppointments.asStateFlow()

    private val _buildings = MutableStateFlow<List<CampusBuilding>>(emptyList())
    val buildings: StateFlow<List<CampusBuilding>> = _buildings.asStateFlow()

    private val _events = MutableStateFlow<List<CampusEvent>>(emptyList())
    val events: StateFlow<List<CampusEvent>> = _events.asStateFlow()

    private val _clubs = MutableStateFlow<List<CampusClub>>(emptyList())
    val clubs: StateFlow<List<CampusClub>> = _clubs.asStateFlow()

    private val _supportTickets = MutableStateFlow<List<SupportTicket>>(emptyList())
    val supportTickets: StateFlow<List<SupportTicket>> = _supportTickets.asStateFlow()

    private val _ecoStats = MutableStateFlow<EcoSustainabilityStats?>(null)
    val ecoStats: StateFlow<EcoSustainabilityStats?> = _ecoStats.asStateFlow()

    private val _uiNotice = MutableStateFlow<String?>(null)
    val uiNotice: StateFlow<String?> = _uiNotice.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            campusRepository.getBusRoutes().collect { _busRoutes.value = it }
        }
        viewModelScope.launch {
            campusRepository.getTransportPass().collect { _transportPass.value = it }
        }
        viewModelScope.launch {
            campusRepository.getHostelAllocation().collect { _hostelAllocation.value = it }
        }
        viewModelScope.launch {
            campusRepository.getMessMenu().collect { _messMenu.value = it }
        }
        viewModelScope.launch {
            campusRepository.getHostelLeaveRequests().collect { _leaveRequests.value = it }
        }
        viewModelScope.launch {
            campusRepository.getEmergencyAlerts().collect { _emergencyAlerts.value = it }
        }
        viewModelScope.launch {
            campusRepository.getDoctorSchedules().collect { _doctors.value = it }
        }
        viewModelScope.launch {
            campusRepository.getMedicalAppointments().collect { _medicalAppointments.value = it }
        }
        viewModelScope.launch {
            campusRepository.getCampusBuildings().collect { _buildings.value = it }
        }
        viewModelScope.launch {
            campusRepository.getCampusEvents().collect { _events.value = it }
        }
        viewModelScope.launch {
            campusRepository.getCampusClubs().collect { _clubs.value = it }
        }
        viewModelScope.launch {
            campusRepository.getSupportTickets().collect { _supportTickets.value = it }
        }
        viewModelScope.launch {
            campusRepository.getEcoStats().collect { _ecoStats.value = it }
        }
    }

    fun submitHostelLeave(reason: String, startDate: String, endDate: String) {
        viewModelScope.launch {
            campusRepository.submitLeaveRequest(reason, startDate, endDate)
            _uiNotice.value = "Hostel leave request submitted successfully."
        }
    }

    fun triggerOneTapSos() {
        viewModelScope.launch {
            campusRepository.triggerOneTapSos(23.78088, 90.41924)
            _uiNotice.value = "EMERGENCY SOS ALERT BROADCASTED TO CAMPUS SECURITY HQ!"
        }
    }

    fun submitSupportTicket(category: String, subject: String, description: String, priority: String) {
        viewModelScope.launch {
            campusRepository.submitSupportTicket(category, subject, description, priority)
            _uiNotice.value = "Help desk support ticket created successfully."
        }
    }

    fun clearNotice() {
        _uiNotice.value = null
    }
}
