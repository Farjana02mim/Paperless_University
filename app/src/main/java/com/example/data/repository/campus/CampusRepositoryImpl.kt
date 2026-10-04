package com.example.data.repository.campus

import com.example.data.local.campus.CampusDao
import com.example.data.local.campus.EmergencyAlertEntity
import com.example.data.local.campus.HostelLeaveRequestEntity
import com.example.data.local.campus.SupportTicketEntity
import com.example.domain.model.campus.*
import com.example.domain.repository.campus.CampusRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class CampusRepositoryImpl(
    private val campusDao: CampusDao,
    private val firestore: FirebaseFirestore?
) : CampusRepository {

    override fun getBusRoutes(): Flow<List<BusRoute>> = flow {
        val mockRoutes = listOf(
            BusRoute(
                routeId = "RT-101",
                routeName = "Uttara Express (Route 1)",
                startLocation = "Uttara Sector 11",
                destination = "Main Campus Gate 1",
                totalStops = 8,
                regularTime = "07:30 AM",
                busNumber = "Dhaka Metro-Cha 11-4092",
                driverName = "Abdul Mannan",
                driverPhone = "+880 1711-998877",
                availableSeats = 18,
                totalCapacity = 40,
                isRunningLive = true,
                currentEstimatedArrivalMinutes = 12
            ),
            BusRoute(
                routeId = "RT-102",
                routeName = "Dhanmondi Shuttle (Route 2)",
                startLocation = "Dhanmondi 27",
                destination = "Main Campus Gate 2",
                totalStops = 6,
                regularTime = "08:00 AM",
                busNumber = "Dhaka Metro-Cha 14-8821",
                driverName = "Kabir Hossain",
                driverPhone = "+880 1822-334455",
                availableSeats = 5,
                totalCapacity = 40,
                isRunningLive = true,
                currentEstimatedArrivalMinutes = 25
            ),
            BusRoute(
                routeId = "RT-103",
                routeName = "Mirpur Circular (Route 3)",
                startLocation = "Mirpur 10 Circle",
                destination = "Main Campus Bus Stop",
                totalStops = 10,
                regularTime = "07:15 AM",
                busNumber = "Dhaka Metro-Cha 15-9922",
                driverName = "Shahadat Hossain",
                driverPhone = "+880 1912-667788",
                availableSeats = 22,
                totalCapacity = 40,
                isRunningLive = false,
                currentEstimatedArrivalMinutes = 0
            )
        )
        emit(mockRoutes)
    }

    override fun getTransportPass(): Flow<TransportPass> = flow {
        emit(
            TransportPass(
                passId = "PASS-2026-8812",
                userUid = "2024-3-60-042",
                routeName = "Uttara Express (Route 1)",
                validityEndDate = "31 December 2026",
                qrPassPayload = "SMART_CAMPUS_TRANSPORT|UID:2024-3-60-042|PASS:PASS-2026-8812|EXP:2026-12-31",
                feePaid = true
            )
        )
    }

    override fun getHostelAllocation(): Flow<HostelRoomAllocation> = flow {
        emit(
            HostelRoomAllocation(
                allocationId = "HAL-402",
                hostelName = "Bijoy Hall (Boys Wing)",
                roomNumber = "402-B",
                bedNumber = "Bed 02",
                roommateNames = listOf("Sajid Rahman (CSE)", "Naimur Hasan (EEE)"),
                monthlyFeeAmount = 3500.0,
                isFeePaidThisMonth = true
            )
        )
    }

    override fun getMessMenu(): Flow<MessMenuItem> = flow {
        emit(
            MessMenuItem(
                dayOfWeek = "Today (Friday Special)",
                breakfast = "Paratha, Egg Fry, Mixed Dal, Milk Tea",
                lunch = "Chicken Kacchi Biryani, Roast, Cucumber Salad, Borhani",
                dinner = "Plain Fine Rice, Rui Fish Curry, Fried Eggplant, Dal"
            )
        )
    }

    override fun getHostelLeaveRequests(): Flow<List<HostelLeaveRequest>> = campusDao.getLeaveRequests().map { entities ->
        if (entities.isEmpty()) {
            listOf(
                HostelLeaveRequest(
                    requestId = "LR-901",
                    studentName = "Tanvir Ahmed",
                    reason = "Visiting Home for Family Event",
                    startDate = "10 Aug 2026",
                    endDate = "13 Aug 2026",
                    status = "APPROVED"
                )
            )
        } else {
            entities.map {
                HostelLeaveRequest(
                    requestId = it.requestId,
                    studentName = it.studentName,
                    reason = it.reason,
                    startDate = it.startDate,
                    endDate = it.endDate,
                    status = it.status
                )
            }
        }
    }

    override suspend fun submitLeaveRequest(reason: String, startDate: String, endDate: String) {
        val req = HostelLeaveRequestEntity(
            requestId = "LR-" + System.currentTimeMillis().toString().takeLast(4),
            studentName = "Tanvir Ahmed",
            reason = reason,
            startDate = startDate,
            endDate = endDate,
            status = "PENDING"
        )
        campusDao.insertLeaveRequest(req)
    }

    override fun getEmergencyAlerts(): Flow<List<EmergencyAlert>> = campusDao.getEmergencyAlerts().map { entities ->
        if (entities.isEmpty()) {
            listOf(
                EmergencyAlert(
                    alertId = "EMG-01",
                    senderName = "Campus Security HQ",
                    alertType = EmergencyType.GENERAL,
                    title = "Monsoon Safety Guidance",
                    message = "Heavy rain expected around campus gates. Use covered overbridge walkway.",
                    timestamp = System.currentTimeMillis() - 3600000,
                    isBroadcasting = true,
                    emergencyHotline = "+880 1700-998877"
                )
            )
        } else {
            entities.map {
                EmergencyAlert(
                    alertId = it.alertId,
                    senderName = it.senderName,
                    alertType = EmergencyType.valueOf(it.alertType),
                    title = it.title,
                    message = it.message,
                    timestamp = it.timestamp,
                    isBroadcasting = it.isBroadcasting,
                    emergencyHotline = it.emergencyHotline
                )
            }
        }
    }

    override suspend fun triggerOneTapSos(latitude: Double, longitude: Double): Boolean {
        val alert = EmergencyAlertEntity(
            alertId = "SOS-" + System.currentTimeMillis().toString().takeLast(4),
            senderName = "Student SOS Trigger (Tanvir Ahmed)",
            alertType = "SECURITY",
            title = "CRITICAL STUDENT SOS BROADCAST",
            message = "Immediate assistance required near Lat: %.4f, Lon: %.4f".format(latitude, longitude),
            timestamp = System.currentTimeMillis(),
            isBroadcasting = true,
            emergencyHotline = "+880 1700-998877"
        )
        campusDao.insertEmergencyAlert(alert)
        return true
    }

    override fun getDoctorSchedules(): Flow<List<DoctorSchedule>> = flow {
        emit(
            listOf(
                DoctorSchedule(
                    doctorId = "DOC-101",
                    doctorName = "Dr. Farhana Yasmin",
                    specialization = "Senior Medical Officer & General Practitioner",
                    visitingHours = "09:00 AM - 04:00 PM (Sun-Thu)",
                    roomNumber = "Medical Center Room 101",
                    isAvailableToday = true
                ),
                DoctorSchedule(
                    doctorId = "DOC-102",
                    doctorName = "Dr. Kazi Ariful Islam",
                    specialization = "Campus Psychiatrist & Mental Health Specialist",
                    visitingHours = "02:00 PM - 06:00 PM (Mon, Wed, Fri)",
                    roomNumber = "Medical Center Room 104",
                    isAvailableToday = true
                )
            )
        )
    }

    override fun getMedicalAppointments(): Flow<List<MedicalAppointment>> = flow {
        emit(
            listOf(
                MedicalAppointment(
                    appointmentId = "APT-301",
                    doctorName = "Dr. Farhana Yasmin",
                    patientName = "Tanvir Ahmed",
                    appointmentDate = "08 Aug 2026",
                    appointmentTime = "11:30 AM",
                    symptoms = "Routine Health Checkup & Eye Strain",
                    status = "CONFIRMED"
                )
            )
        )
    }

    override suspend fun bookMedicalAppointment(
        doctorName: String,
        date: String,
        time: String,
        symptoms: String
    ) {
        // Mock persistence or Firestore push
    }

    override fun getCampusBuildings(): Flow<List<CampusBuilding>> = flow {
        emit(
            listOf(
                CampusBuilding(
                    buildingId = "BLD-01",
                    buildingName = "Main Academic Building",
                    category = "ACADEMIC",
                    totalFloors = 10,
                    prominentLabsAndDepts = listOf("Computer Science Dept (Fl 5)", "AI & Robotics Lab (Fl 6)", "Central Dean Office (Fl 1)"),
                    latitude = 23.78088,
                    longitude = 90.41924,
                    openHours = "07:30 AM - 08:30 PM"
                ),
                CampusBuilding(
                    buildingId = "BLD-02",
                    buildingName = "Central Library & Research Hub",
                    category = "LIBRARY",
                    totalFloors = 5,
                    prominentLabsAndDepts = listOf("Paperless Digital Library (Fl 2)", "Graduate Reading Zone (Fl 4)"),
                    latitude = 23.78120,
                    longitude = 90.41980,
                    openHours = "08:00 AM - 10:00 PM"
                ),
                CampusBuilding(
                    buildingId = "BLD-03",
                    buildingName = "Bijoy Hall (Boys Hostel)",
                    category = "HOSTEL",
                    totalFloors = 8,
                    prominentLabsAndDepts = listOf("Hostel Provost Office", "Student Dining Hall"),
                    latitude = 23.78200,
                    longitude = 90.42050,
                    openHours = "24 Hours (Gate curfew 10:30 PM)"
                ),
                CampusBuilding(
                    buildingId = "BLD-04",
                    buildingName = "Campus Medical Center",
                    category = "MEDICAL",
                    totalFloors = 2,
                    prominentLabsAndDepts = listOf("24/7 Emergency Ward", "Doctor Consultation Rooms"),
                    latitude = 23.78050,
                    longitude = 90.41890,
                    openHours = "24 Hours Open"
                )
            )
        )
    }

    override fun getCampusEvents(): Flow<List<CampusEvent>> = flow {
        emit(
            listOf(
                CampusEvent(
                    eventId = "EVT-2026-01",
                    title = "National Paperless Smart Campus Hackathon 2026",
                    organizer = "Computer Club & IT Division",
                    date = "15-16 August 2026",
                    venue = "Central Auditorium & Computer Labs",
                    description = "48-hour continuous innovation hackathon focusing on paperless campus solutions and local AI engines.",
                    isRegistered = true,
                    ticketQrToken = "EVENT_TICKET|EVT:EVT-2026-01|USER:2024-3-60-042"
                ),
                CampusEvent(
                    eventId = "EVT-2026-02",
                    title = "Annual Inter-Department Football Championship",
                    organizer = "University Sports Club",
                    date = "22 August 2026",
                    venue = "Central Playground",
                    description = "Join the biggest sports tournament of the semester. Free entry for all students.",
                    isRegistered = false,
                    ticketQrToken = ""
                )
            )
        )
    }

    override fun getCampusClubs(): Flow<List<CampusClub>> = flow {
        emit(
            listOf(
                CampusClub(
                    clubId = "CLUB-01",
                    clubName = "Smart Campus AI & Robotics Club",
                    category = "TECHNOLOGY",
                    totalMembers = 340,
                    leadName = "Prof. Dr. Mahfuzur Rahman",
                    description = "Researching robotics, embedded IoT, and computer vision for smart campus mobility.",
                    isMember = true
                ),
                CampusClub(
                    clubId = "CLUB-02",
                    clubName = "Paperless Green Eco Club",
                    category = "ENVIRONMENT",
                    totalMembers = 210,
                    leadName = "Dr. Nazmul Huda",
                    description = "Promoting zero-paper initiatives, campus tree plantation, and waste recycling.",
                    isMember = true
                )
            )
        )
    }

    override suspend fun registerForEvent(eventId: String): Boolean = true

    override fun getSupportTickets(): Flow<List<SupportTicket>> = campusDao.getSupportTickets().map { entities ->
        if (entities.isEmpty()) {
            listOf(
                SupportTicket(
                    ticketId = "TCK-8812",
                    category = "HOSTEL_MAINTENANCE",
                    subject = "Room 402 Wi-Fi Router Intermittent Signal",
                    description = "Signal drops frequently during peak study hours.",
                    priority = "HIGH",
                    status = "IN_PROGRESS",
                    createdDate = "06 Aug 2026"
                )
            )
        } else {
            entities.map {
                SupportTicket(
                    ticketId = it.ticketId,
                    category = it.category,
                    subject = it.subject,
                    description = it.description,
                    priority = it.priority,
                    status = it.status,
                    createdDate = it.createdDate
                )
            }
        }
    }

    override suspend fun submitSupportTicket(
        category: String,
        subject: String,
        description: String,
        priority: String
    ) {
        val ticket = SupportTicketEntity(
            ticketId = "TCK-" + System.currentTimeMillis().toString().takeLast(4),
            category = category,
            subject = subject,
            description = description,
            priority = priority,
            status = "OPEN",
            createdDate = "07 Aug 2026"
        )
        campusDao.insertSupportTicket(ticket)
    }

    override fun getEcoStats(): Flow<EcoSustainabilityStats> = flow {
        emit(
            EcoSustainabilityStats(
                totalSheetsSaved = 1420500L,
                estimatedTreesSaved = 170,
                carbonDioxideReductionKg = 11364.0,
                userPersonalScore = 96,
                personalDigitalTransfers = 184
            )
        )
    }
}
