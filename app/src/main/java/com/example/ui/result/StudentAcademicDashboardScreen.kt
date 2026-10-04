package com.example.ui.result

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.result.AcademicStanding
import com.example.domain.model.result.CourseResult
import com.example.domain.model.result.SemesterResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentAcademicDashboardScreen(
    viewModel: StudentResultViewModel,
    onNavigateToTranscript: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val semesterResults by viewModel.semesterResults.collectAsStateWithLifecycle()
    val filteredCourseResults by viewModel.filteredCourseResults.collectAsStateWithLifecycle()
    val cgpa by viewModel.overallCGPA.collectAsStateWithLifecycle()
    val completedCredits by viewModel.completedCredits.collectAsStateWithLifecycle()
    val standing by viewModel.currentAcademicStanding.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()

    val tabs = listOf("Current", "Semester History", "Course Breakdown", "Transcript", "Analytics")
    var selectedCourseForDetail by remember { mutableStateOf<CourseResult?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Academic Results Portal", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToAnalytics) {
                        Icon(Icons.Default.Analytics, contentDescription = "Analytics")
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
            // Summary Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Overall CGPA", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                            Text("%.2f / 4.00".format(cgpa), fontWeight = FontWeight.Bold, fontSize = 26.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = when (standing) {
                                AcademicStanding.DEANS_LIST -> Color(0xFF2E7D32)
                                AcademicStanding.EXCELLENT -> Color(0xFF1565C0)
                                AcademicStanding.GOOD -> Color(0xFF00838F)
                                AcademicStanding.SATISFACTORY -> Color(0xFFE65100)
                                else -> Color(0xFFC62828)
                            }
                        ) {
                            Text(
                                standing.name.replace('_', ' '),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ResultSummaryMetric("Earned Credits", "${completedCredits.toInt()} Cr")
                        ResultSummaryMetric("Department", "CS & Eng")
                        ResultSummaryMetric("Current Sem", "Semester 6")
                    }
                }
            }

            // Tab Navigation Row
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = {
                            viewModel.setSelectedTab(index)
                            if (index == 3) onNavigateToTranscript()
                            if (index == 4) onNavigateToAnalytics()
                        },
                        text = { Text(title, fontSize = 12.sp, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            // Tab Body Content
            Crossfade(targetState = selectedTab, label = "ResultTabFade") { tabIndex ->
                when (tabIndex) {
                    0, 2 -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredCourseResults) { course ->
                                CourseResultItemCard(
                                    course = course,
                                    onClick = { selectedCourseForDetail = course }
                                )
                            }
                        }
                    }
                    1 -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(semesterResults) { sem ->
                                SemesterHistoryCard(sem = sem)
                            }
                        }
                    }
                }
            }
        }
    }

    // Detailed Mark Breakdown Modal Dialog
    selectedCourseForDetail?.let { course ->
        AlertDialog(
            onDismissRequest = { selectedCourseForDetail = null },
            icon = { Icon(Icons.Default.School, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("${course.courseCode} - ${course.courseTitle}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Teacher: ${course.teacherName}", fontSize = 12.sp, color = Color.Gray)
                    Text("Credit Hours: ${course.creditHours}", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))
                    MarkBreakdownRow("Attendance Marks", "${course.attendanceMarks} / 10.0")
                    MarkBreakdownRow("Assignment Marks", "${course.assignmentMarks} / 10.0")
                    MarkBreakdownRow("Quiz Marks", "${course.quizMarks} / 15.0")
                    MarkBreakdownRow("Midterm Exam", "${course.midMarks} / 30.0")
                    MarkBreakdownRow("Lab / Practical", "${course.labMarks} / 20.0")
                    MarkBreakdownRow("Final Semester Exam", "${course.finalMarks} / 50.0")
                    HorizontalDivider()
                    MarkBreakdownRow("Total Score", "%.1f / 100.0".format(course.totalMarks), isBold = true)
                    MarkBreakdownRow("Letter Grade", "${course.letterGrade} (${course.gradePoint} Point)", isBold = true)
                    if (course.remarks.isNotBlank()) {
                        Text("Remarks: ${course.remarks}", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedCourseForDetail = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun ResultSummaryMetric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(label, fontSize = 10.sp, color = Color.Gray)
    }
}

@Composable
fun CourseResultItemCard(
    course: CourseResult,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        "${course.courseCode} • ${course.creditHours} Cr",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(course.courseTitle, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Total: %.1f Marks".format(course.totalMarks), fontSize = 12.sp, color = Color.Gray)
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (course.letterGrade) {
                        "A+", "A" -> Color(0xFF2E7D32)
                        "A-", "B+" -> Color(0xFF1565C0)
                        "B", "B-" -> Color(0xFF00838F)
                        "F" -> Color(0xFFC62828)
                        else -> MaterialTheme.colorScheme.primary
                    }
                ) {
                    Text(
                        course.letterGrade,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("${course.gradePoint} Point", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SemesterHistoryCard(sem: SemesterResult) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(sem.semester, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("${sem.earnedCredits} Credits Earned • Rank: #${sem.rankInBatch}", fontSize = 12.sp, color = Color.Gray)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            "GPA: %.2f".format(sem.semesterGPA),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle"
                        )
                    }
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider()
                    sem.courseResults.forEach { c ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${c.courseCode} ${c.courseTitle.take(20)}", fontSize = 13.sp)
                            Text("${c.letterGrade} (${c.gradePoint})", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MarkBreakdownRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
        Text(value, fontSize = 13.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
    }
}
