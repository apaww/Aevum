package com.example.aevum.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "days")
data class Day(
    @PrimaryKey
    val day: Int,  // Format: YYYYMMDD
    val tasksNames: String = "",      // Stored as "task1_task2_task3"
    val tasksDone: String = "",       // Stored as "101" (1=true, 0=false)
    val phoneUnlocks: String = ""     // Stored as "reason1_reason2_reason3"
)