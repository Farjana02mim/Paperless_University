package com.example.ui.campus.screens

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
import com.example.ui.campus.CampusServicesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalCenterScreen(
    viewModel: CampusServicesViewModel,
    onBackClick: () -> Unit
) {
    val doctors by viewModel.doctors.collectAsState()
    val appointments by viewModel.medicalAppointments.collectAsState()

    var showBookDialog by remember { mutableStateOf(false) }
    var selectedDoctor by remember { mutableStateOf("") }
    var symptomsInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Campus Medical Center", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 24/7 Ward Notice Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocalHospital, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("24/7 Emergency Medical Ward", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("Location: Campus Gate 2 Medical Annex • Phone: +880 1700-445566", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Text("On-Duty Doctor Schedules", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

            doctors.forEach { doc ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(doc.doctorName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                Text(doc.specialization, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            }
                            Button(
                                onClick = {
                                    selectedDoctor = doc.doctorName
                                    showBookDialog = true
                                },
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("Book", fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Hours: ${doc.visitingHours}", style = MaterialTheme.typography.bodySmall)
                        Text("Location: ${doc.roomNumber}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Text("Your Medical Appointments", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

            appointments.forEach { apt ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(apt.doctorName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text("${apt.appointmentDate} at ${apt.appointmentTime}", style = MaterialTheme.typography.bodySmall)
                            Text("Symptoms: ${apt.symptoms}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                apt.status,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showBookDialog) {
        AlertDialog(
            onDismissRequest = { showBookDialog = false },
            title = { Text("Book Doctor Appointment") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Doctor: $selectedDoctor", fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = symptomsInput,
                        onValueChange = { symptomsInput = it },
                        label = { Text("Brief Symptoms / Reason") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (symptomsInput.isNotBlank()) {
                            showBookDialog = false
                            symptomsInput = ""
                        }
                    }
                ) {
                    Text("Confirm Appointment")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBookDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
