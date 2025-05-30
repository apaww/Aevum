package com.example.aevum

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.aevum.data.NavItem
import com.example.aevum.pages.FocusPage
import com.example.aevum.pages.HistoryPage
import com.example.aevum.pages.SettingsPage
import com.example.aevum.pages.TodayPage


@SuppressLint("SourceLockedOrientationActivity")
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    val NavItemList = listOf<NavItem>(
        NavItem(stringResource(R.string.bar_today), Icons.Default.WbSunny),
        NavItem(stringResource(R.string.bar_focus), Icons.Default.Timer),
        NavItem(stringResource(R.string.bar_history), Icons.Default.CollectionsBookmark),
        NavItem(stringResource(R.string.bar_settings), Icons.Default.Settings)
    )

    var selectedIndex by remember {
        mutableIntStateOf(0)
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                        },
                        icon = {
                            Icon(imageVector = navItem.icon, contentDescription = "Icon")
                        },
                        label = {
                            Text(text = navItem.label)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        ContentScreen(modifier = Modifier.padding(innerPadding), selectedIndex)
    }
}

@Composable
fun ContentScreen(modifier: Modifier = Modifier, selectedIndex: Int) {
    when(selectedIndex) {
        0 -> TodayPage()
        1 -> FocusPage()
        2 -> HistoryPage()
        3 -> SettingsPage()
    }
}