package com.chronos.calendar

import android.app.Application
import com.chronos.calendar.notification.ReminderScheduler

class ChronosApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Schedule periodic reminder checks
        ReminderScheduler.scheduleReminderCheck(this)
    }
}

