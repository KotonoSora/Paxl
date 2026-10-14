package local.paxl.ui.infrastructure.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.runtime.staticCompositionLocalOf
import local.paxl.ui.R

/**
 * SFX-only sound manager (no BGM). Sanctioned bypass of core `AudioManager`:
 * - Lives in game-ui and owns no preferences of its own.
 * - `soundEnabled` is driven by the host from core `AudioSettings.sfxEnabledFlow`;
 *   toggles route through core `AudioSettings` (see SettingsViewModel).
 * - The core `musicEnabledFlow` toggle is still honored (surfaced in Settings)
 *   even though there is no background music to pause.
 */
class SoundManager(context: Context? = null) {
    private val soundPool: SoundPool?
    private val sounds: Map<String, Int>

    /** Driven from core AudioSettings.sfxEnabledFlow by the host. */
    var soundEnabled: Boolean = true

    init {
        if (context != null) {
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(attributes)
                .build()

            sounds = mapOf(
                "place" to loadSound(context, R.raw.place),
                "clear" to loadSound(context, R.raw.clear),
                "vanish" to loadSound(context, R.raw.vanish),
                "click" to loadSound(context, R.raw.click),
                "win" to loadSound(context, R.raw.win),
                "lose" to loadSound(context, R.raw.lose)
            )
        } else {
            soundPool = null
            sounds = emptyMap()
        }
    }

    private fun loadSound(context: Context, resId: Int): Int {
        return try {
            soundPool?.load(context, resId, 1) ?: -1
        } catch (_: Exception) {
            -1
        }
    }

    fun playPlace() = play("place")
    fun playClear() = play("clear")
    fun playVanish() = play("vanish")
    fun playClick() = play("click")
    fun playWin() = play("win")
    fun playLose() = play("lose")

    private fun play(name: String) {
        if (!soundEnabled) return
        sounds[name]?.let { id ->
            if (id != -1) soundPool?.play(id, 1f, 1f, 0, 0, 1f)
        }
    }

    fun release() {
        soundPool?.release()
    }
}

val LocalSoundManager = staticCompositionLocalOf<SoundManager> {
    // Provide a no-op SoundManager by default to avoid crashes in Previews
    SoundManager(null)
}
