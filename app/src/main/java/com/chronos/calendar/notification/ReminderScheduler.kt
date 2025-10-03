package com.chronos.calendar.notification

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Scheduler for periodic reminder checks
 */
object ReminderScheduler {
    
    private const val WORK_NAME = "event_reminder_check"
    
    fun scheduleReminderCheck(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<EventReminderWorker>(
            15, TimeUnit.MINUTES // Check every 15 minutes
        ).build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
    
    fun cancelReminderCheck(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}

