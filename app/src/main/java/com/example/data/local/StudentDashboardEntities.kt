package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_notices")
data class NoticeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val priority: String,
    val publishedTime: String,
    val isRead: Boolean,
    val details: String
)

@Entity(tableName = "cached_schedules")
data class ScheduleEntity(
    @PrimaryKey val id: String,
    val subject: String,
    val teacher: String,
    val time: String,
    val room: String,
    val status: String
)

@Entity(tableName = "cached_assignments")
data class AssignmentEntity(
    @PrimaryKey val id: String,
    val subject: String,
    val title: String,
    val deadline: String,
    val remainingDays: Int,
    val submissionStatus: String,
    val progressPercent: Float
)

@Entity(tableName = "cached_notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val category: String,
    val timestamp: String,
    val isRead: Boolean
)

@Entity(tableName = "cached_courses")
data class CourseEntity(
    @PrimaryKey val code: String,
    val title: String,
    val instructor: String,
    val credits: Int,
    val progress: Float,
    val currentGrade: String
)

@Entity(tableName = "cached_library")
data class LibraryEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val category: String,
    val isAvailable: Boolean,
    val copies: Int
)
