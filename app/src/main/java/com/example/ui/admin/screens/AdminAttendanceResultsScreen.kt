package com.example.ui.admin.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AdminAttendanceResultsScreen() {
    var resultLocked by remember { mutableStateOf(true) }
    var minAttendanceThreshold by remember { mutableStateOf(75) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Attendance Thresholds & Results Clearance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Grade Locking & Verification", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(
                    "When locked, faculty members cannot modify submitted marks without explicit Registrar clearance.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Semester Marks Status: ${if (resultLocked) "LOCKED" else "UNLOCKED"}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Switch(checked = resultLocked, onCheckedChange = { resultLocked = it })
                }
            }
        }

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Exam Eligibility Threshold", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Minimum required attendance to sit for Fall 2026 Semester Finals:", fontSize = 12.sp)

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    listOf(60, 70, 75, 80).forEach { th ->
                        FilterChip(
                            selected = minAttendanceThreshold == th,
                            onClick = { minAttendanceThreshold = th },
                            label = { Text("$th%", fontSize = 12.sp) }
                        )
                    }
                }
            }
        }

        Text("Pending Attendance Exemption Appeals", fontWeight = FontWeight.Bold, fontSize = 14.sp)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(3) { idx ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Student: John Doe #${1001 + idx}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Reason: Medical Leave (68% Attendance)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(onClick = {}) { Text("Reject", fontSize = 11.sp) }
                            Button(onClick = {}) { Text("Approve", fontSize = 11.sp) }
                        }
                    }
                }
            }
        }
    }
}
