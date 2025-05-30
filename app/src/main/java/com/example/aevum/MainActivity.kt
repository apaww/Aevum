package com.example.aevum

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.aevum.data.DayRepository
import com.example.aevum.data.SettingsViewModel
import com.example.aevum.services.LanguageHelper
import com.example.aevum.services.PhoneUnlockTracker
import com.example.aevum.ui.theme.AevumTheme
import com.example.aevum.ui.theme.ThemeManager

class MainActivity : ComponentActivity() {
    private lateinit var phoneUnlockTracker: PhoneUnlockTracker

    override fun attachBaseContext(newBase: Context) {
        val sharedPrefs = newBase.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val langCode = sharedPrefs.getString("language", "en") ?: "en"
        super.attachBaseContext(LanguageHelper.setLocale(newBase, langCode))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DayRepository.initialize(this)
        phoneUnlockTracker = PhoneUnlockTracker(this)
        enableEdgeToEdge()
        setContent {
            AevumTheme {
                AevumApp()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (hasPhoneStatePermission()) {
            phoneUnlockTracker.startTracking()
        }
    }

    override fun onPause() {
        super.onPause()
        phoneUnlockTracker.stopTracking()
    }

    private fun hasPhoneStatePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED
    }
}

@Composable
fun AevumApp() {
    val context = LocalContext.current
    val viewModel = remember { SettingsViewModel(context) }

    // Initialize theme on app start
    LaunchedEffect(Unit) {
        ThemeManager.updateTheme(viewModel.currentTheme)
    }

    AevumTheme(
        darkTheme = ThemeManager.shouldUseDarkTheme()
    ) {
        MainScreen()
    }
}