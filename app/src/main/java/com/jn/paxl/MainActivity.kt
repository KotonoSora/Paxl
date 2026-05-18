package com.jn.paxl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jn.paxl.ui.GameNavigation
import com.jn.paxl.ui.LocalSoundManager
import com.jn.paxl.ui.SoundManager
import com.jn.paxl.ui.theme.PaxlTheme
import com.jn.paxl.viewmodel.GameViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val soundManager = remember { SoundManager(context) }
            val viewModel: GameViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()

            LaunchedEffect(uiState.soundEnabled) {
                soundManager.isEnabled = uiState.soundEnabled
            }

            DisposableEffect(Unit) {
                onDispose {
                    soundManager.release()
                }
            }

            PaxlTheme {
                CompositionLocalProvider(LocalSoundManager provides soundManager) {
                    GameNavigation(viewModel = viewModel)
                }
            }
        }
    }
}
