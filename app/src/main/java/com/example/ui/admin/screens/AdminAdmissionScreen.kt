package com.example.ui.admin.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.domain.model.admin.AdmissionApplication
import com.example.ui.admin.AdminModalType

@Composable
fun AdminAdmissionScreen(
    applications: List<AdmissionApplication>,
    onSelectApplication: (AdmissionApplication?) -> Unit,
    onOpenModal: (AdminModalType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Admission & Seat Allocation Control", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Badge { Text("${applications.size} Applicants") }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(applications) { app ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().clickable {
                        onSelectApplication(app)
                        onOpenModal(AdminModalType.ADMISSION_ACTION)
                    }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(app.applicantName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            AssistChip(
                                onClick = {},
                                label = { Text(app.status, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Applied Department: ${app.appliedDept}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text("Merit Score: ${app.meritScore} | HSC GPA: ${app.hscGpa}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        Text("Allocated Seat: ${app.seatAllocated}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Submitted: ${app.submissionDate}", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}
