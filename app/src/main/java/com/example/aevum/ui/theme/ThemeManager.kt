package com.example.aevum.ui.theme

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

object ThemeManager {
    var currentTheme: ThemeType by mutableStateOf(ThemeType.SYSTEM)
        private set

    fun updateTheme(newTheme: ThemeType) {
        currentTheme = newTheme
    }

    @Composable
    fun shouldUseDarkTheme(): Boolean {
        return when (currentTheme) {
            ThemeType.LIGHT -> false
            ThemeType.DARK -> true
            ThemeType.SYSTEM -> isSystemInDarkTheme()
        }
    }
}