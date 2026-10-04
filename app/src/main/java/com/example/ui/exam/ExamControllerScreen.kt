package com.example.ui.exam

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ExamAdmitCard(
    val examTitle: String = "B.Sc. (Engg.) 6th Semester Final Examination - 2025",
    val studentName: String = "Tanvir Ahmed",
    val studentRoll: String = "20-42101-1",
    val registrationNo: String = "JSTU-REG-2020-0482",
    val department: String = "Computer Science & Engineering",
    val hallName: String = "Bangabandhu Sheikh Mujibur Rahman Hall",
    val examCenter: String = "Academic Building 1, JSTU Main Campus",
    val qrPassCode: String = "JSTU-EXAM-2025-SEMESTER6-PASS-882910",
    val isClearanceApproved: Boolean = true,
    val courses: List<ExamCourseSchedule> = listOf(
        ExamCourseSchedule("CSE3201", "Design & Analysis of Algorithms", "15 Feb 2025", "10:00 AM - 01:00 PM", "Room 402"),
        ExamCourseSchedule("CSE3203", "Software Engineering & Architecture", "18 Feb 2025", "10:00 AM - 01:00 PM", "Room 405"),
        ExamCourseSchedule("CSE3205", "Database Systems & Data Warehousing", "22 Feb 2025", "10:00 AM - 01:00 PM", "Room 402"),
        ExamCourseSchedule("CSE3207", "Computer Networks & Security", "26 Feb 2025", "10:00 AM - 01:00 PM", "Lab 2")
    )
)

data class ExamCourseSchedule(
    val courseCode: String,
    val courseTitle: String,
    val examDate: String,
    val examTime: String,
    val roomNo: String
)

class ExamControllerViewModel : ViewModel() {
    private val _admitCard = MutableStateFlow(ExamAdmitCard())
    val admitCard: StateFlow<ExamAdmitCard> = _admitCard.asStateFlow()

    private val _cgpaTarget = MutableStateFlow(3.82)
    val cgpaTarget: StateFlow<Double> = _cgpaTarget.asStateFlow()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamControllerScreen(
    viewModel: ExamControllerViewModel = remember { ExamControllerViewModel() },
    onBackClick: () -> Unit
) {
    val admitCard by viewModel.admitCard.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0: Admit Card, 1: Schedule, 2: CGPA & UGC Seals

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Controller of Examinations", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Print / Download PDF Admit Card */ }) {
                        Icon(Icons.Default.Print, contentDescription = "Print Admit Card")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Tab Row
            PrimaryTabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Admit Card", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Badge, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Routine", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Event, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("UGC CGPA", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Verified, contentDescription = null) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> AdmitCardView(admitCard)
                1 -> ExamRoutineView(admitCard.courses)
                2 -> UgcCgpaCalculatorView()
            }
        }
    }
}

@Composable
fun AdmitCardView(admitCard: ExamAdmitCard) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Official University Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "JAMALPUR SCIENCE & TECHNOLOGY UNIVERSITY",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "OFFICE OF THE CONTROLLER OF EXAMINATIONS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE8F5E9)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(12.dp))
                        Text("CLEARED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                admitCard.examTitle,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Student Information Grid
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                AdmitInfoRow("Student Name:", admitCard.studentName)
                AdmitInfoRow("Roll / Student ID:", admitCard.studentRoll)
                AdmitInfoRow("Registration No:", admitCard.registrationNo)
                AdmitInfoRow("Department:", admitCard.department)
                AdmitInfoRow("Residential Hall:", admitCard.hallName)
                AdmitInfoRow("Exam Center:", admitCard.examCenter)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // QR Pass Invigilator Code
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                    Column {
                        Text(
                            "Digital Invigilator Security Verification",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Text(
                            admitCard.qrPassCode,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdmitInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun ExamRoutineView(courses: List<ExamCourseSchedule>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(courses) { course ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Event, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("${course.courseCode}: ${course.courseTitle}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text("Date: ${course.examDate} (${course.examTime})", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        Text("Venue: ${course.roomNo}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun UgcCgpaCalculatorView() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color(0xFF2E7D32))
                Text("UGC Bangladesh Grading Scale Standard", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            GradeScaleRow("80% and above", "A+", "4.00")
            GradeScaleRow("75% to <80%", "A", "3.75")
            GradeScaleRow("70% to <75%", "A-", "3.50")
            GradeScaleRow("65% to <70%", "B+", "3.25")
            GradeScaleRow("60% to <65%", "B", "3.00")
            GradeScaleRow("55% to <60%", "B-", "2.75")
            GradeScaleRow("50% to <55%", "C+", "2.50")
            GradeScaleRow("45% to <50%", "C", "2.25")
            GradeScaleRow("40% to <45%", "D", "2.00")
            GradeScaleRow("< 40%", "F", "0.00")
        }
    }
}

@Composable
fun GradeScaleRow(range: String, letter: String, point: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(range, fontSize = 11.sp)
        Text(letter, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text("Grade Point: $point", fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
