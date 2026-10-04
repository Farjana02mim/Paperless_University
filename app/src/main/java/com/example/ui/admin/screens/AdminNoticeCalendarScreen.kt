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
fun AdminNoticeCalendarScreen() {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isEmergency by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Campus Emergency Broadcast & Calendar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Post Institutional Broadcast", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Notice Title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Notice Description / Policy Update") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = isEmergency, onCheckedChange = { isEmergency = it })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Push High-Priority Emergency Alert", fontSize = 13.sp, color = if (isEmergency) Color.Red else MaterialTheme.colorScheme.onSurface)
                }

                Button(
                    onClick = {
                        title = ""
                        content = ""
                        isEmergency = false
                    },
                    modifier = Modifier.align(Alignment.End),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Broadcast Notice")
                }
            }
        }

        Text("Active Institutional Announcements", fontWeight = FontWeight.Bold, fontSize = 14.sp)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("[EMERGENCY] Final Exam Schedule Revision", fontWeight = FontWeight.Bold, color = Color(0xFF991B1B), fontSize = 13.sp)
                        Text("Due to severe weather warnings, all 2.00 PM exams today are moved to Saturday.", fontSize = 12.sp, color = Color(0xFF991B1B))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Broadcasted to: 102,450 Mobile Devices", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}
