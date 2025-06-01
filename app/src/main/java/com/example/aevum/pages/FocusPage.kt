package com.example.aevum.pages

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import com.example.aevum.R
import com.example.aevum.data.SettingsViewModel
import kotlinx.coroutines.delay
import kotlin.reflect.typeOf


class TimerState {
    var hours by mutableStateOf("0")
    var minutes by mutableStateOf("0")
    var isRunning by mutableStateOf(false)
    var remainingTimeInSeconds by mutableLongStateOf(0L)
}


@Composable
fun FocusPage(modifier: Modifier = Modifier, context: Context = LocalContext.current) {
    val settingsViewModel = remember { SettingsViewModel(context) }
    val filled = MaterialTheme.colorScheme.primary
    val voided = MaterialTheme.colorScheme.primaryContainer
    var sizePx by remember { mutableIntStateOf(900) }
    val sizeDp = with(LocalDensity.current) { sizePx.toDp() }
    val timerState = remember { TimerState() }

    val totalSeconds = remember(timerState.hours, timerState.minutes) {
        (timerState.hours.toIntOrNull()?.times(3600) ?: 0) +
                (timerState.minutes.toIntOrNull()?.times(60) ?: 0)
    }

    val progress by animateFloatAsState(
        targetValue = if (totalSeconds > 0) {
            (totalSeconds.toFloat() - timerState.remainingTimeInSeconds.toFloat()) / totalSeconds.toFloat()
        } else {
            1f
        },
        animationSpec = tween(
            durationMillis = 1000,
            easing = LinearEasing
        ),
        label = "timerProgressAnimation"
    )

    val sweepAngle by animateFloatAsState(
        targetValue = 260f * progress,
        animationSpec = tween(
            durationMillis = 1000,
            easing = LinearEasing
        ),
        label = "sweepAngleAnimation"
    )
    Box (
        modifier = Modifier
            .fillMaxSize(),
            contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(sizeDp)) {
            drawArc(
                color = voided,
                startAngle = 140f,
                sweepAngle = 260f,
                useCenter = false,
                style = Stroke(width = 25.dp.toPx(), cap = StrokeCap.Round),
                size = Size(size.width, size.height)
            )
            drawArc(
                color = filled,
                startAngle = 140f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round),
                size = Size(size.width, size.height)
            )
        }

        Timer(timerState, settingsViewModel.currentTimerSound)
    }
}


@Composable
fun Timer(timerState: TimerState, timerSoundResId: Int) {
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    val context = LocalContext.current
    val notificationManager = remember {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    LaunchedEffect(Unit) {
        createNotificationChannel(context)
    }

    LaunchedEffect(timerState.isRunning, timerState.remainingTimeInSeconds) {
        if (timerState.isRunning && timerState.remainingTimeInSeconds > 0) {
            delay(1000)
            timerState.remainingTimeInSeconds--

            if (timerState.remainingTimeInSeconds == 0L) {
                timerState.isRunning = false
                showNotification(context,
                    context.getString(R.string.focus_notification_title),
                    context.getString(R.string.focus_notification_string))
                // Stop music when timer ends
                mediaPlayer?.release()
                mediaPlayer = null
            }
        }
    }

    LaunchedEffect(timerState.isRunning) {
        if (timerState.isRunning && timerState.remainingTimeInSeconds > 0) {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, timerSoundResId).apply {
                isLooping = true
                start()
            }
        } else {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = formatTime(timerState.remainingTimeInSeconds),
            style = MaterialTheme.typography.displayMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = timerState.hours,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.toIntOrNull() != null) {
                        timerState.hours = newValue
                    }
                },
                label = { Text(stringResource(R.string.focus_hours)) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.width(100.dp)
            )

            Text(text = ":", style = MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = timerState.minutes,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || (newValue.toIntOrNull() != null && newValue.toInt() < 60)) {
                        timerState.minutes = newValue
                    }
                },
                label = { Text(stringResource(R.string.focus_minutes)) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.width(100.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Button(
                onClick = {
                    val totalSeconds = (timerState.hours.toIntOrNull() ?: 0) * 3600 + (timerState.minutes.toIntOrNull() ?: 0) * 60
                    if (totalSeconds > 0) {
                        timerState.remainingTimeInSeconds = totalSeconds.toLong()
                        timerState.isRunning = true
                    }
                },
                enabled = !timerState.isRunning && (timerState.hours.toIntOrNull() ?: 0) * 3600 + (timerState.minutes.toIntOrNull() ?: 0) * 60 > 0
            ) {
                Text(stringResource(R.string.focus_start))
            }

            Button(
                onClick = {
                    timerState.hours = "0"
                    timerState.minutes = "0"
                    timerState.remainingTimeInSeconds = 0
                    timerState.isRunning = false
                }
            ) {
                Text(stringResource(R.string.focus_reset))
            }
        }
    }
}

private fun formatTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, secs)
}

private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "timer_channel",
            "Timer Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Channel for timer notifications"
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

private fun showNotification(context: Context, title: String, message: String) {
    val builder = NotificationCompat.Builder(context, "timer_channel")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(1, builder.build())
}