package com.example.aevum.data

data class DaySummary(
    val day: Int,              // YYYYMMDD format
    val dayName: String,       // "Monday", "Tuesday", etc.
    val tasksCompleted: Int,   // Number of completed tasks
    val totalTasks: Int,       // Total number of tasks
    val phoneUnlocks: Int      // Number of phone unlocks
)