package com.chronos.calendar.data

import androidx.lifecycle.LiveData
import java.util.Calendar

/**
 * Repository class for managing Event data
 */
class EventRepository(private val eventDao: EventDao) {
    
    val allEvents: LiveData<List<Event>> = eventDao.getAllEvents()
    
    suspend fun getEventById(eventId: Long): Event? {
        return eventDao.getEventById(eventId)
    }
    
    fun getEventsInRange(startTime: Long, endTime: Long): LiveData<List<Event>> {
        return eventDao.getEventsInRange(startTime, endTime)
    }
    
    suspend fun getEventsOnDate(date: Calendar): List<Event> {
        val startOfDay = date.clone() as Calendar
        startOfDay.set(Calendar.HOUR_OF_DAY, 0)
        startOfDay.set(Calendar.MINUTE, 0)
        startOfDay.set(Calendar.SECOND, 0)
        startOfDay.set(Calendar.MILLISECOND, 0)
        
        val endOfDay = startOfDay.clone() as Calendar
        endOfDay.add(Calendar.DAY_OF_MONTH, 1)
        
        return eventDao.getEventsOnDate(startOfDay.timeInMillis, endOfDay.timeInMillis)
    }
    
    suspend fun getUpcomingEventsWithReminders(): List<Event> {
        return eventDao.getUpcomingEventsWithReminders(System.currentTimeMillis())
    }
    
    suspend fun insert(event: Event): Long {
        return eventDao.insert(event.copy(
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        ))
    }
    
    suspend fun update(event: Event) {
        eventDao.update(event.copy(updatedAt = System.currentTimeMillis()))
    }
    
    suspend fun delete(event: Event) {
        eventDao.delete(event)
    }
    
    suspend fun deleteById(eventId: Long) {
        eventDao.deleteById(eventId)
    }
    
    suspend fun getEventCountOnDate(date: Calendar): Int {
        val startOfDay = date.clone() as Calendar
        startOfDay.set(Calendar.HOUR_OF_DAY, 0)
        startOfDay.set(Calendar.MINUTE, 0)
        startOfDay.set(Calendar.SECOND, 0)
        startOfDay.set(Calendar.MILLISECOND, 0)
        
        val endOfDay = startOfDay.clone() as Calendar
        endOfDay.add(Calendar.DAY_OF_MONTH, 1)
        
        return eventDao.getEventCountOnDate(startOfDay.timeInMillis, endOfDay.timeInMillis)
    }
}

