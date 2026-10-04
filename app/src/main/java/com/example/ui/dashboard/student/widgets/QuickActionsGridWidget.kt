package com.example.ui.dashboard.student.widgets

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CoPresent
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class QuickActionItem(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val actionKey: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickActionsGridWidget(
    onActionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val quickActions = remember {
        listOf(
            QuickActionItem("Digital ID", Icons.Default.Badge, Color(0xFF1E88E5), "digital_id"),
            QuickActionItem("Attendance", Icons.Default.CoPresent, Color(0xFF43A047), "attendance"),
            QuickActionItem("Assignments", Icons.Default.Assignment, Color(0xFFFB8C00), "assignments"),
            QuickActionItem("Results", Icons.Default.Grade, Color(0xFF8E24AA), "results"),
            QuickActionItem("Fee Payment", Icons.Default.Payment, Color(0xFF00ACC1), "fee_payment"),
            QuickActionItem("Admission", Icons.Default.School, Color(0xFF00796B), "admission"),
            QuickActionItem("Class Routine", Icons.Default.Schedule, Color(0xFF3949AB), "class_routine"),
            QuickActionItem("Exam Routine", Icons.Default.EventNote, Color(0xFFD81B60), "exam_routine"),
            QuickActionItem("Library", Icons.Default.Book, Color(0xFF5D4037), "library"),
            QuickActionItem("Calendar", Icons.Default.CalendarMonth, Color(0xFF00897B), "calendar"),
            QuickActionItem("Notices", Icons.Default.Notifications, Color(0xFFF4511E), "notices"),
            QuickActionItem("Career Portal", Icons.Default.Work, Color(0xFF1565C0), "career"),
            QuickActionItem("Downloads", Icons.Default.Download, Color(0xFF546E7A), "downloads"),
            QuickActionItem("Emergency", Icons.Default.PhoneInTalk, Color(0xFFE53935), "emergency")
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            maxItemsInEachRow = 4
        ) {
            quickActions.forEach { action ->
                QuickActionCard(
                    action = action,
                    onClick = { onActionClick(action.actionKey) },
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .weight(1f)
                )
            }
        }
    }
}

@Composable
fun QuickActionCard(
    action: QuickActionItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = tween(durationMillis = 100),
        label = "scale_anim"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    isPressed = true
                    onClick()
                    isPressed = false
                }
            )
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(action.color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.title,
                tint = action.color,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = action.title,
            style = MaterialTheme.typography.labelMedium,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}
