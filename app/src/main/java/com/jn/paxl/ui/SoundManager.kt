package com.jn.paxl.ui

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.runtime.staticCompositionLocalOf
import com.jn.paxl.R

class SoundManager(context: Context) {
    private val soundPool: SoundPool
    private val placeSound: Int
    private val clearSound: Int
    private val clickSound: Int
    private val winSound: Int
    private val loseSound: Int

    var isEnabled: Boolean = true

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        placeSound = loadSound(context, R.raw.place)
        clearSound = loadSound(context, R.raw.clear)
        clickSound = loadSound(context, R.raw.click)
        winSound = loadSound(context, R.raw.win)
        loseSound = loadSound(context, R.raw.lose)
    }

    private fun loadSound(context: Context, resId: Int): Int {
        return try {
            soundPool.load(context, resId, 1)
        } catch (e: Exception) {
            -1
        }
    }

    fun playPlace() {
        playSound(placeSound)
    }

    fun playClear() {
        playSound(clearSound)
    }

    fun playClick() {
        playSound(clickSound)
    }

    fun playWin() {
        playSound(winSound)
    }

    fun playLose() {
        playSound(loseSound)
    }

    private fun playSound(soundId: Int) {
        if (isEnabled && soundId != -1) {
            soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
        }
    }

    fun release() {
        soundPool.release()
    }
}

val LocalSoundManager = staticCompositionLocalOf<SoundManager?> { null }
