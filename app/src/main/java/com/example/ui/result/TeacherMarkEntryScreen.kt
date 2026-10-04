package com.example.ui.result

import androidx.compose.foundation.background
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
import com.example.domain.model.result.CourseResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherMarkEntryScreen(
    viewModel: TeacherMarkEntryViewModel,
    onNavigateBack: () -> Unit
) {
    val studentMarksList by viewModel.studentMarksList.collectAsStateWithLifecycle()
    val actionMessage by viewModel.actionMessage.collectAsStateWithLifecycle()
    val isDraftSaved by viewModel.isDraftSaved.collectAsStateWithLifecycle()

    var editingStudentResult by remember { mutableStateOf<CourseResult?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Teacher Mark Entry Console", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.importBulkMarksFromCsv() }) {
                        Icon(Icons.Default.UploadFile, contentDescription = "Import CSV")
                    }
                    IconButton(onClick = { viewModel.saveDraft() }) {
                        Icon(Icons.Default.Save, contentDescription = "Save Draft")
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.saveDraft() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isDraftSaved) "Draft Saved" else "Save Draft")
                    }

                    Button(
                        onClick = { viewModel.submitMarksForApproval() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Submit Marks")
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Course Header Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("CS-301: Advanced Algorithms", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Total Enrolled Students: ${studentMarksList.size} • Semester 6", fontSize = 12.sp, color = Color.Gray)
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(studentMarksList) { student ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(student.studentName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Roll: ${student.rollNumber}", fontSize = 12.sp, color = Color.Gray)
                                Text(
                                    "Att: ${student.attendanceMarks.toInt()} | Assign: ${student.assignmentMarks.toInt()} | Quiz: ${student.quizMarks.toInt()} | Mid: ${student.midMarks.toInt()} | Final: ${student.finalMarks.toInt()}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        "${student.totalMarks.toInt()} (${student.letterGrade})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                IconButton(onClick = { editingStudentResult = student }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Marks Dialog
    editingStudentResult?.let { student ->
        var attText by remember { mutableStateOf(student.attendanceMarks.toString()) }
        var assignText by remember { mutableStateOf(student.assignmentMarks.toString()) }
        var quizText by remember { mutableStateOf(student.quizMarks.toString()) }
        var midText by remember { mutableStateOf(student.midMarks.toString()) }
        var finText by remember { mutableStateOf(student.finalMarks.toString()) }

        AlertDialog(
            onDismissRequest = { editingStudentResult = null },
            title = { Text("Edit Marks for ${student.studentName}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = attText, onValueChange = { attText = it }, label = { Text("Attendance (Max 10)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = assignText, onValueChange = { assignText = it }, label = { Text("Assignments (Max 10)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = quizText, onValueChange = { quizText = it }, label = { Text("Quizzes (Max 15)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = midText, onValueChange = { midText = it }, label = { Text("Midterm Exam (Max 30)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = finText, onValueChange = { finText = it }, label = { Text("Final Exam (Max 50)") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(onClick = {
                    val att = attText.toDoubleOrNull() ?: 0.0
                    val assign = assignText.toDoubleOrNull() ?: 0.0
                    val quiz = quizText.toDoubleOrNull() ?: 0.0
                    val mid = midText.toDoubleOrNull() ?: 0.0
                    val fin = finText.toDoubleOrNull() ?: 0.0

                    viewModel.updateStudentMarks(student.resultId, att, assign, quiz, mid, 0.0, fin)
                    editingStudentResult = null
                }) {
                    Text("Save Marks")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingStudentResult = null }) { Text("Cancel") }
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
