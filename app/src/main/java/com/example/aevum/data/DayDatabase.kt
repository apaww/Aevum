package com.example.aevum.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Day::class],
    version = 1
)
abstract class DayDatabase: RoomDatabase() {
    abstract fun dayDao(): DayDAO
}