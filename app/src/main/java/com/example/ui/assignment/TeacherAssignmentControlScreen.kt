package com.example.ui.assignment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.AssignmentSubmission
import com.example.domain.model.RubricCriterion
import com.example.domain.model.SubmissionStatus
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherAssignmentControlScreen(
    viewModel: TeacherAssignmentViewModel,
    onNavigateBack: () -> Unit
) {
    val teacherAssignments by viewModel.teacherAssignments.collectAsStateWithLifecycle()
    val selectedAssignmentId by viewModel.selectedAssignmentId.collectAsStateWithLifecycle()
    val submissions by viewModel.currentSubmissions.collectAsStateWithLifecycle()
    val actionMessage by viewModel.actionMessage.collectAsStateWithLifecycle()

    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedSubmissionForEvaluation by remember { mutableStateOf<AssignmentSubmission?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Teacher Grading Console", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Create Assignment") },
                containerColor = MaterialTheme.colorScheme.primary
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("Your Active Assignments", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                if (teacherAssignments.isEmpty()) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                Text("No assignments created yet. Tap '+' to create one.", color = Color.Gray)
                            }
                        }
                    }
                } else {
                    items(teacherAssignments) { assignment ->
                        val isSelected = selectedAssignmentId == assignment.assignmentId
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectAssignment(assignment.assignmentId) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(assignment.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("${assignment.subject} • ${assignment.department}", fontSize = 12.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Submissions: ${assignment.totalSubmissions}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("Max Marks: ${assignment.maximumMarks.toInt()}", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }

                // Submissions Feed
                selectedAssignmentId?.let { id ->
                    item {
                        Text("Student Submissions (${submissions.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    if (submissions.isEmpty()) {
                        item {
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                    Text("No student submissions received for this assignment yet.", color = Color.Gray)
                                }
                            }
                        }
                    } else {
                        items(submissions) { sub ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(sub.studentName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Roll: ${sub.rollNumber} • ${if (sub.isLate) "Late Submission" else "On Time"}", fontSize = 12.sp, color = if (sub.isLate) Color.Red else Color.Gray)
                                    }

                                    Button(onClick = { selectedSubmissionForEvaluation = sub }) {
                                        Icon(Icons.Default.Grade, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (sub.status == SubmissionStatus.EVALUATED) "Re-Grade" else "Grade")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Create Assignment Dialog
    if (showCreateDialog) {
        var title by remember { mutableStateOf("") }
        var subject by remember { mutableStateOf("Computer Science") }
        var description by remember { mutableStateOf("") }
        var maxMarks by remember { mutableStateOf("100") }

        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create New Assignment") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Assignment Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Subject / Course") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description & Problem Statement") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    OutlinedTextField(
                        value = maxMarks,
                        onValueChange = { maxMarks = it },
                        label = { Text("Maximum Marks") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (title.isNotBlank()) {
                        viewModel.createAssignment(
                            title = title,
                            description = description,
                            instructions = "Submit original work prior to deadline.",
                            subject = subject,
                            courseId = "CS-301",
                            courseName = subject,
                            department = "Computer Science",
                            semester = "Semester 6",
                            maximumMarks = maxMarks.toDoubleOrNull() ?: 100.0,
                            passingMarks = 40.0,
                            deadlineDays = 7,
                            allowLate = true,
                            rubricCriteria = listOf(
                                RubricCriterion(UUID.randomUUID().toString(), "Correctness & Logic", "Evaluates algorithm accuracy", 50.0),
                                RubricCriterion(UUID.randomUUID().toString(), "Code Style & Documentation", "Clean code practices", 50.0)
                            )
                        )
                        showCreateDialog = false
                    }
                }) {
                    Text("Publish Assignment")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Grading Evaluation Sheet Dialog
    selectedSubmissionForEvaluation?.let { sub ->
        var marksText by remember { mutableStateOf(sub.obtainedMarks.toString()) }
        var feedbackText by remember { mutableStateOf(sub.teacherFeedback) }

        AlertDialog(
            onDismissRequest = { selectedSubmissionForEvaluation = null },
            title = { Text("Grade ${sub.studentName}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Roll Number: ${sub.rollNumber}", fontSize = 12.sp, color = Color.Gray)
                    OutlinedTextField(
                        value = marksText,
                        onValueChange = { marksText = it },
                        label = { Text("Obtained Marks (Max 100)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = feedbackText,
                        onValueChange = { feedbackText = it },
                        label = { Text("Teacher Feedback & Comments") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val marks = marksText.toDoubleOrNull() ?: 0.0
                    viewModel.evaluateSubmission(
                        submissionId = sub.submissionId,
                        obtainedMarks = marks,
                        feedback = feedbackText,
                        remarks = "Evaluated digitally",
                        rubricGrades = emptyList()
                    )
                    selectedSubmissionForEvaluation = null
                }) {
                    Text("Save & Publish Grade")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedSubmissionForEvaluation = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    actionMessage?.let { msg ->
        Snackbar(
            action = { TextButton(onClick = { viewModel.clearActionMessage() }) { Text("OK") } },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(msg)
        }
    }
}
