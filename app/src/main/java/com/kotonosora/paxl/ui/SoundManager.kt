package com.kotonosora.paxl.ui

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.kotonosora.paxl.R

class SoundManager(context: Context) {
    private val soundPool: SoundPool
    private val placeSound: Int
    private val clearSound: Int
    private val clickSound: Int

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        // These would require actual raw resources (e.g., res/raw/place.mp3)
        // For now, we'll use placeholders if the resources don't exist
        placeSound = try {
            soundPool.load(context, R.raw.place, 1)
        } catch (e: Exception) {
            -1
        }
        clearSound = try {
            soundPool.load(context, R.raw.clear, 1)
        } catch (e: Exception) {
            -1
        }
        clickSound = try {
            soundPool.load(context, R.raw.click, 1)
        } catch (e: Exception) {
            -1
        }
    }

    fun playPlace() {
        if (placeSound != -1) soundPool.play(placeSound, 1f, 1f, 0, 0, 1f)
    }

    fun playClear() {
        if (clearSound != -1) soundPool.play(clearSound, 1f, 1f, 0, 0, 1f)
    }

    fun playClick() {
        if (clickSound != -1) soundPool.play(clickSound, 1f, 1f, 0, 0, 1f)
    }

    fun release() {
        soundPool.release()
    }
}
