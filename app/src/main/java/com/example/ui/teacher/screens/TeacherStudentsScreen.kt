package com.example.ui.teacher.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.domain.model.teacher.TeacherStudentItem

@Composable
fun TeacherStudentsScreen(
    students: List<TeacherStudentItem>,
    searchQuery: String,
    departmentFilter: String,
    semesterFilter: String,
    selectedStudent: TeacherStudentItem?,
    onSearchQueryChange: (String) -> Unit,
    onFilterChange: (String, String) -> Unit,
    onSelectStudent: (TeacherStudentItem?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Student Roster & Profiles", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search student by name, roll, or ID...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        // Filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = departmentFilter == "All",
                onClick = { onFilterChange("All", semesterFilter) },
                label = { Text("All Depts", fontSize = 11.sp) }
            )
            FilterChip(
                selected = departmentFilter.contains("Computer"),
                onClick = { onFilterChange("Computer Science & Engineering", semesterFilter) },
                label = { Text("CSE", fontSize = 11.sp) }
            )
            FilterChip(
                selected = semesterFilter == "Semester 5",
                onClick = { onFilterChange(departmentFilter, if (semesterFilter == "Semester 5") "All" else "Semester 5") },
                label = { Text("Sem 5", fontSize = 11.sp) }
            )
        }

        // Student List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(students) { student ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().clickable { onSelectStudent(student) }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    student.studentName.take(1),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 18.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(student.studentName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("${student.rollNumber} • ${student.section}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${student.department} (${student.semester})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("${student.attendancePercentage}%", fontWeight = FontWeight.Bold, color = if (student.attendancePercentage >= 85) Color(0xFF16A34A) else Color(0xFFD97706))
                            Text("Attnd.", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Grade: ${student.avgGrade}", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }

    // Student Detail Sheet / Dialog
    if (selectedStudent != null) {
        AlertDialog(
            onDismissRequest = { onSelectStudent(null) },
            confirmButton = {
                Button(onClick = { onSelectStudent(null) }) {
                    Text("Close Roster Card")
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(selectedStudent.studentName, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Roll Number: ${selectedStudent.rollNumber}", fontSize = 13.sp)
                    Text("Department: ${selectedStudent.department}", fontSize = 13.sp)
                    Text("Semester: ${selectedStudent.semester} (${selectedStudent.section})", fontSize = 13.sp)
                    Text("Course Enrolled: ${selectedStudent.courseCode}", fontSize = 13.sp)
                    Text("Email: ${selectedStudent.email}", fontSize = 13.sp)
                    Text("Phone: ${selectedStudent.phone}", fontSize = 13.sp)

                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                    Text("Academic Performance Summary", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("• Attendance Rate: ${selectedStudent.attendancePercentage}%", fontSize = 13.sp)
                    Text("• Average Grade: ${selectedStudent.avgGrade}", fontSize = 13.sp)
                    Text("• Assignments Submitted: ${selectedStudent.assignmentsSubmitted} / ${selectedStudent.totalAssignments}", fontSize = 13.sp)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}
