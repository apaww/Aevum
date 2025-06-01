package com.example.aevum.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.foundation.background
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.aevum.R
import com.example.aevum.data.SettingsViewModel
import com.example.aevum.ui.theme.ThemeType

@SuppressLint("ContextCastToActivity")
@Composable
fun SettingsPage(
    context: Context = LocalContext.current
) {
    val viewModel = remember { SettingsViewModel(context) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    val showSoundDialog = remember { mutableStateOf(false) }
    val currentTheme by remember { derivedStateOf { viewModel.currentTheme }}
    val activity = LocalContext.current as Activity

    val notificationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.hasNotificationPermission = isGranted
        if (isGranted) {
            Toast.makeText(context,
                context.getString(R.string.settings_notification_permission_granted), Toast.LENGTH_SHORT).show()
        }
    }

//    val phoneStateLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted ->
//        viewModel.hasPhoneStatePermission = isGranted
//        if (isGranted) {
//            Toast.makeText(context,
//                context.getString(R.string.settings_phone_tracking_permission_granted), Toast.LENGTH_SHORT).show()
//        }
//    }

    var showNotificationDialog by remember { mutableStateOf(false) }
//    var showPhoneStateDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.paddingFromBaseline(16.dp))

        Text(stringResource(R.string.settings_app_theme), style = MaterialTheme.typography.titleMedium)
        Column {
            ThemeType.entries.forEach { theme ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setTheme(theme) }
                ) {
                    RadioButton(
                        selected = currentTheme == theme,
                        onClick = { viewModel.setTheme(theme) }
                    )
                    Text(
                        text = when(theme) {
                            ThemeType.LIGHT -> stringResource(R.string.settings_light)
                            ThemeType.DARK -> stringResource(R.string.settings_dark)
                            ThemeType.SYSTEM -> stringResource(R.string.settings_system_default)
                        }
                    )
                }
            }
        }

        Text(stringResource(R.string.settings_language), style = MaterialTheme.typography.titleMedium)
        OutlinedButton(
            onClick = { showLanguageDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(SettingsViewModel.supportedLanguages[viewModel.currentLanguage] ?: "English")
        }

        Text(stringResource(R.string.settings_timer_sound), style = MaterialTheme.typography.titleMedium)
        OutlinedButton(
            onClick = { showSoundDialog.value = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                when(viewModel.currentTimerSound) {
                    R.raw.nothing -> stringResource(R.string.settings_nothingexe)
                    R.raw.cpoco -> stringResource(R.string.settings_cpoco)
                    else -> stringResource(R.string.settings_select_sound)
                }
            )
        }

        Text(stringResource(R.string.settings_permissions), style = MaterialTheme.typography.titleMedium)

        PermissionButton(
            text = "Notification Permission",
            isGranted = viewModel.hasNotificationPermission,
            onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (viewModel.hasNotificationPermission) {
                        Toast.makeText(context,
                            context.getString(R.string.settings_permission_already_granted), Toast.LENGTH_SHORT).show()
                    } else {
                        showNotificationDialog = true
                    }
                } else {
                    Toast.makeText(context,
                        context.getString(R.string.settings_not_needed_on_this_android_version),
                        Toast.LENGTH_SHORT).show()
                }
            }
        )

//        PermissionButton(
//            text = "Phone Unlock Tracking",
//            isGranted = viewModel.hasPhoneStatePermission,
//            onClick = {
//                if (viewModel.hasPhoneStatePermission) {
//                    Toast.makeText(context,
//                        context.getString(R.string.settings_permission_already_granted), Toast.LENGTH_SHORT).show()
//                } else {
//                    showPhoneStateDialog = true
//                }
//            }
//        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Devs: @apaww & @AnAkfiaSaltes")
            Text("Music: @Mikor123")
        }
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(stringResource(R.string.settings_select_language)) },
            text = {
                Column {
                    SettingsViewModel.supportedLanguages.forEach { (code, name) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setLanguage(code)
                                    showLanguageDialog = false
                                    // Restart activity to apply language change
                                    (context as? Activity)?.recreate()
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = viewModel.currentLanguage == code,
                                onClick = {
                                    viewModel.setLanguage(code)
                                    showLanguageDialog = false
                                    (context as? Activity)?.recreate()
                                }
                            )
                            Text(name, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(stringResource(R.string.settings_cancel))
                }
            }
        )
    }

    if (showSoundDialog.value) {
        AlertDialog(
            onDismissRequest = { showSoundDialog.value = false },
            title = { Text(stringResource(R.string.settings_select_timer_sound)) },
            text = {
                Column {
                    listOf(
                        R.raw.nothing to stringResource(R.string.settings_nothingexe),
                        R.raw.cpoco to stringResource(R.string.settings_cpoco)
                    ).forEach { (soundRes, soundName) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setTimerSound(soundRes)
                                    showSoundDialog.value = false
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = viewModel.currentTimerSound == soundRes,
                                onClick = {
                                    viewModel.setTimerSound(soundRes)
                                    showSoundDialog.value = false
                                }
                            )
                            Text(soundName, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSoundDialog.value = false }) {
                    Text(stringResource(R.string.settings_cancel))
                }
            }
        )
    }

    if (showNotificationDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationDialog = false },
            title = { Text(stringResource(R.string.settings_notification_permission)) },
            text = { Text(stringResource(R.string.settings_this_permission_allows_notifications)) },
            confirmButton = {
                Button(onClick = {
                    showNotificationDialog = false
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }) {
                    Text(stringResource(R.string.settings_continue))
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotificationDialog = false }) {
                    Text(stringResource(R.string.settings_cancel))
                }
            }
        )
    }

//    if (showPhoneStateDialog) {
//        AlertDialog(
//            onDismissRequest = { showPhoneStateDialog = false },
//            title = { Text(stringResource(R.string.settings_phone_unlock_tracking)) },
//            text = { Text(stringResource(R.string.settings_this_permission_helps_track_how_often)) },
//            confirmButton = {
//                Button(onClick = {
//                    showPhoneStateDialog = false
//                    phoneStateLauncher.launch(Manifest.permission.READ_PHONE_STATE)
//                }) {
//                    Text(stringResource(R.string.settings_continue))
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { showPhoneStateDialog = false }) {
//                    Text(stringResource(R.string.settings_cancel))
//                }
//            }
//        )
//    }
}

@Composable
fun PermissionButton(
    text: String,
    isGranted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isGranted) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text)
            Icon(
                imageVector = if (isGranted) Icons.Default.Check else Icons.Default.Info,
                contentDescription = if (isGranted) "Granted" else "Required",
                tint = if (isGranted) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
