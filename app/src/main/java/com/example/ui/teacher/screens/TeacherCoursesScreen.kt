package com.example.ui.teacher.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.domain.model.teacher.*
import com.example.ui.teacher.TeacherModalType

@Composable
fun TeacherCoursesScreen(
    courses: List<TeacherCourse>,
    resources: List<CourseResource>,
    students: List<TeacherStudentItem>,
    selectedCourseId: String?,
    onSelectCourse: (String?) -> Unit,
    onOpenModal: (TeacherModalType, ClassScheduleItem?) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Syllabus, 1: Materials, 2: Students, 3: Stats
    val currentCourse = courses.find { it.courseId == selectedCourseId } ?: courses.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Course Selector Header / Cards
        Text("Assigned Courses", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        LazyColumn(
            modifier = Modifier.heightIn(max = 220.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(courses) { course ->
                val isSelected = course.courseId == currentCourse?.courseId
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp),
                    modifier = Modifier.fillMaxWidth().clickable { onSelectCourse(course.courseId) }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(course.courseCode, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("${course.credits} Credits • ${course.section}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(course.courseName, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            Text("Room: ${course.roomNumber} • ${course.totalStudents} Students", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("${course.progressPercentage}%", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("Course Progress", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        Divider()

        if (currentCourse != null) {
            // Course Detail Tabs
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("Syllabus", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("Materials", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                    Text("Roster (${students.size})", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }) {
                    Text("Stats", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> SyllabusTab(currentCourse)
                    1 -> MaterialsTab(currentCourse, resources, onOpenModal)
                    2 -> RosterTab(students)
                    3 -> CourseStatsTab(currentCourse)
                }
            }
        }
    }
}

@Composable
fun SyllabusTab(course: TeacherCourse) {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Course Syllabus & Learning Outcomes", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(course.syllabusSummary, style = MaterialTheme.typography.bodyMedium)
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Weekly Schedule & Room Allocation", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Schedule: ${course.scheduleTime}", fontSize = 13.sp)
                Text("Venue: ${course.roomNumber}", fontSize = 13.sp)
                Text("Department: ${course.department}", fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun MaterialsTab(
    course: TeacherCourse,
    resources: List<CourseResource>,
    onOpenModal: (TeacherModalType, ClassScheduleItem?) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Uploaded Materials (${resources.size})", fontWeight = FontWeight.Bold)
            Button(
                onClick = { onOpenModal(TeacherModalType.UPLOAD_RESOURCE, null) },
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Upload Material")
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(resources) { res ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    when (res.resourceType) {
                                        ResourceType.PDF -> Icons.Default.PictureAsPdf
                                        ResourceType.SLIDES -> Icons.Default.Slideshow
                                        ResourceType.VIDEO -> Icons.Default.VideoLibrary
                                        else -> Icons.Default.Description
                                    },
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(res.title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("${res.resourceType.name} • ${res.sizeKb} KB • Uploaded ${res.uploadDate}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Download, contentDescription = "Download")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RosterTab(students: List<TeacherStudentItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(top = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(students) { student ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(student.studentName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${student.rollNumber} • ${student.section}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("${student.attendancePercentage}% Attnd.", fontWeight = FontWeight.SemiBold, color = Color(0xFF16A34A), fontSize = 12.sp)
                        Text("Grade: ${student.avgGrade}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
fun CourseStatsTab(course: TeacherCourse) {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Course Performance & Engagement", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Text("Average Course Attendance: ${course.averageAttendanceRate}%", fontSize = 13.sp)
                LinearProgressIndicator(
                    progress = (course.averageAttendanceRate / 100.0).toFloat(),
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Course Syllabus Completion: ${course.progressPercentage}%", fontSize = 13.sp)
                LinearProgressIndicator(
                    progress = (course.progressPercentage / 100.0).toFloat(),
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
                )
            }
        }
    }
}
