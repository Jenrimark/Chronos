package com.chronos.calendar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.chronos.calendar.databinding.FragmentDayViewBinding
import com.chronos.calendar.viewmodel.EventViewModel
import kotlinx.coroutines.launch
import java.util.Calendar

class DayViewFragment : Fragment() {
    
    private var _binding: FragmentDayViewBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: EventViewModel by activityViewModels()
    private var dayOffset: Int = 0
    
    companion object {
        private const val ARG_DAY_OFFSET = "day_offset"
        
        fun newInstance(dayOffset: Int) = DayViewFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_DAY_OFFSET, dayOffset)
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dayOffset = arguments?.getInt(ARG_DAY_OFFSET) ?: 0
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDayViewBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDayView()
    }
    
    private fun setupDayView() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, dayOffset)
        
        binding.eventsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        val adapter = EventListAdapter { event ->
            val bottomSheet = EventBottomSheetFragment.newInstance(event.id)
            bottomSheet.show(parentFragmentManager, "EventBottomSheet")
        }
        
        binding.eventsRecyclerView.adapter = adapter
        
        // Get events for this day
        lifecycleScope.launch {
            val events = viewModel.getEventsInRange(
                getStartOfDay(calendar).timeInMillis,
                getEndOfDay(calendar).timeInMillis
            )
            events.observe(viewLifecycleOwner) { eventList ->
                adapter.submitList(eventList)
            }
        }
    }
    
    private fun getStartOfDay(calendar: Calendar): Calendar {
        val start = calendar.clone() as Calendar
        start.set(Calendar.HOUR_OF_DAY, 0)
        start.set(Calendar.MINUTE, 0)
        start.set(Calendar.SECOND, 0)
        start.set(Calendar.MILLISECOND, 0)
        return start
    }
    
    private fun getEndOfDay(calendar: Calendar): Calendar {
        val end = calendar.clone() as Calendar
        end.set(Calendar.HOUR_OF_DAY, 23)
        end.set(Calendar.MINUTE, 59)
        end.set(Calendar.SECOND, 59)
        end.set(Calendar.MILLISECOND, 999)
        return end
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

