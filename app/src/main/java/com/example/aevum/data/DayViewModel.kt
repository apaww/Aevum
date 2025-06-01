package com.example.aevum.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aevum.services.PhoneUnlockTracker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class DayViewModel : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _tasks = MutableStateFlow<List<TaskItem>>(emptyList())
    val tasks: StateFlow<List<TaskItem>> = _tasks.asStateFlow()

    private var currentDay: Int = -1

    private val _days = MutableStateFlow<List<DaySummary>>(emptyList())
    val days: StateFlow<List<DaySummary>> = _days.asStateFlow()

    fun loadDays() {
        viewModelScope.launch {
            // Convert database days to summaries
            val allDays = DayRepository.getAllDays().collect { dayDataList ->
                _days.value = dayDataList.map { dayData ->
                    DaySummary(
                        day = dayData.day,
                        dayName = getDayName(dayData.day),
                        tasksCompleted = dayData.tasksDone.count { it },
                        totalTasks = dayData.tasksDone.size,
                        phoneUnlocks = dayData.phoneUnlocks.size
                    )
                }
            }
        }
    }

    private fun getDayName(day: Int): String {
        val date = SimpleDateFormat("yyyyMMdd").parse(day.toString())
        return SimpleDateFormat("EEEE").format(date)
    }

    fun loadTasks(day: Int) {
        currentDay = day
        viewModelScope.launch {
            _tasks.value = DayRepository.getDayTasks(day)
        }
    }

    fun addTask(day: Int, taskName: String) {
        viewModelScope.launch {
            try {
                DayRepository.addTasks(day, listOf(taskName))
                loadTasks(day) // Refresh the task list after adding
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateTaskCompletion(day: Int, taskIndex: Int, isDone: Boolean) {
        viewModelScope.launch {
            try {
                DayRepository.updateTaskDone(day, taskIndex, isDone)
                loadTasks(day) // Refresh the task list after updating
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteTask(day: Int, taskIndex: Int) {
        viewModelScope.launch {
            try {
                // Get current tasks
                val currentDayData = DayRepository.getDay(day) ?: return@launch
                val tasks = currentDayData.tasks.toMutableList()
                val tasksDone = currentDayData.tasksDone.toMutableList()

                // Remove the task
                if (taskIndex in tasks.indices) {
                    tasks.removeAt(taskIndex)
                    tasksDone.removeAt(taskIndex)

                    // Update in database
                    DayRepository.createOrUpdateDay(
                        day = day,
                        tasks = tasks,
                        tasksDone = tasksDone.map { it },
                        phoneUnlocks = currentDayData.phoneUnlocks
                    )

                    // Refresh the list
                    loadTasks(day)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // Clear error message
    fun clearError() {
        _errorMessage.value = null
    }
}