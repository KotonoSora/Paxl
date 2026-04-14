package com.kotonosora.paxl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kotonosora.paxl.ui.PaxlNavigation
import com.kotonosora.paxl.ui.theme.PaxlTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PaxlTheme {
                PaxlNavigation()
            }
        }
    }
}
