package com.example.ui.admin.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.example.domain.model.admin.AdminProfile
import com.example.domain.model.admin.AdminSystemMetrics
import com.example.ui.admin.AdminModalType
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryTeal

@Composable
fun AdminDashboardTab(
    profile: AdminProfile,
    metrics: AdminSystemMetrics,
    onOpenModal: (AdminModalType) -> Unit,
    onNavigateTab: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Admin Badge Banner Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Unspecified),
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF831843), Color(0xFF0F172A))
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
                    Text(
                        text = "CENTRAL ERP CONTROL CENTER",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AccentGold,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentGold.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("ROOT ACCESS", color = AccentGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "Admin Photo",
                            modifier = Modifier.size(50.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = profile.fullName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "${profile.adminId} • ${profile.department}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.9f))
                        )
                        Text(
                            text = "System Health: ${metrics.systemHealthPercent}% | Active Alerts: ${metrics.activeAlertsCount}",
                            style = MaterialTheme.typography.bodySmall.copy(color = AccentGold)
                        )
                    }
                }
            }
        }

        // Executive Statistics Grid (13 Counters)
        Text("University Key Operational Counters", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("Total Students", "${metrics.totalStudents}", "Active enrollees", Icons.Default.Groups, Color(0xFF0284C7), Modifier.weight(1f))
            MetricCard("Total Teachers", "${metrics.totalTeachers}", "Faculty members", Icons.Default.School, Color(0xFF0F766E), Modifier.weight(1f))
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("Departments", "${metrics.totalDepartments}", "Academic units", Icons.Default.AccountTree, Color(0xFF7C3AED), Modifier.weight(1f))
            MetricCard("Active Courses", "${metrics.totalCourses}", "Offered subjects", Icons.Default.MenuBook, Color(0xFF16A34A), Modifier.weight(1f))
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("Today Attendance", "${metrics.todayAttendanceRate}%", "Campus check-in", Icons.Default.CheckCircle, Color(0xFF059669), Modifier.weight(1f))
            MetricCard("Today Revenue", "$${metrics.todayPaymentsTotal.toInt()}", "Invoices settled", Icons.Default.Payments, Color(0xFFD97706), Modifier.weight(1f))
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("Pending Approvals", "${metrics.pendingAssignments}", "Assignments & Leaves", Icons.Default.PendingActions, Color(0xFFDC2626), Modifier.weight(1f))
            MetricCard("Admissions", "${metrics.admissionApplicationsCount}", "Applications Fall 26", Icons.Default.HowToReg, Color(0xFF2563EB), Modifier.weight(1f))
        }

        // Quick Control Actions
        Text("Administrative Quick Actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AdminActionTile("Add User", Icons.Default.PersonAdd, Color(0xFF0284C7), Modifier.weight(1f)) { onOpenModal(AdminModalType.CREATE_USER) }
            AdminActionTile("New Dept", Icons.Default.DomainAdd, Color(0xFF7C3AED), Modifier.weight(1f)) { onOpenModal(AdminModalType.CREATE_DEPARTMENT) }
            AdminActionTile("Add Course", Icons.Default.LibraryAdd, Color(0xFF16A34A), Modifier.weight(1f)) { onOpenModal(AdminModalType.CREATE_COURSE) }
            AdminActionTile("Backup", Icons.Default.CloudUpload, Color(0xFFEA580C), Modifier.weight(1f)) { onOpenModal(AdminModalType.CLOUD_BACKUP) }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AdminActionTile("Report", Icons.Default.Assessment, Color(0xFF2563EB), Modifier.weight(1f)) { onOpenModal(AdminModalType.GENERATE_REPORT) }
            AdminActionTile("Settings", Icons.Default.Settings, Color(0xFF0F766E), Modifier.weight(1f)) { onOpenModal(AdminModalType.SYSTEM_SETTINGS) }
            AdminActionTile("AI Predict", Icons.Default.Psychology, Color(0xFFDB2777), Modifier.weight(1f)) { onOpenModal(AdminModalType.AI_INSIGHTS_VIEW) }
            AdminActionTile("Audits", Icons.Default.Shield, Color(0xFF4F46E5), Modifier.weight(1f)) { onNavigateTab(8) }
        }
    }
}

@Composable
fun MetricCard(
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
            Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            Text(subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun AdminActionTile(
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
