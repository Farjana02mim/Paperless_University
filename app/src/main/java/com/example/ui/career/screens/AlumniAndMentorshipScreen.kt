package com.example.ui.career.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.career.AlumniProfile
import com.example.ui.career.CareerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlumniAndMentorshipScreen(
    viewModel: CareerViewModel,
    onBackClick: () -> Unit
) {
    val alumniList by viewModel.alumniDirectory.collectAsState()
    val sessions by viewModel.mentorshipSessions.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showRequestDialog by remember { mutableStateOf<AlumniProfile?>(null) }
    var topicInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alumni & Mentorship Network", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Alumni Directory") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Mentorship Sessions") }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (selectedTab == 0) {
                    alumniList.forEach { alumni ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(alumni.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                        Text("${alumni.designation} at ${alumni.company}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            "Class of ${alumni.graduationYear}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Dept: ${alumni.department} • Location: ${alumni.location}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Expertise: ${alumni.skills.joinToString(" • ")}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)

                                Spacer(modifier = Modifier.height(12.dp))
                                if (alumni.isAvailableForMentorship) {
                                    Button(
                                        onClick = { showRequestDialog = alumni },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.GroupAdd, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Request 1-on-1 Mentorship")
                                    }
                                } else {
                                    OutlinedButton(
                                        onClick = {},
                                        enabled = false,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Currently Unavailable for Mentorship")
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Text("Your Scheduled 1-on-1 Sessions", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

                    sessions.forEach { sess ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(sess.mentorName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            sess.status,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Topic: ${sess.topic}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                Text("Time: ${sess.scheduledTime}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { /* Join Google Meet */ },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.VideoCall, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Join Google Meet Session")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    showRequestDialog?.let { alumni ->
        AlertDialog(
            onDismissRequest = { showRequestDialog = null },
            title = { Text("Request Mentorship Session") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Mentor: ${alumni.name} (${alumni.company})", fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = topicInput,
                        onValueChange = { topicInput = it },
                        label = { Text("Guidance Topic / Questions") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (topicInput.isNotBlank()) {
                            viewModel.requestMentorship(alumni.name, topicInput)
                            showRequestDialog = null
                            topicInput = ""
                        }
                    }
                ) {
                    Text("Send Request")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRequestDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
