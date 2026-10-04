package com.example.ui.admin.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.admin.AdminAuditLog
import com.example.domain.model.admin.SystemSettings
import com.example.ui.admin.AdminModalType

@Composable
fun AdminSettingsSecurityScreen(
    settings: SystemSettings,
    auditLogs: List<AdminAuditLog>,
    onOpenModal: (AdminModalType) -> Unit
) {
    var subTab by remember { mutableIntStateOf(0) } // 0: Settings & Backup, 1: Audit System Logs

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("System Settings, Security & Audit Trail", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        TabRow(selectedTabIndex = subTab) {
            Tab(selected = subTab == 0, onClick = { subTab = 0 }) {
                Text("ERP System Settings", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
            Tab(selected = subTab == 1, onClick = { subTab = 1 }) {
                Text("Audit Trail (${auditLogs.size})", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }

        if (subTab == 0) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("University Configuration", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        IconButton(onClick = { onOpenModal(AdminModalType.SYSTEM_SETTINGS) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Settings")
                        }
                    }

                    Text("Institution Name: ${settings.universityName}", fontSize = 13.sp)
                    Text("University Code: ${settings.universityCode}", fontSize = 13.sp)
                    Text("Active Semester: ${settings.currentSemester}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                    Text("Minimum Attendance Required: ${settings.minAttendancePercentage}%", fontSize = 13.sp)
                    Text("MFA Requirement: ${if (settings.mfaRequired) "MANDATORY FOR ALL USERS" else "OPTIONAL"}", fontSize = 13.sp, color = if (settings.mfaRequired) Color(0xFF166534) else Color.DarkGray)
                }
            }

            // Cloud Data Snapshot Tile
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Cloud Database Backup", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Schedule: ${settings.autoBackupFrequency}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Button(
                        onClick = { onOpenModal(AdminModalType.CLOUD_BACKUP) },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Backup Now")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(auditLogs) { log ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(log.action, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                                Text(log.timestamp, fontSize = 11.sp, color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("User: ${log.user} (${log.role})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("Details: ${log.details}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("IP & Station: ${log.ipAddress} | ${log.device}", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}
