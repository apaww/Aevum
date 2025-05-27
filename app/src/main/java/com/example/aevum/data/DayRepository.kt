package com.example.aevum.data

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

object DayRepository {
    private var database: DayDatabase? = null
    private var dayDao: DayDAO? = null

    @Synchronized
    fun initialize(context: Context) {
        if (database == null) {
            database = Room.databaseBuilder(
                        context.applicationContext,
                        DayDatabase::class.java,
                        "days_database.db"
                    ).fallbackToDestructiveMigration(false)
                .build()
            dayDao = database?.dayDao()
        }
    }

    private fun requireDao(): DayDAO {
        return dayDao ?: throw IllegalStateException("DayRepository not initialized. Call initialize() first.")
    }

    // Create or update a day with all data
    suspend fun createOrUpdateDay(
        day: Int,
        tasks: List<String> = emptyList(),
        tasksDone: List<Boolean> = emptyList(),
        phoneUnlocks: List<String> = emptyList()
    ) {
        withContext(Dispatchers.IO) {
            requireDao().upsertDay(
                Day(
                    day = day,
                    tasksNames = tasks.joinToString("_"),
                    tasksDone = tasksDone.joinToString("") { if (it) "1" else "0" },
                    phoneUnlocks = phoneUnlocks.joinToString("_")
                )
            )
        }
    }

    // Add new tasks to an existing day
    suspend fun addTasks(day: Int, newTasks: List<String>) {
        withContext(Dispatchers.IO) {
            val existingDay = requireDao().getDay(day) ?: run {
                createOrUpdateDay(day = day, tasks = newTasks, tasksDone = List(newTasks.size) { false })
                return@withContext
            }

            val currentTasks = existingDay.tasksNames.split("_").filter { it.isNotEmpty() }.toMutableList()
            currentTasks.addAll(newTasks)

            val currentTasksDone = existingDay.tasksDone.map { it == '1' }.toMutableList()
            currentTasksDone.addAll(List(newTasks.size) { false })

            createOrUpdateDay(
                day = day,
                tasks = currentTasks,
                tasksDone = currentTasksDone,
                phoneUnlocks = existingDay.phoneUnlocks.split("_").filter { it.isNotEmpty() }
            )
        }
    }

    // Add new phone unlock reasons
    suspend fun addPhoneUnlocks(day: Int, unlockReasons: List<String>) {
        withContext(Dispatchers.IO) {
            val existingDay = requireDao().getDay(day) ?: run {
                createOrUpdateDay(day = day, phoneUnlocks = unlockReasons)
                return@withContext
            }

            val currentUnlocks = existingDay.phoneUnlocks.split("_").filter { it.isNotEmpty() }.toMutableList()
            currentUnlocks.addAll(unlockReasons)

            createOrUpdateDay(
                day = day,
                tasks = existingDay.tasksNames.split("_").filter { it.isNotEmpty() },
                tasksDone = existingDay.tasksDone.map { it == '1' },
                phoneUnlocks = currentUnlocks
            )
        }
    }

    // Update task completion status
    suspend fun updateTaskDone(day: Int, taskIndex: Int, isDone: Boolean) {
        withContext(Dispatchers.IO) {
            val existingDay = requireDao().getDay(day) ?: return@withContext

            val tasksDone = existingDay.tasksDone.toMutableList()
            if (taskIndex in tasksDone.indices) {
                tasksDone[taskIndex] = if (isDone) '1' else '0'
                requireDao().upsertDay(existingDay.copy(tasksDone = tasksDone.joinToString("")))
            }
        }
    }

    // Get all days with converted data
    fun getAllDays(): Flow<List<DayData>> {
        return requireDao().getDays().map { days ->
            days.map { day ->
                DayData(
                    day = day.day,
                    tasks = day.tasksNames.split("_").filter { it.isNotEmpty() },
                    tasksDone = day.tasksDone.map { it == '1' },
                    phoneUnlocks = day.phoneUnlocks.split("_").filter { it.isNotEmpty() }
                )
            }
        }
    }

    // Get a specific day with converted data
    suspend fun getDay(day: Int): DayData? {
        return withContext(Dispatchers.IO) {
            requireDao().getDay(day)?.let { dayEntity ->
                DayData(
                    day = dayEntity.day,
                    tasks = dayEntity.tasksNames.split("_").filter { it.isNotEmpty() },
                    tasksDone = dayEntity.tasksDone.map { it == '1' },
                    phoneUnlocks = dayEntity.phoneUnlocks.split("_").filter { it.isNotEmpty() }
                )
            }
        }
    }

    suspend fun getDayTasks(day: Int): List<TaskItem> {
        return withContext(Dispatchers.IO) {
            requireDao().getDay(day)?.let { dayEntity ->
                DayData(
                    day = dayEntity.day,
                    tasks = dayEntity.tasksNames.split("_").filter { it.isNotEmpty() },
                    tasksDone = dayEntity.tasksDone.map { it == '1' },
                    phoneUnlocks = dayEntity.phoneUnlocks.split("_").filter { it.isNotEmpty() }
                ).toTaskItems()
            } ?: emptyList()
        }
    }

    suspend fun getOrCreateDay(day: Int): DayRepository.DayData {
        return requireDao().getDay(day)?.let { dayEntity ->
            DayRepository.DayData(
                day = dayEntity.day,
                tasks = dayEntity.tasksNames.split("_").filter { it.isNotEmpty() },
                tasksDone = dayEntity.tasksDone.map { it == '1' },
                phoneUnlocks = dayEntity.phoneUnlocks.split("_").filter { it.isNotEmpty() }
            )
        } ?: run {
            val newDay = Day(
                day = day,
                tasksNames = "",
                tasksDone = "",
                phoneUnlocks = ""
            )
            requireDao().upsertDay(newDay)
            DayRepository.DayData(
                day = newDay.day,
                tasks = emptyList(),
                tasksDone = emptyList(),
                phoneUnlocks = emptyList()
            )
        }
    }

    private fun DayData.toTaskItems(): List<TaskItem> {
        return this.tasks.zip(this.tasksDone) { name, isDone ->
            TaskItem(
                isDone = isDone,
                name = name
            )
        }
    }

    // Data class for converted day information
    data class DayData(
        val day: Int,
        val tasks: List<String>,
        val tasksDone: List<Boolean>,
        val phoneUnlocks: List<String>
    )
}