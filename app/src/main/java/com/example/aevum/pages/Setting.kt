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
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.util.*

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview

//@Composable
//fun Setting (modifier: Modifier = Modifier) {
//    var backgroundColor by remember { mutableStateOf(Color.White) }
//    var textColor by remember { mutableStateOf(Color.Black) }
//
//    Column (
//        modifier = Modifier
//            .fillMaxSize()
//            .background(backgroundColor),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ){
//        Text("rrrrr", color = textColor)
//
//        Button(onClick = {
//            if (backgroundColor == Color.White) {
//                backgroundColor = Color.Black
//                textColor = Color.White
//            } else {
//                backgroundColor = Color.White
//                textColor = Color.Black
//            }
//        }) {
//            Text("Change Background & Text Color")
//        }
//    }
//}

val LocalBackgroundColor = staticCompositionLocalOf { mutableStateOf(Color.White) }
val LocalTextColor = staticCompositionLocalOf { mutableStateOf(Color.Black) }
val LocalLanguage = staticCompositionLocalOf { mutableStateOf(Locale.getDefault().language) }



@Composable
fun Rem() {
//    val backgroundColor = remember { LocalBackgroundColor.current }
//    val textColor = remember { LocalTextColor.current }
//    or
    val backgroundColor = remember { mutableStateOf(Color.White) }
    val textColor = remember { mutableStateOf(Color.Black) }
    val language = remember { mutableStateOf(Locale.getDefault().language) }


    CompositionLocalProvider(LocalBackgroundColor provides backgroundColor, LocalTextColor provides textColor, LocalLanguage provides language) {

        Setting()
//        OtherPage()
    }
}


@Composable
@Preview
fun Setting(modifier: Modifier = Modifier) {
    val backgroundColor = LocalBackgroundColor.current
    val textColor = LocalTextColor.current
    val language = LocalLanguage.current
    var showDialog by remember { mutableStateOf(false) }

    val translatedText = remember(language.value) {
        when (language.value) {
            "en" -> mapOf(
                "Set" to "Set",
                "Color" to "Color",
                "Language" to "Language"
            )
            "de" -> mapOf(
                "Set" to "Einstellen",
                "Color" to "die Farbe",
                "Language" to "Sprache"
            )
            else -> mapOf(
                "Set" to "Set",
                "Change Background & Text Color" to "Change Background & Text Color",
                "Change Language" to "Change Language"
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor.value),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = translatedText["Set"] ?: "Set",
            color = textColor.value
        ) // И здесь
//        Text("Set", color = textColor.value)

        Button(onClick = {
            backgroundColor.value = if (backgroundColor.value == Color.White) {
                Color.Black
            } else {
                Color.White
            }
            textColor.value = if (textColor.value == Color.White) {
                Color.Black
            } else {
                Color.White
            }
        }) {
//            Text("Color", color = textColor.value)
            Text(
                text = translatedText["Color"] ?: "Color",
                color = textColor.value
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { showDialog = true }) {
            Text(
                text = translatedText["Language"] ?: "Language",
                color = textColor.value
            )

        }

        if (showDialog) {
            LanguageDialog(
                onDismiss = { showDialog = false },
                onLanguageSelected = { selectedLanguage ->
                    language.value = selectedLanguage
                    showDialog = false
                }
            )
        }
    }
}
//@Composable
//fun OtherPage(modifier: Modifier = Modifier) {
//    val backgroundColor = LocalBackgroundColor.current
//    val textColor = LocalTextColor.current
//    val language = LocalLanguage.current
//
//    val translatedText = remember(language.value) {
//        when (language.value) {
//            "en" -> mapOf("This is another page" to "This is another page")
//            "de" -> mapOf("This is another page" to "Dies ist eine andere Seite")
//            else -> mapOf("This is another page" to "This is another page")
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(backgroundColor.value),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
////        Text("This is another page", color = textColor.value)
//        Text(
//            text = translatedText["This is another page"] ?: "This is another page",
//            color = textColor.value
//        ) // И здесь
//    }
//}

@Composable
fun LanguageDialog(onDismiss: () -> Unit, onLanguageSelected: (String) -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = "Select Language", modifier = Modifier.padding(16.dp))
            Button(modifier = Modifier.padding(8.dp), onClick = { onLanguageSelected("en") }) {
                Text("English")
            }
            Button(modifier = Modifier.padding(8.dp), onClick = { onLanguageSelected("de") }) {
                Text("German")
            }
        }
    }
}


