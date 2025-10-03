package com.chronos.calendar.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

/**
 * Event entity class following RFC5545 (iCalendar) specifications
 * 
 * Core properties based on RFC5545:
 * - UID: Unique identifier
 * - DTSTART: Start date/time
 * - DTEND: End date/time
 * - SUMMARY: Title/summary
 * - DESCRIPTION: Description
 * - LOCATION: Location
 * - STATUS: Event status
 * - RRULE: Recurrence rule (for future expansion)
 */
@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // RFC5545: SUMMARY - Short summary or subject
    val title: String,
    
    // RFC5545: DESCRIPTION - More complete description
    val description: String = "",
    
    // RFC5545: LOCATION - Intended venue
    val location: String = "",
    
    // RFC5545: DTSTART - Start date-time
    val startTime: Long,
    
    // RFC5545: DTEND - End date-time
    val endTime: Long,
    
    // All-day event flag
    val isAllDay: Boolean = false,
    
    // RFC5545: STATUS - Overall status (TENTATIVE, CONFIRMED, CANCELLED)
    val status: EventStatus = EventStatus.CONFIRMED,
    
    // Reminder time in minutes before event (0 = no reminder)
    val reminderMinutes: Int = 0,
    
    // RFC5545: RRULE - Recurrence rule (for future expansion)
    val recurrenceRule: String? = null,
    
    // Creation and modification timestamps
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    
    // Color for event display
    val color: Int = 0xFF2196F3.toInt() // Default blue
)

enum class EventStatus {
    TENTATIVE,
    CONFIRMED,
    CANCELLED
}

/**
 * Reminder presets in minutes
 */
object ReminderPresets {
    const val NONE = 0
    const val AT_TIME = -1  // Special value for "at event time"
    const val MIN_5 = 5
    const val MIN_15 = 15
    const val MIN_30 = 30
    const val HOUR_1 = 60
    const val DAY_1 = 1440
}

/**
 * Extension functions for Event
 */
fun Event.getStartCalendar(): Calendar {
    return Calendar.getInstance().apply {
        timeInMillis = startTime
    }
}

fun Event.getEndCalendar(): Calendar {
    return Calendar.getInstance().apply {
        timeInMillis = endTime
    }
}

fun Event.getDurationMinutes(): Long {
    return (endTime - startTime) / (1000 * 60)
}

fun Event.isOnDate(date: Calendar): Boolean {
    val eventStart = getStartCalendar()
    return eventStart.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
           eventStart.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
}

fun Event.isInDateRange(startDate: Calendar, endDate: Calendar): Boolean {
    val eventStartTime = startTime
    val eventEndTime = endTime
    val rangeStart = startDate.timeInMillis
    val rangeEnd = endDate.timeInMillis
    
    return eventStartTime < rangeEnd && eventEndTime > rangeStart
}

