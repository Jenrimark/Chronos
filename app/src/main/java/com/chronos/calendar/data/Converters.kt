package com.chronos.calendar.data

import androidx.room.TypeConverter

/**
 * Type converters for Room database
 */
class Converters {
    
    @TypeConverter
    fun fromEventStatus(status: EventStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun toEventStatus(statusString: String): EventStatus {
        return try {
            EventStatus.valueOf(statusString)
        } catch (e: IllegalArgumentException) {
            EventStatus.CONFIRMED
        }
    }
}

