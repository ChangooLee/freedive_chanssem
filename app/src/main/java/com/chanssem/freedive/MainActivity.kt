package com.chanssem.freedive

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.chanssem.freedive.ui.FreediveApp
import com.chanssem.freedive.ui.SplashScreen
import com.chanssem.freedive.ui.theme.FreediveChanssemTheme
import com.chanssem.freedive.tts.TtsManager
import com.chanssem.freedive.utils.LanguageManager

class MainActivity : ComponentActivity() {

    private lateinit var ttsManager: TtsManager

    override fun attachBaseContext(newBase: Context) {
        val language = LanguageManager.getSavedLanguage(newBase)
        val context = LanguageManager.setAppLanguage(newBase, language)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ttsManager = TtsManager(this)

        setContent {
            FreediveChanssemTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showSplash by remember { mutableStateOf(true) }

                    if (showSplash) {
                        SplashScreen(
                            onSplashEnd = { showSplash = false }
                        )
                    } else {
                        FreediveApp(
                            speak = { text -> ttsManager.speak(text) }
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        ttsManager.shutdown()
        super.onDestroy()
    }
}