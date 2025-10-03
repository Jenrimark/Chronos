package com.chronos.calendar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.chronos.calendar.databinding.FragmentWeekViewBinding
import com.chronos.calendar.viewmodel.EventViewModel
import java.util.Calendar

class WeekViewFragment : Fragment() {
    
    private var _binding: FragmentWeekViewBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: EventViewModel by activityViewModels()
    private var weekOffset: Int = 0
    
    companion object {
        private const val ARG_WEEK_OFFSET = "week_offset"
        
        fun newInstance(weekOffset: Int) = WeekViewFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_WEEK_OFFSET, weekOffset)
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weekOffset = arguments?.getInt(ARG_WEEK_OFFSET) ?: 0
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeekViewBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWeekView()
    }
    
    private fun setupWeekView() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_YEAR, weekOffset)
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        
        binding.eventsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        val adapter = EventListAdapter { event ->
            val bottomSheet = EventBottomSheetFragment.newInstance(event.id)
            bottomSheet.show(parentFragmentManager, "EventBottomSheet")
        }
        
        binding.eventsRecyclerView.adapter = adapter
        
        // Get events for this week
        val weekEnd = calendar.clone() as Calendar
        weekEnd.add(Calendar.DAY_OF_YEAR, 7)
        
        viewModel.getEventsInRange(calendar.timeInMillis, weekEnd.timeInMillis)
            .observe(viewLifecycleOwner) { events ->
                adapter.submitList(events)
            }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

