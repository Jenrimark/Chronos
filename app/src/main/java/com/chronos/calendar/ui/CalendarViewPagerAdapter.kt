package com.chronos.calendar.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class CalendarViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val viewMode: CalendarViewMode
) : FragmentStateAdapter(fragmentActivity) {
    
    companion object {
        const val START_POSITION = 1000
        private const val TOTAL_PAGES = 2000
    }
    
    override fun getItemCount(): Int = TOTAL_PAGES
    
    override fun createFragment(position: Int): Fragment {
        val offset = position - START_POSITION
        return when (viewMode) {
            CalendarViewMode.MONTH -> MonthViewFragment.newInstance(offset)
            CalendarViewMode.WEEK -> WeekViewFragment.newInstance(offset)
            CalendarViewMode.DAY -> DayViewFragment.newInstance(offset)
        }
    }
}

