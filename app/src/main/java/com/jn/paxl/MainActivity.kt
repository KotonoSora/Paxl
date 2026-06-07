package com.jn.paxl

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jn.paxl.ui.GameNavigation
import com.jn.paxl.ui.LocalSoundManager
import com.jn.paxl.ui.SoundManager
import com.jn.paxl.ui.theme.GameTheme
import com.jn.paxl.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    private fun enterFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            window.attributes = window.attributes.apply {
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }

        WindowCompat.getInsetsController(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterFullscreen()
        setContent {
            val context = LocalContext.current
            val soundManager = remember { SoundManager(context) }
            val viewModel: GameViewModel = viewModel(factory = GameViewModel.Factory)
            val uiState by viewModel.uiState.collectAsState()

            LaunchedEffect(uiState.soundEnabled) {
                soundManager.isEnabled = uiState.soundEnabled
            }

            DisposableEffect(Unit) {
                onDispose {
                    soundManager.release()
                }
            }

            GameTheme {
                CompositionLocalProvider(LocalSoundManager provides soundManager) {
                    GameNavigation(viewModel = viewModel)
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            enterFullscreen()
        }
    }
}
