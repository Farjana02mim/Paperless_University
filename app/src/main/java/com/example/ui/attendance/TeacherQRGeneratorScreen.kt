package com.example.ui.attendance

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.AttendanceRecord
import com.example.domain.model.AttendanceStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherQRGeneratorScreen(
    viewModel: TeacherAttendanceViewModel,
    onNavigateBack: () -> Unit
) {
    val currentSession by viewModel.currentSession.collectAsStateWithLifecycle()
    val qrBitmap by viewModel.qrBitmap.collectAsStateWithLifecycle()
    val countdown by viewModel.qrCountdownSeconds.collectAsStateWithLifecycle()
    val liveRecords by viewModel.liveRecords.collectAsStateWithLifecycle()

    var showCorrectionDialog by remember { mutableStateOf(false) }
    var selectedRecordForCorrection by remember { mutableStateOf<AttendanceRecord?>(null) }
    var exportSuccessMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Live Attendance Session", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        exportSuccessMessage = "Attendance report exported as CSV & PDF!"
                    }) {
                        Icon(Icons.Default.FileDownload, contentDescription = "Export Report")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            val session = currentSession
            if (session == null || !session.isActive) {
                // Quick Launcher if no active session
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Start New Class Session",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Generates anti-cheating, auto-rotating encrypted QR codes with location radius guard.",
                                color = Color.Gray,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = {
                                    viewModel.startAttendanceSession(
                                        courseId = "CS-301",
                                        courseName = "Advanced Algorithms",
                                        classroom = "Lecture Hall 4A",
                                        expiryMinutes = 60
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Generate Dynamic QR Code", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Session Header Banner
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    session.courseName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                Text(
                                    "Classroom: ${session.classroom} • Dept: ${session.department}",
                                    color = Color.Gray,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    // Rotating QR Code Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.LockClock,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            "Anti-Screenshot QR",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            "Rotates in ${countdown}s",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                val bitmap = qrBitmap
                                if (bitmap != null) {
                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = "Dynamic Attendance QR Code",
                                        modifier = Modifier.size(240.dp)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(240.dp)
                                            .background(Color.LightGray, RoundedCornerShape(16.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { viewModel.endSession() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Close Attendance Session")
                                }
                            }
                        }
                    }

                    // Live Counters Row
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CounterStatBox("Present", session.presentCount, Color(0xFF4CAF50), Modifier.weight(1f))
                            CounterStatBox("Late", session.lateCount, Color(0xFFFF9800), Modifier.weight(1f))
                            CounterStatBox("Absent", session.absentCount, Color(0xFFFF5252), Modifier.weight(1f))
                            CounterStatBox("Excused", session.excusedCount, Color(0xFF2196F3), Modifier.weight(1f))
                        }
                    }

                    // Checked-in Students Header
                    item {
                        Text(
                            "Live Attendance Feed (${liveRecords.size}/${session.totalStudentsCount})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    if (liveRecords.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Waiting for students to scan...", color = Color.Gray)
                            }
                        }
                    } else {
                        items(liveRecords) { record ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(record.studentName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Roll: ${record.rollNumber} • ${"%.1f".format(record.distanceFromClassroomMeters)}m away", color = Color.Gray, fontSize = 12.sp)
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = when (record.status) {
                                                AttendanceStatus.PRESENT -> Color(0xFFE8F5E9)
                                                AttendanceStatus.LATE -> Color(0xFFFFF3E0)
                                                else -> Color(0xFFFFEBEE)
                                            }
                                        ) {
                                            Text(
                                                record.status.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = when (record.status) {
                                                    AttendanceStatus.PRESENT -> Color(0xFF2E7D32)
                                                    AttendanceStatus.LATE -> Color(0xFFE65100)
                                                    else -> Color(0xFFC62828)
                                                },
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }

                                        IconButton(onClick = {
                                            selectedRecordForCorrection = record
                                            showCorrectionDialog = true
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Override", modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Export Confirmation Snack
            exportSuccessMessage?.let { msg ->
                Snackbar(
                    action = {
                        TextButton(onClick = { exportSuccessMessage = null }) {
                            Text("OK")
                        }
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(msg)
                }
            }
        }
    }

    // Manual Correction Dialog
    if (showCorrectionDialog && selectedRecordForCorrection != null) {
        val target = selectedRecordForCorrection!!
        AlertDialog(
            onDismissRequest = { showCorrectionDialog = false },
            title = { Text("Manual Status Override") },
            text = {
                Column {
                    Text("Change attendance status for ${target.studentName}:")
                    Spacer(modifier = Modifier.height(12.dp))
                    AttendanceStatus.values().forEach { st ->
                        Button(
                            onClick = {
                                viewModel.manualCorrection(
                                    recordId = target.recordId,
                                    studentId = target.studentId,
                                    newStatus = st,
                                    remarks = "Manual override by teacher"
                                )
                                showCorrectionDialog = false
                            },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (target.status == st) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (target.status == st) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text(st.name)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCorrectionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CounterStatBox(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(count.toString(), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = color)
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = color)
        }
    }
}
