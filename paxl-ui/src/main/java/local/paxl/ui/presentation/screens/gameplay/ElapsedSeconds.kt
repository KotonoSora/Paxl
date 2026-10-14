package local.paxl.ui.presentation.screens.gameplay

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import kotlinx.coroutines.delay

/**
 * Elapsed run timer (presentation concern). Extracted from the screen body so
 * the Composable only consumes the value.
 */
@Composable
fun rememberElapsedSeconds(sessionStartMs: Long): Long {
    val elapsedSeconds by produceState(initialValue = 0L, key1 = sessionStartMs) {
        while (true) {
            value =
                ((System.currentTimeMillis() - sessionStartMs) / 1000L).coerceAtLeast(0L)
            delay(1000)
        }
    }
    return elapsedSeconds
}
