package com.example.domain.model.campus

/**
 * Enterprise Smart Campus Services Platform Models.
 */

// --- Smart Transport ---
data class BusRoute(
    val routeId: String = "RT-101",
    val routeName: String = "Uttara - Campus Express",
    val startLocation: String = "Uttara Sector 11",
    val destination: String = "Main Campus Gate 1",
    val totalStops: Int = 8,
    val regularTime: String = "07:30 AM",
    val busNumber: String = "Dhaka Metro-Cha 11-4092",
    val driverName: String = "Abdul Mannan",
    val driverPhone: String = "+880 1711-998877",
    val availableSeats: Int = 18,
    val totalCapacity: Int = 40,
    val isRunningLive: Boolean = true,
    val currentEstimatedArrivalMinutes: Int = 12
)

data class TransportPass(
    val passId: String = "PASS-9921",
    val userUid: String = "2024-3-60-042",
    val routeName: String = "Uttara - Campus Express",
    val validityEndDate: String = "31 Dec 2024",
    val qrPassPayload: String = "TRANSPORT_PASS|UID:2024-3-60-042|ROUTE:RT-101|STATUS:VALID",
    val feePaid: Boolean = true
)

// --- Hostel Management ---
data class HostelRoomAllocation(
    val allocationId: String = "HAL-302",
    val hostelName: String = "Bijoy Hall (Boys)",
    val roomNumber: String = "402-B",
    val bedNumber: String = "Bed 2",
    val roommateNames: List<String> = listOf("Sajid Rahman", "Naimur Hasan"),
    val monthlyFeeAmount: Double = 3500.0,
    val isFeePaidThisMonth: Boolean = true
)

data class MessMenuItem(
    val dayOfWeek: String = "Today (Friday)",
    val breakfast: String = "Paratha, Egg Curry, Tea",
    val lunch: String = "Chicken Biryani, Salad, Borhani",
    val dinner: String = "Plain Rice, Fish Curry, Dal, Vegetable"
)

data class HostelLeaveRequest(
    val requestId: String = "LR-882",
    val studentName: String = "Tanvir Ahmed",
    val reason: String = "Visiting Home for Weekend",
    val startDate: String = "10 Aug 2026",
    val endDate: String = "12 Aug 2026",
    val status: String = "APPROVED" // PENDING, APPROVED, REJECTED
)

// --- Emergency Alert System ---
enum class EmergencyType {
    SECURITY, MEDICAL, FIRE, DISASTER, GENERAL
}

data class EmergencyAlert(
    val alertId: String = "EMG-001",
    val senderName: String = "Campus Security HQ",
    val alertType: EmergencyType = EmergencyType.GENERAL,
    val title: String = "Severe Weather Alert",
    val message: String = "Heavy rain and thunderstorms expected. Stay indoors or use covered walkways.",
    val timestamp: Long = System.currentTimeMillis(),
    val isBroadcasting: Boolean = true,
    val emergencyHotline: String = "+880 1700-112233"
)

// --- Medical Center ---
data class DoctorSchedule(
    val doctorId: String = "DOC-01",
    val doctorName: String = "Dr. Farhana Yasmin",
    val specialization: String = "General Physician & Campus Medical Officer",
    val visitingHours: String = "09:00 AM - 04:00 PM (Sun-Thu)",
    val roomNumber: String = "Medical Center Room 102",
    val isAvailableToday: Boolean = true
)

data class MedicalAppointment(
    val appointmentId: String = "APT-501",
    val doctorName: String = "Dr. Farhana Yasmin",
    val patientName: String = "Tanvir Ahmed",
    val appointmentDate: String = "08 Aug 2026",
    val appointmentTime: String = "11:30 AM",
    val symptoms: String = "Mild fever & headache",
    val status: String = "CONFIRMED"
)

// --- Campus Map & Buildings ---
data class CampusBuilding(
    val buildingId: String = "BLD-01",
    val buildingName: String = "Main Academic Building",
    val category: String = "ACADEMIC", // ACADEMIC, HOSTEL, CAFETERIA, LIBRARY, MEDICAL, SPORTS
    val totalFloors: Int = 10,
    val prominentLabsAndDepts: List<String> = listOf("CSE Department (5th Fl)", "AI & Robotics Lab (6th Fl)", "Auditorium (2nd Fl)"),
    val latitude: Double = 23.78088,
    val longitude: Double = 90.41924,
    val openHours: String = "07:30 AM - 08:30 PM"
)

// --- Event & Club Management ---
data class CampusEvent(
    val eventId: String = "EVT-2024-01",
    val title: String = "National Paperless Tech Hackathon 2026",
    val organizer: String = "Computer Club & IT Division",
    val date: String = "15-16 August 2026",
    val venue: String = "Central Auditorium & Computer Labs",
    val description: String = "48-hour continuous hackathon focused on paperless campus innovations & AI applications.",
    val isRegistered: Boolean = true,
    val ticketQrToken: String = "EVENT_TICKET|EVT:EVT-2024-01|USER:2024-3-60-042"
)

data class CampusClub(
    val clubId: String = "CLUB-01",
    val clubName: String = "Smart Campus Robotics & AI Club",
    val category: String = "TECHNOLOGY",
    val totalMembers: Int = 340,
    val leadName: String = "Prof. Dr. Mahfuzur Rahman",
    val description: String = "Empowering students in autonomous robotics, Machine Learning, and embedded IoT systems.",
    val isMember: Boolean = true
)

// --- Help Desk & Maintenance ---
data class SupportTicket(
    val ticketId: String = "TCK-8812",
    val category: String = "HOSTEL_MAINTENANCE", // ELECTRICAL, INTERNET, FURNITURE, ACADEMIC
    val subject: String = "Room 402 Wi-Fi Router Connection Intermittent",
    val description: String = "Signal drops every 15 minutes during evening study hours.",
    val priority: String = "HIGH", // LOW, NORMAL, HIGH, URGENT
    val status: String = "IN_PROGRESS", // OPEN, IN_PROGRESS, RESOLVED, CLOSED
    val createdDate: String = "06 Aug 2026"
)

// --- Eco Monitoring ---
data class EcoSustainabilityStats(
    val totalSheetsSaved: Long = 1420500L,
    val estimatedTreesSaved: Int = 170,
    val carbonDioxideReductionKg: Double = 11364.0,
    val userPersonalScore: Int = 94, // 0 to 100
    val personalDigitalTransfers: Int = 184
)
