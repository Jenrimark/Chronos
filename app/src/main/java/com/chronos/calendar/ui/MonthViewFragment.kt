package com.chronos.calendar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.chronos.calendar.databinding.FragmentMonthViewBinding
import com.chronos.calendar.viewmodel.EventViewModel
import java.util.Calendar

class MonthViewFragment : Fragment() {
    
    private var _binding: FragmentMonthViewBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: EventViewModel by activityViewModels()
    private var monthOffset: Int = 0
    
    companion object {
        private const val ARG_MONTH_OFFSET = "month_offset"
        
        fun newInstance(monthOffset: Int) = MonthViewFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_MONTH_OFFSET, monthOffset)
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        monthOffset = arguments?.getInt(ARG_MONTH_OFFSET) ?: 0
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonthViewBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCalendar()
    }
    
    private fun setupCalendar() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, monthOffset)
        
        binding.calendarRecyclerView.layoutManager = GridLayoutManager(requireContext(), 7)
        
        val adapter = MonthCalendarAdapter { date ->
            viewModel.selectDate(date)
            val bottomSheet = EventBottomSheetFragment()
            bottomSheet.show(parentFragmentManager, "EventBottomSheet")
        }
        
        binding.calendarRecyclerView.adapter = adapter
        adapter.setMonth(calendar)
        
        // Load event counts for this month
        viewModel.allEvents.observe(viewLifecycleOwner) { events ->
            adapter.setEvents(events)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

