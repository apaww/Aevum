package com.example.aevum.services

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.example.aevum.data.DayRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class PhoneUnlockTracker(private val context: Context) {
    private val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    private val dateFormat = SimpleDateFormat("yyyyMMdd")
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var job: Job? = null

    private val unlockReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_USER_PRESENT) {
                job?.cancel() // Cancel previous job if it's still running
                job = coroutineScope.launch {
                    recordPhoneUnlock()
                }
            }
        }
    }

    fun startTracking() {
        context.registerReceiver(unlockReceiver, IntentFilter(Intent.ACTION_USER_PRESENT))
    }

    fun stopTracking() {
        try {
            context.unregisterReceiver(unlockReceiver)
            job?.cancel()
        } catch (e: IllegalArgumentException) {
            // Receiver wasn't registered
        }
    }

    private suspend fun recordPhoneUnlock() {
        val currentDate = dateFormat.format(Date()).toInt()
        DayRepository.addPhoneUnlocks(
            day = currentDate,
            unlockReasons = listOf(System.currentTimeMillis().toString())
        )
    }
}