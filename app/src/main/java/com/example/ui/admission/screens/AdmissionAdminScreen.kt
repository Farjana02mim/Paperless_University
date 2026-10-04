package com.example.ui.admission.screens

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
import com.example.domain.model.admission.*
import com.example.ui.admission.AdmissionAdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdmissionAdminScreen(
    viewModel: AdmissionAdminViewModel,
    onNavigateToAnalytics: () -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedAppForReview by remember { mutableStateOf<AdmissionApplication?>(null) }
    var showNewSessionDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(uiState.exportStatus) {
        uiState.exportStatus?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Admission Admin Portal", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToAnalytics) {
                        Icon(Icons.Default.BarChart, contentDescription = "Analytics")
                    }
                    IconButton(onClick = { viewModel.exportReport("Excel") }) {
                        Icon(Icons.Default.TableChart, contentDescription = "Export Excel")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showNewSessionDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New Admission Session") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search Bar & Filters
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = { Text("Search by Name, App ID, Phone...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            // Status Filter Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = uiState.selectedStatus == null,
                        onClick = { viewModel.filterByStatus(null) },
                        label = { Text("All Applications") }
                    )
                }
                items(ApplicationStatus.entries.toTypedArray()) { st ->
                    FilterChip(
                        selected = uiState.selectedStatus == st,
                        onClick = { viewModel.filterByStatus(st) },
                        label = { Text(st.name.replace("_", " ")) }
                    )
                }
            }

            // Applications List
            if (uiState.applications.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No admission applications match filter criteria", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.applications) { app ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedAppForReview = app },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(app.fullName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                        Text("ID: ${app.applicationId} • Phone: ${app.phone}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                    StatusBadge(status = app.applicationStatus)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Choice 1: ${app.departmentChoice1}", style = MaterialTheme.typography.bodySmall)
                                    Text("HSC GPA: ${app.hscInfo.gpa}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.updateApplicationStatus(app.applicationId, ApplicationStatus.APPROVED, "Approved by Admin") },
                                        modifier = Modifier.weight(1f).height(36.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Approve", fontSize = 11.sp)
                                    }

                                    OutlinedButton(
                                        onClick = { viewModel.updateApplicationStatus(app.applicationId, ApplicationStatus.REJECTED, "Ineligible") },
                                        modifier = Modifier.weight(1f).height(36.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Reject", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet for Application Review
    selectedAppForReview?.let { app ->
        ModalBottomSheet(onDismissRequest = { selectedAppForReview = null }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Review Application: ${app.fullName}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                Text("Academic Overview", fontWeight = FontWeight.Bold)
                Text("SSC: Board ${app.sscInfo.board}, GPA ${app.sscInfo.gpa}")
                Text("HSC: Board ${app.hscInfo.board}, GPA ${app.hscInfo.gpa}")

                HorizontalDivider()

                Text("Uploaded Documents", fontWeight = FontWeight.Bold)
                app.documents.forEach { doc ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${doc.docType.name}: ${doc.fileName}", style = MaterialTheme.typography.bodySmall)
                        IconButton(onClick = { viewModel.verifyDocument(app.applicationId, doc.docType, true, "Verified by Admin") }) {
                            Icon(Icons.Default.Verified, contentDescription = "Verify", tint = if (doc.status == DocVerificationStatus.VERIFIED) Color(0xFF388E3C) else Color.Gray)
                        }
                    }
                }

                Button(
                    onClick = { selectedAppForReview = null },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Done Reviewing")
                }
            }
        }
    }

    if (showNewSessionDialog) {
        CreateSessionDialog(
            onDismiss = { showNewSessionDialog = false },
            onCreate = { sess ->
                viewModel.createSession(sess)
                showNewSessionDialog = false
            }
        )
    }
}

@Composable
fun CreateSessionDialog(
    onDismiss: () -> Unit,
    onCreate: (AdmissionSession) -> Unit
) {
    var name by remember { mutableStateOf("Spring Admission 2027") }
    var year by remember { mutableStateOf("2026-2027") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Admission Session") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Session Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = year, onValueChange = { year = it }, label = { Text("Academic Year") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val sess = AdmissionSession(
                        sessionId = "sess_${System.currentTimeMillis().toString().takeLast(6)}",
                        name = name,
                        academicYear = year,
                        departments = listOf("Computer Science & Engineering", "Electrical Engineering", "Business Administration")
                    )
                    onCreate(sess)
                }
            ) {
                Text("Create Session")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
