package com.example.ui.teacher.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.model.teacher.*
import com.example.ui.teacher.TeacherModalType
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.StatusSuccess

@Composable
fun TeacherHomeScreen(
    profile: TeacherProfile,
    todayClasses: List<ClassScheduleItem>,
    tasks: List<TeacherTask>,
    analytics: TeachingAnalytics,
    notifications: List<TeacherNotification>,
    onOpenModal: (TeacherModalType, ClassScheduleItem?) -> Unit,
    onToggleTask: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Teacher Profile & Greeting Header Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Unspecified),
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF0F766E), Color(0xFF0F172A))
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentDescription = "Teacher Photo",
                                modifier = Modifier.size(46.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Welcome, ${profile.fullName}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "${profile.designation} | ${profile.teacherId}",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.85f))
                            )
                            Text(
                                text = "${profile.department} • Room: ${profile.officeRoom}",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.7f))
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Divider(color = Color.White.copy(alpha = 0.2f))

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    HeaderStatItem(label = "Teaching Hours", value = "${analytics.totalTeachingHours}h")
                    HeaderStatItem(label = "Attendance", value = "${analytics.overallAttendanceRate}%")
                    HeaderStatItem(label = "Avg Student Score", value = "${analytics.avgStudentScore}%")
                    HeaderStatItem(label = "Rating", value = "★ ${profile.rating}")
                }
            }
        }

        // Quick Statistics Cards Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickStatCard(
                title = "Today's Classes",
                value = "${todayClasses.size}",
                subtitle = "${todayClasses.count { it.status == ScheduleStatus.COMPLETED }} completed",
                icon = Icons.Default.Schedule,
                color = Color(0xFF0284C7),
                modifier = Modifier.weight(1f)
            )
            QuickStatCard(
                title = "Pending Eval",
                value = "${analytics.pendingEvaluations}",
                subtitle = "Submissions waiting",
                icon = Icons.Default.AssignmentLate,
                color = Color(0xFFD97706),
                modifier = Modifier.weight(1f)
            )
            QuickStatCard(
                title = "Engagement",
                value = "${analytics.studentEngagementScore}%",
                subtitle = "Active participation",
                icon = Icons.Default.TrendingUp,
                color = Color(0xFF16A34A),
                modifier = Modifier.weight(1f)
            )
        }

        // Today's Class Schedule Section
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Class Schedule",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                TextButton(onClick = { onOpenModal(TeacherModalType.CALENDAR, null) }) {
                    Text("Full Calendar")
                }
            }

            if (todayClasses.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No classes scheduled for today.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                todayClasses.forEach { classItem ->
                    ClassScheduleCard(
                        classItem = classItem,
                        onQrClick = { onOpenModal(TeacherModalType.QR_ATTENDANCE, classItem) },
                        onManualClick = { onOpenModal(TeacherModalType.MANUAL_ATTENDANCE, classItem) },
                        onRescheduleClick = { onOpenModal(TeacherModalType.RESCHEDULE_CLASS, classItem) }
                    )
                }
            }
        }

        // Interactive Quick Actions Grid
        Text(
            text = "Faculty Shortcuts & Actions",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TeacherActionTile(
                title = "Start Live QR",
                icon = Icons.Default.QrCode2,
                color = Color(0xFF0D9488),
                modifier = Modifier.weight(1f),
                onClick = { onOpenModal(TeacherModalType.QR_ATTENDANCE, todayClasses.firstOrNull()) }
            )
            TeacherActionTile(
                title = "Manual Roll",
                icon = Icons.Default.Checklist,
                color = Color(0xFF0284C7),
                modifier = Modifier.weight(1f),
                onClick = { onOpenModal(TeacherModalType.MANUAL_ATTENDANCE, todayClasses.firstOrNull()) }
            )
            TeacherActionTile(
                title = "Enter Marks",
                icon = Icons.Default.Grade,
                color = Color(0xFF7C3AED),
                modifier = Modifier.weight(1f),
                onClick = { onOpenModal(TeacherModalType.ENTER_MARKS, null) }
            )
            TeacherActionTile(
                title = "Post Notice",
                icon = Icons.Default.Campaign,
                color = Color(0xFFE11D48),
                modifier = Modifier.weight(1f),
                onClick = { onOpenModal(TeacherModalType.POST_NOTICE, null) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TeacherActionTile(
                title = "Upload Slides",
                icon = Icons.Default.CloudUpload,
                color = Color(0xFF2563EB),
                modifier = Modifier.weight(1f),
                onClick = { onOpenModal(TeacherModalType.UPLOAD_RESOURCE, null) }
            )
            TeacherActionTile(
                title = "Send Message",
                icon = Icons.Default.Chat,
                color = Color(0xFF059669),
                modifier = Modifier.weight(1f),
                onClick = { onOpenModal(TeacherModalType.SEND_MESSAGE, null) }
            )
            TeacherActionTile(
                title = "Analytics",
                icon = Icons.Default.BarChart,
                color = Color(0xFFEA580C),
                modifier = Modifier.weight(1f),
                onClick = { onOpenModal(TeacherModalType.ANALYTICS, null) }
            )
            TeacherActionTile(
                title = "Calendar",
                icon = Icons.Default.CalendarMonth,
                color = Color(0xFF4F46E5),
                modifier = Modifier.weight(1f),
                onClick = { onOpenModal(TeacherModalType.CALENDAR, null) }
            )
        }

        // Pending Tasks & Deadlines
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Pending Evaluations & Deadlines",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            tasks.forEach { task ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Checkbox(
                                checked = task.isCompleted,
                                onCheckedChange = { onToggleTask(task.taskId) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = task.title,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${task.courseName} • Due: ${task.deadline}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        AssistChip(
                            onClick = {},
                            label = { Text(task.priority, fontSize = 11.sp) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (task.priority == "High") Color(0xFFFEE2E2) else Color(0xFFFEF3C7),
                                labelColor = if (task.priority == "High") Color(0xFF991B1B) else Color(0xFF92400E)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderStatItem(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
fun QuickStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            Text(subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ClassScheduleCard(
    classItem: ClassScheduleItem,
    onQrClick: () -> Unit,
    onManualClick: () -> Unit,
    onRescheduleClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = classItem.courseCode,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = classItem.timeSlot,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                AssistChip(
                    onClick = {},
                    label = { Text(classItem.status.name, fontSize = 10.sp) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (classItem.status) {
                            ScheduleStatus.COMPLETED -> Color(0xFFDCFCE7)
                            ScheduleStatus.IN_PROGRESS -> Color(0xFFDBEAFE)
                            ScheduleStatus.RESCHEDULED -> Color(0xFFFEF3C7)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        labelColor = when (classItem.status) {
                            ScheduleStatus.COMPLETED -> Color(0xFF166534)
                            ScheduleStatus.IN_PROGRESS -> Color(0xFF1E40AF)
                            ScheduleStatus.RESCHEDULED -> Color(0xFF92400E)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = classItem.courseName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = classItem.topicTitle,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Room, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Room: ${classItem.roomNumber} • ${classItem.section} • ${classItem.studentCount} Students", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onQrClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Live QR", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onManualClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Checklist, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Manual Roll", fontSize = 11.sp)
                }

                TextButton(
                    onClick = onRescheduleClick,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text("Reschedule", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun TeacherActionTile(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, maxLines = 1)
        }
    }
}
