package com.chronos.calendar

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.chronos.calendar.databinding.ActivityMainBinding
import com.chronos.calendar.ui.CalendarViewMode
import com.chronos.calendar.ui.CalendarViewPagerAdapter
import com.chronos.calendar.ui.EventBottomSheetFragment
import com.chronos.calendar.viewmodel.EventViewModel
import com.google.android.material.tabs.TabLayoutMediator
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: EventViewModel
    private var currentViewMode = CalendarViewMode.MONTH
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        
        viewModel = ViewModelProvider(this)[EventViewModel::class.java]
        
        setupViewPager()
        setupFab()
        observeViewModel()
    }
    
    private fun setupViewPager() {
        val adapter = CalendarViewPagerAdapter(this, currentViewMode)
        binding.viewPager.adapter = adapter
        binding.viewPager.setCurrentItem(CalendarViewPagerAdapter.START_POSITION, false)
        
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            // Tab text will be updated by the fragments
        }.attach()
        
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateTitle(position)
            }
        })
        
        updateTitle(CalendarViewPagerAdapter.START_POSITION)
    }
    
    private fun setupFab() {
        binding.fab.setOnClickListener {
            val bottomSheet = EventBottomSheetFragment()
            bottomSheet.show(supportFragmentManager, "EventBottomSheet")
        }
    }
    
    private fun observeViewModel() {
        viewModel.selectedDate.observe(this) { date ->
            updateTitle(binding.viewPager.currentItem)
        }
    }
    
    private fun updateTitle(position: Int) {
        val offset = position - CalendarViewPagerAdapter.START_POSITION
        val calendar = Calendar.getInstance()
        
        when (currentViewMode) {
            CalendarViewMode.MONTH -> {
                calendar.add(Calendar.MONTH, offset)
                supportActionBar?.title = "${calendar.get(Calendar.YEAR)}年 ${calendar.get(Calendar.MONTH) + 1}月"
            }
            CalendarViewMode.WEEK -> {
                calendar.add(Calendar.WEEK_OF_YEAR, offset)
                supportActionBar?.title = "${calendar.get(Calendar.YEAR)}年 第${calendar.get(Calendar.WEEK_OF_YEAR)}周"
            }
            CalendarViewMode.DAY -> {
                calendar.add(Calendar.DAY_OF_YEAR, offset)
                supportActionBar?.title = "${calendar.get(Calendar.YEAR)}年 ${calendar.get(Calendar.MONTH) + 1}月 ${calendar.get(Calendar.DAY_OF_MONTH)}日"
            }
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_month_view -> {
                switchViewMode(CalendarViewMode.MONTH)
                true
            }
            R.id.action_week_view -> {
                switchViewMode(CalendarViewMode.WEEK)
                true
            }
            R.id.action_day_view -> {
                switchViewMode(CalendarViewMode.DAY)
                true
            }
            R.id.action_today -> {
                goToToday()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun switchViewMode(mode: CalendarViewMode) {
        if (currentViewMode != mode) {
            currentViewMode = mode
            setupViewPager()
        }
    }
    
    private fun goToToday() {
        binding.viewPager.setCurrentItem(CalendarViewPagerAdapter.START_POSITION, true)
        viewModel.selectDate(Calendar.getInstance())
    }
}

