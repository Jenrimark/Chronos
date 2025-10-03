package com.chronos.calendar.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Data Access Object for Event entity
 */
@Dao
interface EventDao {
    
    @Query("SELECT * FROM events ORDER BY startTime ASC")
    fun getAllEvents(): LiveData<List<Event>>
    
    @Query("SELECT * FROM events WHERE id = :eventId")
    suspend fun getEventById(eventId: Long): Event?
    
    @Query("SELECT * FROM events WHERE startTime >= :startTime AND endTime <= :endTime ORDER BY startTime ASC")
    fun getEventsInRange(startTime: Long, endTime: Long): LiveData<List<Event>>
    
    @Query("SELECT * FROM events WHERE startTime >= :startTime AND startTime < :endTime ORDER BY startTime ASC")
    suspend fun getEventsOnDate(startTime: Long, endTime: Long): List<Event>
    
    @Query("SELECT * FROM events WHERE startTime >= :currentTime AND reminderMinutes > 0 ORDER BY startTime ASC")
    suspend fun getUpcomingEventsWithReminders(currentTime: Long): List<Event>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: Event): Long
    
    @Update
    suspend fun update(event: Event)
    
    @Delete
    suspend fun delete(event: Event)
    
    @Query("DELETE FROM events WHERE id = :eventId")
    suspend fun deleteById(eventId: Long)
    
    @Query("DELETE FROM events")
    suspend fun deleteAll()
    
    @Query("SELECT COUNT(*) FROM events WHERE startTime >= :startTime AND startTime < :endTime")
    suspend fun getEventCountOnDate(startTime: Long, endTime: Long): Int
}

