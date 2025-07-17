package com.example.acoustic_morse_based_ultrasonic_signaling_system.audio

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlin.math.sin

object UltrasonicTransmitter {
    private const val SAMPLE_RATE = 44100
    private const val FREQ_DOT = 19000.0
    private const val FREQ_DASH = 20000.0
    private const val DURATION_DOT = 100
    private const val DURATION_DASH = 300

    fun transmitMorse(morseCode: String) {
        morseCode.forEach {
            when (it) {
                '.' -> playTone(FREQ_DOT, DURATION_DOT)
                '-' -> playTone(FREQ_DASH, DURATION_DASH)
                ' ' -> Thread.sleep(100)
            }
        }
    }

    private fun playTone(freq: Double, durationMs: Int) {
        val count = (SAMPLE_RATE * durationMs / 1000.0).toInt()
        val audioData = ShortArray(count) { i ->
            (sin(2 * Math.PI * i / (SAMPLE_RATE / freq)) * Short.MAX_VALUE).toInt().toShort()
        }

        val audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            audioData.size * 2,
            AudioTrack.MODE_STATIC
        )
        audioTrack.write(audioData, 0, audioData.size)
        audioTrack.play()
        Thread.sleep(durationMs.toLong())
        audioTrack.release()
    }
}