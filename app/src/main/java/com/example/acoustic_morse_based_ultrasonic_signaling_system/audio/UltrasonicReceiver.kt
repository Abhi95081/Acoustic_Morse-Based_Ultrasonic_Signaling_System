package com.example.acoustic_morse_based_ultrasonic_signaling_system.audio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import org.jtransforms.fft.DoubleFFT_1D
import kotlin.math.abs

object UltrasonicReceiver {

    private const val SAMPLE_RATE = 44100
    private const val BUFFER_SIZE = 2048
    private const val FREQ_DOT = 19000.0
    private const val FREQ_DASH = 20000.0
    private const val THRESHOLD = 100000.0

    private var isReceiving = false

    fun hasAudioPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun startReceiving(context: Context, onMessageDecoded: (String) -> Unit) {
        if (!hasAudioPermission(context)) {
            onMessageDecoded("Mic Permission Denied")
            return
        }

        isReceiving = true

        CoroutineScope(Dispatchers.IO).launch {
            val audioRecord = try {
                AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    BUFFER_SIZE
                )
            } catch (e: SecurityException) {
                onMessageDecoded("Mic Access Blocked")
                return@launch
            }

            val audioBuffer = ShortArray(BUFFER_SIZE)
            val fftBuffer = DoubleArray(BUFFER_SIZE * 2)
            val fft = DoubleFFT_1D(BUFFER_SIZE.toLong())

            val detectedMorse = StringBuilder()

            try {
                audioRecord.startRecording()
            } catch (e: SecurityException) {
                onMessageDecoded("Mic Start Failed")
                audioRecord.release()
                return@launch
            }

            while (isReceiving) {
                val readCount = audioRecord.read(audioBuffer, 0, BUFFER_SIZE)

                if (readCount > 0) {
                    for (i in 0 until BUFFER_SIZE) {
                        fftBuffer[i * 2] = audioBuffer[i].toDouble()
                        fftBuffer[i * 2 + 1] = 0.0
                    }

                    fft.complexForward(fftBuffer)

                    val freqs = DoubleArray(BUFFER_SIZE / 2)
                    for (i in freqs.indices) {
                        val re = fftBuffer[2 * i]
                        val im = fftBuffer[2 * i + 1]
                        freqs[i] = re * re + im * im
                    }

                    val peakIndex = freqs.indices.maxByOrNull { freqs[it] } ?: -1
                    val peakFreq = peakIndex * SAMPLE_RATE / BUFFER_SIZE

                    if (freqs[peakIndex] > THRESHOLD) {
                        when {
                            abs(peakFreq - FREQ_DOT) < 300 -> detectedMorse.append(".")
                            abs(peakFreq - FREQ_DASH) < 300 -> detectedMorse.append("-")
                        }
                    } else {
                        if (detectedMorse.isNotEmpty()) {
                            val decodedChar = MorseDecoder.decode(detectedMorse.toString())
                            onMessageDecoded(decodedChar)
                            detectedMorse.clear()
                        }
                    }
                }
            }

            audioRecord.stop()
            audioRecord.release()
        }
    }

    fun stopReceiving() {
        isReceiving = false
    }
}
