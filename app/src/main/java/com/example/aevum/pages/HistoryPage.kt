package com.example.aevum.pages

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aevum.data.DaySummary
import com.example.aevum.data.DayViewModel
import java.text.SimpleDateFormat

@Preview
@Composable
fun HistoryPage(
    modifier: Modifier = Modifier,
) {
    val viewModel: DayViewModel = viewModel()
    val days by viewModel.days.collectAsState()
    var selectedDay by remember { mutableStateOf<DaySummary?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadDays()
    }

    if (selectedDay != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(modifier = Modifier.padding(16.dp, 30.dp, 0.dp, 0.dp)) {
                IconButton(
                    onClick = { selectedDay = null },
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
                DayPage(selectedDay!!)
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(0.dp, 30.dp, 0.dp, 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "History of daily activity",
                fontSize = 31.sp,
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.ExtraBold
                ),
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(0.60f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 4.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp))
            ) {
            DaysList(
                days = days,
                onDayClick = { day -> selectedDay = day } // Receive DaySummary directly
            )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DaysList(
    days: List<DaySummary>,
    onDayClick: (DaySummary) -> Unit, // Change to accept DaySummary
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        itemsIndexed(
            items = days,
            key = { _, day -> day.day }
        ) { _, day ->
            DaySummaryItem(
                day = day,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDayClick(day) } // Pass the day object
                    .animateItemPlacement()
            )
        }
    }
}

@Composable
fun DaySummaryItem(
    day: DaySummary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = day.dayName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = SimpleDateFormat("MMM dd, yyyy").format(
                        SimpleDateFormat("yyyyMMdd").parse(day.day.toString())
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${day.tasksCompleted}/${day.totalTasks} tasks",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${day.phoneUnlocks} unlocks",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}