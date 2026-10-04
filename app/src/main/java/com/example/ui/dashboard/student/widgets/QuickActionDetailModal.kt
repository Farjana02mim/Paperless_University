package com.example.ui.dashboard.student.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun QuickActionDetailModal(
    actionKey: String,
    onDismiss: () -> Unit
) {
    val (title, contentText) = when (actionKey) {
        "attendance" -> "Detailed Attendance Log" to "Your overall attendance is 94% across 5 registered courses this semester.\n\n• CSE-301: 96%\n• CSE-302: 92%\n• CSE-305: 90%\n• MAT-204: 95%\n• HUM-102: 98%"
        "results" -> "Semester Grade Report" to "Cumulative GPA: 3.85\n\nCompleted Credits: 96 / 140\n\nSpring 2026 Semester Status: In Progress (Standing: First Class Honors)"
        "fee_payment" -> "Paperless Fee Payment Portal" to "Current Term Tuition Balance: $0.00 (PAID)\n\nReceipt #TXN-994812 confirmed.\nPayment Method: University Auto-Debit"
        "class_routine" -> "Semester Class Routine" to "Monday to Thursday: 09:00 AM - 03:30 PM\n\nRoom 402, Audience Hall B & Computer Lab 3"
        "exam_routine" -> "Spring 2026 Midterm Exam Routine" to "• CSE-301: March 10, 10:00 AM (Hall A)\n• CSE-302: March 12, 02:00 PM (Hall B)\n• CSE-305: March 15, 10:00 AM (Lab 4)\n• MAT-204: March 17, 02:00 PM (Hall C)"
        "calendar" -> "Academic Calendar 2026" to "• Semester Registration: Jan 15 - Jan 25\n• Mid-Term Exams: March 10 - March 18\n• Spring Recess: April 01 - April 07\n• Final Exams: May 20 - June 05"
        "notices" -> "Notice Board Portal" to "You have 3 active campus notices. Check the Latest Notices section on the main dashboard."
        "downloads" -> "Student Downloads & Syllabus" to "Available Documents:\n1. Spring 2026 Academic Syllabus.pdf\n2. Lab Experiment Manual CSE-302.pdf\n3. Library Access Card Form.pdf"
        "emergency" -> "Campus Emergency Contact Hotline" to "24/7 Security Hotline: +1 (800) 555-CAMPUS\nMedical Center: +1 (800) 555-HEALTH\nStudent Care Center: care@university.edu"
        else -> actionKey.replace("_", " ").capitalize() to "Detailed information and portal access for $actionKey."
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        },
        text = {
            Column {
                Text(
                    text = contentText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, shape = RoundedCornerShape(10.dp)) {
                Text("Close")
            }
        }
    )
}
