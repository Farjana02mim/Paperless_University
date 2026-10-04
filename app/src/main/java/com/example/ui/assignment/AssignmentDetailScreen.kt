package com.example.ui.assignment

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.upload.UploadState
import com.example.domain.model.Assignment
import com.example.domain.model.AssignmentAttachment
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentDetailScreen(
    assignmentId: String,
    viewModel: AssignmentViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val assignments by viewModel.assignments.collectAsStateWithLifecycle()
    val uploadState by viewModel.uploadState.collectAsStateWithLifecycle()
    val submissionMessage by viewModel.submissionStatusMessage.collectAsStateWithLifecycle()

    val assignment = remember(assignments, assignmentId) {
        assignments.find { it.assignmentId == assignmentId } ?: Assignment(
            assignmentId = assignmentId,
            title = "CS301 - Algorithm Analysis Project",
            courseName = "Advanced Algorithms & Data Structures",
            subject = "Computer Science",
            description = "Implement and benchmark Graph Algorithms (Dijkstra, Bellman-Ford, A*) in Kotlin or Java. Provide comprehensive performance metrics and complexity proofs.",
            instructions = "1. Include source code in ZIP or PDF format.\n2. Submit benchmark logs and memory profiling graphs.\n3. Plagiarism check is enforced. Original work required.",
            deadline = System.currentTimeMillis() + (86400000L * 3),
            maximumMarks = 100.0,
            passingMarks = 40.0,
            allowedFileTypes = listOf("PDF", "DOCX", "ZIP", "PNG", "JPG"),
            maximumFileSizeMb = 25
        )
    }

    val selectedFiles = remember { mutableStateListOf<AssignmentAttachment>() }
    var showSubmissionDialog by remember { mutableStateOf(false) }

    // File Picker Contract
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = uri.lastPathSegment?.substringAfterLast('/') ?: "submission_file.pdf"
            val newFile = AssignmentAttachment(
                fileId = UUID.randomUUID().toString(),
                fileName = fileName,
                fileUrl = uri.toString(),
                fileType = fileName.substringAfterLast('.', "PDF").uppercase(),
                fileSizeFormatted = "3.2 MB"
            )
            selectedFiles.add(newFile)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Assignment Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { filePickerLauncher.launch("*/*") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Attach Solution")
                    }

                    Button(
                        onClick = {
                            if (selectedFiles.isNotEmpty()) {
                                viewModel.submitAssignment(assignment, selectedFiles)
                            } else {
                                filePickerLauncher.launch("*/*")
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        enabled = uploadState !is UploadState.Progress
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Submit")
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title & Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(assignment.courseName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(assignment.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Deadline", fontSize = 11.sp, color = Color.Gray)
                                Text(
                                    SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(assignment.deadline)),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Column {
                                Text("Max Marks", fontSize = 11.sp, color = Color.Gray)
                                Text("${assignment.maximumMarks.toInt()} Marks", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            Column {
                                Text("Allowed Files", fontSize = 11.sp, color = Color.Gray)
                                Text(assignment.allowedFileTypes.joinToString(", "), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // Description Section
            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Description", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(assignment.description, fontSize = 14.sp, color = Color.DarkGray)
                    }
                }
            }

            // Instructions Section
            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Submission Instructions", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(assignment.instructions, fontSize = 14.sp, color = Color.DarkGray)
                    }
                }
            }

            // Upload Progress Bar
            item {
                val state = uploadState
                if (state is UploadState.Progress) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Uploading Solution...", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("${state.percent}%", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { state.percent / 100f },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Attached Solutions Preview
            item {
                Text("Your Attached Files (${selectedFiles.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            if (selectedFiles.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.15f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No files attached yet. Click 'Attach Solution' below.", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                items(selectedFiles) { file ->
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.InsertDriveFile, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(file.fileName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("${file.fileType} • ${file.fileSizeFormatted}", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                            IconButton(onClick = { selectedFiles.remove(file) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }

    // Submission Status Toast/Snackbar
    submissionMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { viewModel.clearSubmissionMessage() },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32)) },
            title = { Text("Submission Confirmed") },
            text = { Text(msg) },
            confirmButton = {
                Button(onClick = {
                    viewModel.clearSubmissionMessage()
                    onNavigateBack()
                }) {
                    Text("OK")
                }
            }
        )
    }
}
