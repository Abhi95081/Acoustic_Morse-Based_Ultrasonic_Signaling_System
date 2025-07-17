package com.example.acoustic_morse_based_ultrasonic_signaling_system.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.acoustic_morse_based_ultrasonic_signaling_system.audio.MorseConverter
import com.example.acoustic_morse_based_ultrasonic_signaling_system.audio.UltrasonicReceiver

class SOSViewModel : ViewModel() {

    // Transmit-side State
    var message by mutableStateOf("")
        private set

    var morseCode by mutableStateOf("")
        private set

    var isTransmitting by mutableStateOf(false)
        private set

    // Receive-side State
    var receivedMessage by mutableStateOf("")
        private set

    var isReceiving by mutableStateOf(false)
        private set

    fun updateMessage(newMessage: String) {
        message = newMessage
        morseCode = MorseConverter.toMorse(newMessage)
    }

    fun startTransmission() {
        isTransmitting = true
        // Actual transmission happens externally in UI layer using morseCode
    }

    fun stopTransmission() {
        isTransmitting = false
    }

    fun startListening(context: Context) {
        if (isReceiving) return  // Avoid multiple receivers

        if (!UltrasonicReceiver.hasAudioPermission(context)) {
            receivedMessage = "Mic Permission Denied"
            return
        }

        isReceiving = true
        receivedMessage = ""  // Reset previous message

        UltrasonicReceiver.startReceiving(context) { decodedChar ->
            receivedMessage += decodedChar  // Live append decoded message
        }
    }

    fun stopListening() {
        isReceiving = false
        UltrasonicReceiver.stopReceiving()
    }
}
