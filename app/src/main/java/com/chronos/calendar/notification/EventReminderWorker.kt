package com.chronos.calendar.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.chronos.calendar.MainActivity
import com.chronos.calendar.R
import com.chronos.calendar.data.EventDatabase
import com.chronos.calendar.data.EventRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Worker for checking and sending event reminders
 */
class EventReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    companion object {
        const val CHANNEL_ID = "event_reminders"
        const val CHANNEL_NAME = "日程提醒"
        private const val NOTIFICATION_ID_BASE = 1000
        
        private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    }
    
    override suspend fun doWork(): Result {
        val repository = EventRepository(
            EventDatabase.getDatabase(applicationContext).eventDao()
        )
        
        createNotificationChannel()
        
        val upcomingEvents = repository.getUpcomingEventsWithReminders()
        val currentTime = System.currentTimeMillis()
        
        upcomingEvents.forEach { event ->
            val reminderTime = if (event.reminderMinutes > 0) {
                event.startTime - (event.reminderMinutes * 60 * 1000)
            } else {
                event.startTime
            }
            
            // Check if it's time to send reminder (within 1 minute window)
            if (reminderTime in currentTime..(currentTime + 60000)) {
                sendNotification(event.id.toInt(), event.title, event.startTime, event.location)
            }
        }
        
        return Result.success()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "用于日程提醒通知"
                enableVibration(true)
            }
            
            val notificationManager = applicationContext.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun sendNotification(eventId: Int, title: String, startTime: Long, location: String) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            eventId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val contentText = buildString {
            append(timeFormat.format(Date(startTime)))
            if (location.isNotEmpty()) {
                append(" · $location")
            }
        }
        
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        val notificationManager = applicationContext.getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID_BASE + eventId, notification)
    }
}

