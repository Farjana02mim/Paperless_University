package com.example.ui.teacher.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.domain.model.teacher.*
import com.example.ui.theme.SecondaryTeal

@Composable
fun QrAttendanceGeneratorDialog(
    classItem: ClassScheduleItem?,
    onDismiss: () -> Unit,
    onComplete: (String) -> Unit
) {
    var timerSeconds by remember { mutableIntStateOf(180) }
    var presentCount by remember { mutableIntStateOf(32) }

    LaunchedEffect(Unit) {
        while (timerSeconds > 0) {
            kotlinx.coroutines.delay(1000L)
            timerSeconds--
            if (timerSeconds % 5 == 0 && presentCount < (classItem?.studentCount ?: 45)) {
                presentCount++
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Live Class QR Attendance", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "${classItem?.courseCode} - ${classItem?.courseName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Room: ${classItem?.roomNumber} | Section: ${classItem?.section}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Simulated QR Code Frame
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = "QR Code",
                            modifier = Modifier.size(140.dp),
                            tint = Color.Black
                        )
                        Text("Scan in Smart Campus App", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Live Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Live Scanned", style = MaterialTheme.typography.labelSmall)
                        Text("$presentCount / ${classItem?.studentCount ?: 45}", fontWeight = FontWeight.Bold, color = SecondaryTeal, fontSize = 18.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Timer Left", style = MaterialTheme.typography.labelSmall)
                        Text("${timerSeconds / 60}:${String.format("%02d", timerSeconds % 60)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error, fontSize = 18.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onComplete(classItem?.id ?: "") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Lock Attendance Session")
                }
            }
        }
    }
}

@Composable
fun ManualAttendanceDialog(
    classItem: ClassScheduleItem?,
    students: List<TeacherStudentItem>,
    onDismiss: () -> Unit,
    onComplete: (String) -> Unit
) {
    val attendanceMap = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(students) {
        students.forEach { attendanceMap[it.studentId] = true }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().heightIn(max = 600.dp).padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Manual Attendance Sheet", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("${classItem?.courseCode} - ${classItem?.section}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = { students.forEach { attendanceMap[it.studentId] = true } }) {
                        Text("Mark All Present")
                    }
                    TextButton(onClick = { students.forEach { attendanceMap[it.studentId] = false } }) {
                        Text("Mark All Absent")
                    }
                }

                Divider()

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(students) { student ->
                        val isPresent = attendanceMap[student.studentId] ?: true
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(student.studentName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("${student.rollNumber} | ${student.section}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            FilterChip(
                                selected = isPresent,
                                onClick = { attendanceMap[student.studentId] = !isPresent },
                                label = { Text(if (isPresent) "PRESENT" else "ABSENT") },
                                leadingIcon = {
                                    Icon(
                                        if (isPresent) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                        contentDescription = null,
                                        tint = if (isPresent) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                                    )
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { onComplete(classItem?.id ?: "") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Attendance Roster")
                }
            }
        }
    }
}

@Composable
fun PostNoticeDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String, Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var dept by remember { mutableStateOf("Computer Science & Engineering") }
    var course by remember { mutableStateOf("All") }
    var isPinned by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Publish Academic Notice", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Notice Title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Notice Description / Announcement") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = dept,
                    onValueChange = { dept = it },
                    label = { Text("Target Department") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = course,
                    onValueChange = { course = it },
                    label = { Text("Target Course (e.g. CSE-301 or All)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isPinned, onCheckedChange = { isPinned = it })
                    Text("Pin this notice to top of student feed")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank() && content.isNotBlank()) {
                                onSubmit(title, content, dept, course, isPinned)
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Publish Notice")
                    }
                }
            }
        }
    }
}

@Composable
fun UploadResourceDialog(
    courses: List<TeacherCourse>,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, ResourceType, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCourse by remember { mutableStateOf(courses.firstOrNull()) }
    var resourceType by remember { mutableStateOf(ResourceType.PDF) }
    var fileUrl by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Upload Course Resource", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Resource Title / Topic") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = fileUrl,
                    onValueChange = { fileUrl = it },
                    label = { Text("File URL / Drive Link (PDF, Slides, Video)") },
                    placeholder = { Text("https://example.com/material.pdf") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Text("Resource Type", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ResourceType.values().take(4).forEach { type ->
                        FilterChip(
                            selected = resourceType == type,
                            onClick = { resourceType = type },
                            label = { Text(type.name, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank() && selectedCourse != null) {
                                onSubmit(
                                    selectedCourse!!.courseId,
                                    selectedCourse!!.courseCode,
                                    title,
                                    resourceType,
                                    fileUrl
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Upload Resource")
                    }
                }
            }
        }
    }
}

@Composable
fun RescheduleClassDialog(
    classItem: ClassScheduleItem?,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String) -> Unit
) {
    var newTime by remember { mutableStateOf("02:00 PM - 03:30 PM") }
    var newRoom by remember { mutableStateOf("Lab 204") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Reschedule Class Lecture", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Course: ${classItem?.courseCode} (${classItem?.section})", color = MaterialTheme.colorScheme.primary)

                OutlinedTextField(
                    value = newTime,
                    onValueChange = { newTime = it },
                    label = { Text("New Time Slot") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = newRoom,
                    onValueChange = { newRoom = it },
                    label = { Text("New Room / Lab") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (classItem != null && newTime.isNotBlank() && newRoom.isNotBlank()) {
                                onSubmit(classItem.id, newTime, newRoom)
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Request Reschedule")
                    }
                }
            }
        }
    }
}

@Composable
fun EnterMarksDialog(
    students: List<TeacherStudentItem>,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {
    val marksMap = remember { mutableStateMapOf<String, String>() }

    LaunchedEffect(students) {
        students.forEach { marksMap[it.studentId] = "85" }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().heightIn(max = 580.dp).padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Paperless Result Marks Entry", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Course: CSE-301 Midterm Examination (Total: 100)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(students) { student ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(student.studentName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(student.rollNumber, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            OutlinedTextField(
                                value = marksMap[student.studentId] ?: "",
                                onValueChange = { marksMap[student.studentId] = it },
                                label = { Text("Marks") },
                                modifier = Modifier.width(90.dp),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onSubmit,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Publish Marks & Grade Distribution")
                }
            }
        }
    }
}
