package com.example.domain.repository.campus

import com.example.domain.model.campus.*
import kotlinx.coroutines.flow.Flow

interface CampusRepository {
    fun getBusRoutes(): Flow<List<BusRoute>>
    fun getTransportPass(): Flow<TransportPass>
    
    fun getHostelAllocation(): Flow<HostelRoomAllocation>
    fun getMessMenu(): Flow<MessMenuItem>
    fun getHostelLeaveRequests(): Flow<List<HostelLeaveRequest>>
    suspend fun submitLeaveRequest(reason: String, startDate: String, endDate: String)
    
    fun getEmergencyAlerts(): Flow<List<EmergencyAlert>>
    suspend fun triggerOneTapSos(latitude: Double, longitude: Double): Boolean
    
    fun getDoctorSchedules(): Flow<List<DoctorSchedule>>
    fun getMedicalAppointments(): Flow<List<MedicalAppointment>>
    suspend fun bookMedicalAppointment(doctorName: String, date: String, time: String, symptoms: String)
    
    fun getCampusBuildings(): Flow<List<CampusBuilding>>
    
    fun getCampusEvents(): Flow<List<CampusEvent>>
    fun getCampusClubs(): Flow<List<CampusClub>>
    suspend fun registerForEvent(eventId: String): Boolean
    
    fun getSupportTickets(): Flow<List<SupportTicket>>
    suspend fun submitSupportTicket(category: String, subject: String, description: String, priority: String)
    
    fun getEcoStats(): Flow<EcoSustainabilityStats>
}
