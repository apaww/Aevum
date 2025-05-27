package com.example.aevum.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface DayDAO {
    @Upsert
    suspend fun upsertDay(day: Day)

    @Query("SELECT * FROM days ORDER BY day DESC")
    fun getDays(): Flow<List<Day>>

    @Query("SELECT * FROM days WHERE day = :day")
    suspend fun getDay(day: Int): Day?
}