package com.chronos.calendar.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chronos.calendar.R
import com.chronos.calendar.data.Event
import com.chronos.calendar.data.isOnDate
import java.util.Calendar

class MonthCalendarAdapter(
    private val onDateClick: (Calendar) -> Unit
) : RecyclerView.Adapter<MonthCalendarAdapter.DayViewHolder>() {
    
    private val days = mutableListOf<CalendarDay>()
    private var events = listOf<Event>()
    
    data class CalendarDay(
        val calendar: Calendar,
        val isCurrentMonth: Boolean,
        val isToday: Boolean
    )
    
    fun setMonth(calendar: Calendar) {
        days.clear()
        
        val monthCalendar = calendar.clone() as Calendar
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)
        
        val firstDayOfWeek = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1
        val daysInMonth = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        
        val today = Calendar.getInstance()
        
        // Add days from previous month
        val prevMonth = monthCalendar.clone() as Calendar
        prevMonth.add(Calendar.MONTH, -1)
        val daysInPrevMonth = prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
        
        for (i in firstDayOfWeek - 1 downTo 0) {
            val day = prevMonth.clone() as Calendar
            day.set(Calendar.DAY_OF_MONTH, daysInPrevMonth - i)
            days.add(CalendarDay(day, false, false))
        }
        
        // Add days of current month
        for (day in 1..daysInMonth) {
            val dayCalendar = monthCalendar.clone() as Calendar
            dayCalendar.set(Calendar.DAY_OF_MONTH, day)
            val isToday = isSameDay(dayCalendar, today)
            days.add(CalendarDay(dayCalendar, true, isToday))
        }
        
        // Add days from next month to complete the grid
        val remainingDays = 42 - days.size // 6 rows * 7 days
        val nextMonth = monthCalendar.clone() as Calendar
        nextMonth.add(Calendar.MONTH, 1)
        
        for (day in 1..remainingDays) {
            val dayCalendar = nextMonth.clone() as Calendar
            dayCalendar.set(Calendar.DAY_OF_MONTH, day)
            days.add(CalendarDay(dayCalendar, false, false))
        }
        
        notifyDataSetChanged()
    }
    
    fun setEvents(events: List<Event>) {
        this.events = events
        notifyDataSetChanged()
    }
    
    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return DayViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(days[position])
    }
    
    override fun getItemCount(): Int = days.size
    
    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayText: TextView = itemView.findViewById(R.id.dayText)
        private val eventIndicator: View = itemView.findViewById(R.id.eventIndicator)
        
        fun bind(calendarDay: CalendarDay) {
            val day = calendarDay.calendar.get(Calendar.DAY_OF_MONTH)
            dayText.text = day.toString()
            
            // Style based on state
            when {
                calendarDay.isToday -> {
                    dayText.setTextColor(Color.WHITE)
                    dayText.setBackgroundResource(R.drawable.bg_today)
                }
                !calendarDay.isCurrentMonth -> {
                    dayText.setTextColor(Color.LTGRAY)
                    dayText.setBackgroundColor(Color.TRANSPARENT)
                }
                else -> {
                    dayText.setTextColor(Color.BLACK)
                    dayText.setBackgroundColor(Color.TRANSPARENT)
                }
            }
            
            // Show event indicator
            val hasEvents = events.any { it.isOnDate(calendarDay.calendar) }
            eventIndicator.visibility = if (hasEvents) View.VISIBLE else View.GONE
            
            itemView.setOnClickListener {
                onDateClick(calendarDay.calendar)
            }
        }
    }
}

