package com.chronos.calendar.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chronos.calendar.R
import com.chronos.calendar.data.Event
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventListAdapter(
    private val onEventClick: (Event) -> Unit
) : ListAdapter<Event, EventListAdapter.EventViewHolder>(EventDiffCallback()) {
    
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("MM月dd日", Locale.getDefault())
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.eventTitle)
        private val timeText: TextView = itemView.findViewById(R.id.eventTime)
        private val locationText: TextView = itemView.findViewById(R.id.eventLocation)
        private val colorIndicator: View = itemView.findViewById(R.id.colorIndicator)
        
        fun bind(event: Event) {
            titleText.text = event.title
            
            val timeStr = if (event.isAllDay) {
                "全天"
            } else {
                "${timeFormat.format(Date(event.startTime))} - ${timeFormat.format(Date(event.endTime))}"
            }
            timeText.text = timeStr
            
            if (event.location.isNotEmpty()) {
                locationText.visibility = View.VISIBLE
                locationText.text = event.location
            } else {
                locationText.visibility = View.GONE
            }
            
            colorIndicator.setBackgroundColor(event.color)
            
            itemView.setOnClickListener {
                onEventClick(event)
            }
        }
    }
    
    class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }
}

