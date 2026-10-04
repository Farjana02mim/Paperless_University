package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build

object NotificationChannelHelper {

    const val CHANNEL_EMERGENCY = "channel_emergency_alerts"
    const val CHANNEL_CRITICAL = "channel_critical_notices"
    const val CHANNEL_ACADEMIC = "channel_academic_updates"
    const val CHANNEL_GENERAL = "channel_general_notifications"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // 1. Emergency Channel (Bypasses DND, max priority, custom sound/vibration)
            val emergencyChannel = NotificationChannel(
                CHANNEL_EMERGENCY,
                "🚨 Emergency & Safety Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical campus emergency, lockdown, and safety announcements."
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 500)
                setBypassDnd(true)
            }

            // 2. Critical Notices Channel (Exam, Admission, Fee deadlines)
            val criticalChannel = NotificationChannel(
                CHANNEL_CRITICAL,
                "⚡ Critical Notices",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High-priority academic and administrative notices."
                enableLights(true)
                enableVibration(true)
            }

            // 3. Academic Channel (Routine, Course material, Assignments)
            val academicChannel = NotificationChannel(
                CHANNEL_ACADEMIC,
                "📚 Academic Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily class routine changes, assignment notices, and department news."
            }

            // 4. General Channel (Events, Clubs, Sports, Library)
            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL,
                "📢 Campus Life & Events",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Extracurricular, club events, sports, and general announcements."
            }

            notificationManager.createNotificationChannels(
                listOf(emergencyChannel, criticalChannel, academicChannel, generalChannel)
            )
        }
    }
}
