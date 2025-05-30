package com.example.aevum.data

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.aevum.R
import com.example.aevum.services.PhoneUnlockTracker
import com.example.aevum.ui.theme.ThemeManager
import com.example.aevum.ui.theme.ThemeType

class SettingsViewModel(context: Context) : ViewModel() {
    private val sharedPrefs = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)

    private val phoneUnlockTracker = PhoneUnlockTracker(context)

    fun startTracking() {
        phoneUnlockTracker.startTracking()
    }

    fun stopTracking() {
        phoneUnlockTracker.stopTracking()
    }

    var currentTheme by mutableStateOf(
        sharedPrefs.getString("theme", "SYSTEM")?.let {
            ThemeType.valueOf(it)
        } ?: ThemeType.SYSTEM
    )
        private set

    fun setTheme(theme: ThemeType) {
        currentTheme = theme
        sharedPrefs.edit().putString("theme", theme.name).apply()
        ThemeManager.updateTheme(theme) // Update the global theme state
    }

    // Timer sound state
    var currentTimerSound by mutableStateOf(sharedPrefs.getInt("timer_sound", R.raw.nothing))
        private set

    fun setTimerSound(soundResId: Int) {
        currentTimerSound = soundResId
        sharedPrefs.edit().putInt("timer_sound", soundResId).apply()
    }

    var hasNotificationPermission by mutableStateOf(
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    )

    var hasPhoneStatePermission by mutableStateOf(
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
    )

    fun requestNotificationPermission(activity: Activity, onResult: (Boolean) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        } else {
            onResult(true) // Permission granted by default on older versions
        }
    }

    fun requestPhoneStatePermission(activity: Activity, onResult: (Boolean) -> Unit) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.READ_PHONE_STATE),
            PHONE_STATE_PERMISSION_REQUEST_CODE
        )
    }

    var currentLanguage by mutableStateOf(
        sharedPrefs.getString("language", "en") ?: "en"
    )

    fun setLanguage(languageCode: String) {
        currentLanguage = languageCode
        sharedPrefs.edit().putString("language", languageCode).apply()
    }

    companion object {
        const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
        const val PHONE_STATE_PERMISSION_REQUEST_CODE = 1002
        val supportedLanguages = mapOf(
            "en" to "English",
            "ru" to "Русский",
            "fr" to "Français",
            "zh" to "中文",
            "de" to "Deutsch",
            "es" to "Español"
        )
    }
}