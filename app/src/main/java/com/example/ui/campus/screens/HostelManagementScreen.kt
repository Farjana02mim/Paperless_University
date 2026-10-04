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
fun HostelManagementScreen(
    viewModel: CampusServicesViewModel,
    onBackClick: () -> Unit
) {
    val allocation by viewModel.hostelAllocation.collectAsState()
    val messMenu by viewModel.messMenu.collectAsState()
    val leaveRequests by viewModel.leaveRequests.collectAsState()

    var showLeaveDialog by remember { mutableStateOf(false) }
    var leaveReason by remember { mutableStateOf("") }
    var leaveStartDate by remember { mutableStateOf("12 Aug 2026") }
    var leaveEndDate by remember { mutableStateOf("15 Aug 2026") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hostel Management", fontWeight = FontWeight.Bold) },
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
            // Room Allocation Card
            allocation?.let { room ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bed, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Room & Bed Allocation", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("${room.hostelName} • Room ${room.roomNumber}", fontWeight = FontWeight.Bold)
                        Text("Assigned: ${room.bedNumber} • Monthly Rent: BDT ${room.monthlyFeeAmount.toInt()}", style = MaterialTheme.typography.bodySmall)

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                        Text("Roommates:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                        room.roommateNames.forEach { roommate ->
                            Text("• $roommate", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            // Mess Menu Card
            messMenu?.let { menu ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Restaurant, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Daily Mess Schedule (${menu.dayOfWeek})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Breakfast:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        Text(menu.breakfast, style = MaterialTheme.typography.bodyMedium)

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Lunch:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        Text(menu.lunch, style = MaterialTheme.typography.bodyMedium)

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Dinner:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        Text(menu.dinner, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // Leave Requests Header & Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Hostel Leave Requests", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Button(onClick = { showLeaveDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Apply Leave")
                }
            }

            leaveRequests.forEach { req ->
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
                            Text(req.reason, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text("${req.startDate} to ${req.endDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (req.status == "APPROVED") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                req.status,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showLeaveDialog) {
        AlertDialog(
            onDismissRequest = { showLeaveDialog = false },
            title = { Text("Submit Hostel Leave Request") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = leaveReason,
                        onValueChange = { leaveReason = it },
                        label = { Text("Reason for Leave") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = leaveStartDate,
                        onValueChange = { leaveStartDate = it },
                        label = { Text("Start Date") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = leaveEndDate,
                        onValueChange = { leaveEndDate = it },
                        label = { Text("End Date") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (leaveReason.isNotBlank()) {
                            viewModel.submitHostelLeave(leaveReason, leaveStartDate, leaveEndDate)
                            showLeaveDialog = false
                            leaveReason = ""
                        }
                    }
                ) {
                    Text("Submit Request")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLeaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
