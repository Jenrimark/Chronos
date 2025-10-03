package com.chronos.calendar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.chronos.calendar.data.Event
import com.chronos.calendar.data.EventDatabase
import com.chronos.calendar.data.EventRepository
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * ViewModel for managing Event data
 */
class EventViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: EventRepository
    val allEvents: LiveData<List<Event>>
    
    private val _selectedDate = MutableLiveData<Calendar>()
    val selectedDate: LiveData<Calendar> = _selectedDate
    
    private val _eventsOnSelectedDate = MutableLiveData<List<Event>>()
    val eventsOnSelectedDate: LiveData<List<Event>> = _eventsOnSelectedDate
    
    init {
        val eventDao = EventDatabase.getDatabase(application).eventDao()
        repository = EventRepository(eventDao)
        allEvents = repository.allEvents
        
        // Set initial selected date to today
        _selectedDate.value = Calendar.getInstance()
        loadEventsForSelectedDate()
    }
    
    fun selectDate(date: Calendar) {
        _selectedDate.value = date
        loadEventsForSelectedDate()
    }
    
    private fun loadEventsForSelectedDate() {
        viewModelScope.launch {
            _selectedDate.value?.let { date ->
                val events = repository.getEventsOnDate(date)
                _eventsOnSelectedDate.postValue(events)
            }
        }
    }
    
    fun insert(event: Event, onComplete: ((Long) -> Unit)? = null) {
        viewModelScope.launch {
            val id = repository.insert(event)
            loadEventsForSelectedDate()
            onComplete?.invoke(id)
        }
    }
    
    fun update(event: Event, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            repository.update(event)
            loadEventsForSelectedDate()
            onComplete?.invoke()
        }
    }
    
    fun delete(event: Event, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            repository.delete(event)
            loadEventsForSelectedDate()
            onComplete?.invoke()
        }
    }
    
    fun deleteById(eventId: Long, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            repository.deleteById(eventId)
            loadEventsForSelectedDate()
            onComplete?.invoke()
        }
    }
    
    suspend fun getEventById(eventId: Long): Event? {
        return repository.getEventById(eventId)
    }
    
    fun getEventsInRange(startTime: Long, endTime: Long): LiveData<List<Event>> {
        return repository.getEventsInRange(startTime, endTime)
    }
    
    suspend fun getEventCountOnDate(date: Calendar): Int {
        return repository.getEventCountOnDate(date)
    }
}

