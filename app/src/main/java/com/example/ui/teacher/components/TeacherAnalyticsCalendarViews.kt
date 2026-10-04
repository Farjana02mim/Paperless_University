package com.example.ui.teacher.components

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
import androidx.compose.ui.window.Dialog
import com.example.domain.model.teacher.AcademicCalendarEvent
import com.example.domain.model.teacher.TeachingAnalytics

@Composable
fun AnalyticsDashboardDialog(
    analytics: TeachingAnalytics,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().heightIn(max = 600.dp).padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Teaching Performance & Analytics", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    item {
                        AnalyticsMetricCard(
                            title = "Total Teaching Hours",
                            value = "${analytics.totalTeachingHours} Hours",
                            subtitle = "Semester Goal: 160 Hours",
                            progress = (analytics.totalTeachingHours / 160.0).toFloat(),
                            color = Color(0xFF0284C7)
                        )
                    }

                    item {
                        AnalyticsMetricCard(
                            title = "Overall Class Attendance Rate",
                            value = "${analytics.overallAttendanceRate}%",
                            subtitle = "Campus Avg: 86.5%",
                            progress = (analytics.overallAttendanceRate / 100.0).toFloat(),
                            color = Color(0xFF16A34A)
                        )
                    }

                    item {
                        AnalyticsMetricCard(
                            title = "Average Student Quiz & Exam Score",
                            value = "${analytics.avgStudentScore}%",
                            subtitle = "Target: >80.0%",
                            progress = (analytics.avgStudentScore / 100.0).toFloat(),
                            color = Color(0xFF7C3AED)
                        )
                    }

                    item {
                        AnalyticsMetricCard(
                            title = "Student Engagement & Feedback",
                            value = "${analytics.studentEngagementScore}%",
                            subtitle = "High interactive engagement in live lectures",
                            progress = (analytics.studentEngagementScore / 100.0).toFloat(),
                            color = Color(0xFF059669)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsMetricCard(
    title: String,
    value: String,
    subtitle: String,
    progress: Float,
    color: Color
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text(value, fontWeight = FontWeight.Bold, color = color, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = progress.coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = color
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun AcademicCalendarDialog(
    events: List<AcademicCalendarEvent>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().heightIn(max = 580.dp).padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Faculty Academic Calendar", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(events) { event ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(event.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(event.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("Date: ${event.date}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                }

                                AssistChip(
                                    onClick = {},
                                    label = { Text(event.eventType, fontSize = 10.sp) },
                                    colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
