package com.example.ui.admission.screens

import androidx.compose.foundation.background
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
import com.example.ui.admission.ApplicantDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicantDashboardScreen(
    viewModel: ApplicantDashboardViewModel,
    onNavigateToForm: () -> Unit,
    onNavigateToAdmitCard: (String) -> Unit,
    onNavigateToMeritList: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Admission Portal", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToMeritList) {
                        Icon(Icons.Default.Leaderboard, contentDescription = "Merit List")
                    }
                    IconButton(onClick = onNavigateToAdmin) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin Portal")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToForm,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New / Edit Application") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Active Session Header
            uiState.activeSession?.let { session ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = session.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                AssistChip(
                                    onClick = {},
                                    label = { Text("OPEN", fontWeight = FontWeight.Bold) },
                                    colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF4CAF50), labelColor = Color.White)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Academic Year: ${session.academicYear} • Application Fee: ৳${session.admissionFee.toInt()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = onNavigateToForm,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Fill Online Admission Form")
                            }
                        }
                    }
                }
            }

            // Application Selection Row
            if (uiState.userApplications.isNotEmpty()) {
                item {
                    Text("My Applications", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(uiState.userApplications) { app ->
                            FilterChip(
                                selected = uiState.selectedApplication?.applicationId == app.applicationId,
                                onClick = { viewModel.selectApplication(app) },
                                label = { Text("${app.applicationId} (${app.departmentChoice1.take(15)})") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (app.paymentStatus == PaymentStatus.PAID) Icons.Default.CheckCircle else Icons.Default.Pending,
                                        contentDescription = null,
                                        tint = if (app.paymentStatus == PaymentStatus.PAID) Color(0xFF388E3C) else Color(0xFFF57C00)
                                    )
                                }
                            )
                        }
                    }
                }

                // Selected Application Details & Tracker
                uiState.selectedApplication?.let { app ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(app.fullName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Text("App ID: ${app.applicationId} • Roll: ${app.rollNumber.ifBlank { "Pending" }}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                    StatusBadge(status = app.applicationStatus)
                                }

                                HorizontalDivider()

                                // Department Choices & Quota
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Primary Choice", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                                        Text(app.departmentChoice1, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Quota Type", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                                        Text(app.quotaType.name.replace("_", " "), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }

                                // Status Timeline
                                ApplicationStatusTimeline(currentStatus = app.applicationStatus)

                                // Actions (Download Admit Card, Application PDF, Payment)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (app.applicationStatus in listOf(ApplicationStatus.SUBMITTED, ApplicationStatus.UNDER_REVIEW, ApplicationStatus.DOCUMENT_VERIFICATION, ApplicationStatus.ELIGIBLE, ApplicationStatus.MERIT_LISTED, ApplicationStatus.APPROVED)) {
                                        Button(
                                            onClick = { onNavigateToAdmitCard(app.applicationId) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Icon(Icons.Default.Badge, contentDescription = null)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Admit Card", fontSize = 12.sp)
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = { /* Export PDF */ },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Application PDF", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No Active Applications Found", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Tap 'New / Edit Application' below to start your online admission.", color = Color.Gray)
                        }
                    }
                }
            }

            // Admission Notices Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Campaign, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Important Admission Notices", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        }
                        Text("• Admission Test Date: September 15, 2026 at 10:00 AM.", style = MaterialTheme.typography.bodySmall)
                        Text("• Document Verification: Ensure original HSC/SSC marksheets are scanned clearly.", style = MaterialTheme.typography.bodySmall)
                        Text("• Helpdesk Contact: admission@university.edu.bd | Hotline: +880 9612-345678", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
fun StatusBadge(status: ApplicationStatus) {
    val (bgColor, textColor) = when (status) {
        ApplicationStatus.DRAFT -> Color.LightGray to Color.DarkGray
        ApplicationStatus.SUBMITTED -> Color(0xFF1976D2) to Color.White
        ApplicationStatus.UNDER_REVIEW -> Color(0xFF009688) to Color.White
        ApplicationStatus.DOCUMENT_VERIFICATION -> Color(0xFFFF9800) to Color.White
        ApplicationStatus.ELIGIBLE -> Color(0xFF00BCD4) to Color.White
        ApplicationStatus.MERIT_LISTED -> Color(0xFF9C27B0) to Color.White
        ApplicationStatus.APPROVED -> Color(0xFF388E3C) to Color.White
        ApplicationStatus.REJECTED -> Color(0xFFD32F2F) to Color.White
        else -> Color.Gray to Color.White
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = status.name.replace("_", " "),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ApplicationStatusTimeline(currentStatus: ApplicationStatus) {
    val stages = listOf(
        ApplicationStatus.SUBMITTED,
        ApplicationStatus.DOCUMENT_VERIFICATION,
        ApplicationStatus.ELIGIBLE,
        ApplicationStatus.MERIT_LISTED,
        ApplicationStatus.APPROVED
    )

    val currentIndex = stages.indexOf(currentStatus).let { if (it == -1) 0 else it }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Status Progress Tracker", style = MaterialTheme.typography.labelMedium, color = Color.Gray, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            stages.forEachIndexed { idx, stage ->
                val isCompleted = idx <= currentIndex
                val isCurrent = idx == currentIndex

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCurrent -> MaterialTheme.colorScheme.primary
                                    isCompleted -> Color(0xFF388E3C)
                                    else -> Color.LightGray
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        } else {
                            Text("${idx + 1}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stage.name.take(6),
                        fontSize = 9.sp,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                        color = if (isCompleted) MaterialTheme.colorScheme.onSurface else Color.Gray
                    )
                }
            }
        }
    }
}
