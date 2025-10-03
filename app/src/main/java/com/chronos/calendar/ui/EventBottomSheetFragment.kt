package com.chronos.calendar.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.chronos.calendar.R
import com.chronos.calendar.data.Event
import com.chronos.calendar.data.ReminderPresets
import com.chronos.calendar.databinding.FragmentEventBottomSheetBinding
import com.chronos.calendar.viewmodel.EventViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EventBottomSheetFragment : BottomSheetDialogFragment() {
    
    private var _binding: FragmentEventBottomSheetBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: EventViewModel by activityViewModels()
    
    private var eventId: Long? = null
    private var currentEvent: Event? = null
    
    private val startCalendar = Calendar.getInstance()
    private val endCalendar = Calendar.getInstance().apply {
        add(Calendar.HOUR_OF_DAY, 1)
    }
    
    private val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    
    private var selectedReminderMinutes = ReminderPresets.NONE
    
    companion object {
        private const val ARG_EVENT_ID = "event_id"
        
        fun newInstance(eventId: Long? = null) = EventBottomSheetFragment().apply {
            arguments = Bundle().apply {
                eventId?.let { putLong(ARG_EVENT_ID, it) }
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventId = arguments?.getLong(ARG_EVENT_ID)
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupReminderSpinner()
        loadEvent()
        setupListeners()
        updateDateTimeDisplays()
    }
    
    private fun setupReminderSpinner() {
        val reminders = resources.getStringArray(R.array.reminder_options)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, reminders)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.reminderSpinner.adapter = adapter
    }
    
    private fun loadEvent() {
        eventId?.let { id ->
            lifecycleScope.launch {
                val event = viewModel.getEventById(id)
                event?.let {
                    currentEvent = it
                    populateFields(it)
                }
            }
        }
    }
    
    private fun populateFields(event: Event) {
        binding.titleInput.setText(event.title)
        binding.descriptionInput.setText(event.description)
        binding.locationInput.setText(event.location)
        
        startCalendar.timeInMillis = event.startTime
        endCalendar.timeInMillis = event.endTime
        
        binding.allDayCheckbox.isChecked = event.isAllDay
        
        selectedReminderMinutes = event.reminderMinutes
        binding.reminderSpinner.setSelection(getReminderPosition(event.reminderMinutes))
        
        binding.saveButton.text = getString(R.string.save_event)
        binding.deleteButton.visibility = View.VISIBLE
        
        updateDateTimeDisplays()
    }
    
    private fun getReminderPosition(minutes: Int): Int {
        return when (minutes) {
            ReminderPresets.NONE -> 0
            ReminderPresets.AT_TIME -> 1
            ReminderPresets.MIN_5 -> 2
            ReminderPresets.MIN_15 -> 3
            ReminderPresets.MIN_30 -> 4
            ReminderPresets.HOUR_1 -> 5
            ReminderPresets.DAY_1 -> 6
            else -> 0
        }
    }
    
    private fun getReminderMinutes(position: Int): Int {
        return when (position) {
            0 -> ReminderPresets.NONE
            1 -> ReminderPresets.AT_TIME
            2 -> ReminderPresets.MIN_5
            3 -> ReminderPresets.MIN_15
            4 -> ReminderPresets.MIN_30
            5 -> ReminderPresets.HOUR_1
            6 -> ReminderPresets.DAY_1
            else -> ReminderPresets.NONE
        }
    }
    
    private fun setupListeners() {
        binding.startDateButton.setOnClickListener {
            showDatePicker(startCalendar) {
                updateDateTimeDisplays()
            }
        }
        
        binding.startTimeButton.setOnClickListener {
            showTimePicker(startCalendar) {
                updateDateTimeDisplays()
            }
        }
        
        binding.endDateButton.setOnClickListener {
            showDatePicker(endCalendar) {
                updateDateTimeDisplays()
            }
        }
        
        binding.endTimeButton.setOnClickListener {
            showTimePicker(endCalendar) {
                updateDateTimeDisplays()
            }
        }
        
        binding.allDayCheckbox.setOnCheckedChangeListener { _, isChecked ->
            binding.startTimeButton.visibility = if (isChecked) View.GONE else View.VISIBLE
            binding.endTimeButton.visibility = if (isChecked) View.GONE else View.VISIBLE
        }
        
        binding.saveButton.setOnClickListener {
            saveEvent()
        }
        
        binding.deleteButton.setOnClickListener {
            showDeleteConfirmation()
        }
    }
    
    private fun showDatePicker(calendar: Calendar, onSet: () -> Unit) {
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                onSet()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    
    private fun showTimePicker(calendar: Calendar, onSet: () -> Unit) {
        TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                onSet()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }
    
    private fun updateDateTimeDisplays() {
        binding.startDateButton.text = dateFormat.format(startCalendar.time)
        binding.startTimeButton.text = timeFormat.format(startCalendar.time)
        binding.endDateButton.text = dateFormat.format(endCalendar.time)
        binding.endTimeButton.text = timeFormat.format(endCalendar.time)
    }
    
    private fun saveEvent() {
        val title = binding.titleInput.text.toString().trim()
        
        if (title.isEmpty()) {
            Toast.makeText(requireContext(), R.string.error_title_required, Toast.LENGTH_SHORT).show()
            return
        }
        
        if (endCalendar.timeInMillis <= startCalendar.timeInMillis) {
            Toast.makeText(requireContext(), R.string.error_invalid_time, Toast.LENGTH_SHORT).show()
            return
        }
        
        val event = Event(
            id = eventId ?: 0,
            title = title,
            description = binding.descriptionInput.text.toString().trim(),
            location = binding.locationInput.text.toString().trim(),
            startTime = startCalendar.timeInMillis,
            endTime = endCalendar.timeInMillis,
            isAllDay = binding.allDayCheckbox.isChecked,
            reminderMinutes = getReminderMinutes(binding.reminderSpinner.selectedItemPosition)
        )
        
        if (eventId == null) {
            viewModel.insert(event) {
                Toast.makeText(requireContext(), R.string.event_added, Toast.LENGTH_SHORT).show()
                dismiss()
            }
        } else {
            viewModel.update(event) {
                Toast.makeText(requireContext(), R.string.event_updated, Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }
    }
    
    private fun showDeleteConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_event)
            .setMessage(R.string.confirm_delete)
            .setPositiveButton(R.string.delete_event) { _, _ ->
                eventId?.let { id ->
                    viewModel.deleteById(id) {
                        Toast.makeText(requireContext(), R.string.event_deleted, Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

