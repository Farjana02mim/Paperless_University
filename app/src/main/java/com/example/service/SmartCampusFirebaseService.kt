package com.example.service

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.data.local.AppDatabase
import com.example.data.local.NotificationFcmEntity
import com.example.domain.model.NotificationType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmartCampusFirebaseService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        fun handleIncomingCampusNotification(
            context: Context,
            title: String,
            message: String,
            noticeId: String = "",
            typeStr: String = NotificationType.NEW_NOTICE.name,
            receiverRole: String = "All"
        ) {
            val channelId = when (typeStr) {
                NotificationType.EMERGENCY_ALERT.name -> NotificationChannelHelper.CHANNEL_EMERGENCY
                NotificationType.EXAM_ROUTINE.name, NotificationType.RESULT_PUBLISHED.name -> NotificationChannelHelper.CHANNEL_CRITICAL
                NotificationType.NEW_NOTICE.name, NotificationType.ASSIGNMENT_DEADLINE.name -> NotificationChannelHelper.CHANNEL_ACADEMIC
                else -> NotificationChannelHelper.CHANNEL_GENERAL
            }

            val notificationEntity = NotificationFcmEntity(
                notificationId = System.currentTimeMillis().toString(),
                title = title,
                message = message,
                type = typeStr,
                receiverRole = receiverRole,
                receiverUid = "",
                noticeId = noticeId,
                isRead = false,
                createdAt = System.currentTimeMillis(),
                actionType = "VIEW_NOTICE",
                imageUrl = "",
                deepLink = "smartcampus://notice/$noticeId"
            )

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    AppDatabase.getInstance(context.applicationContext).noticeBoardDao().insertNotification(notificationEntity)
                } catch (_: Exception) { }
            }

            showNotification(context, title, message, noticeId, channelId)
        }

        private fun showNotification(
            context: Context,
            title: String,
            message: String,
            noticeId: String,
            channelId: String
        ) {
            NotificationChannelHelper.createNotificationChannels(context)

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("notice_id", noticeId)
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                noticeId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            try {
                val manager = NotificationManagerCompat.from(context)
                manager.notify(System.currentTimeMillis().toInt(), builder.build())
            } catch (_: SecurityException) { }
        }
    }
}
