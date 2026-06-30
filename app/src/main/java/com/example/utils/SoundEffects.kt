package com.example.utils

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log

object SoundEffects {
    private var toneGenerator: ToneGenerator? = null
    var enabled: Boolean = true

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 85)
        } catch (e: Exception) {
            Log.e("SoundEffects", "Failed to initialize ToneGenerator", e)
        }
    }

    fun playBeep() {
        if (!enabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 80)
        } catch (e: Exception) {
            Log.e("SoundEffects", "Error playing beep", e)
        }
    }

    fun playConfirm() {
        if (!enabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 150)
        } catch (e: Exception) {
            Log.e("SoundEffects", "Error playing confirm", e)
        }
    }

    fun playChime() {
        if (!enabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 200)
        } catch (e: Exception) {
            Log.e("SoundEffects", "Error playing chime", e)
        }
    }

    fun playStartup() {
        if (!enabled) return
        try {
            // Play a little futuristic fan-fare
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 100)
            Thread {
                try {
                    Thread.sleep(150)
                    if (enabled) toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 100)
                    Thread.sleep(150)
                    if (enabled) toneGenerator?.startTone(ToneGenerator.TONE_CDMA_PIP, 200)
                } catch (ignored: Exception) {}
            }.start()
        } catch (e: Exception) {
            Log.e("SoundEffects", "Error playing startup sound", e)
        }
    }
}
