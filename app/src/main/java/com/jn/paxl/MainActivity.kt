package com.jn.paxl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import local.paxl.ui.di.AppContainerHost
import local.paxl.ui.di.AppViewModelFactory
import local.paxl.ui.di.LocalAppContainer
import local.paxl.ui.infrastructure.audio.LocalSoundManager
import local.paxl.ui.infrastructure.audio.SoundManager
import local.paxl.ui.presentation.MainApp
import local.paxl.ui.presentation.theme.GameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as AppContainerHost).container

        setContent {
            val soundManager = remember { SoundManager(applicationContext) }

            DisposableEffect(Unit) {
                onDispose { soundManager.release() }
            }

            CompositionLocalProvider(
                LocalSoundManager provides soundManager,
                LocalAppContainer provides container
            ) {
                GameTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val factory = remember(container) { AppViewModelFactory(container) }
                        MainApp(factory = factory)
                    }
                }
            }
        }
    }
}
