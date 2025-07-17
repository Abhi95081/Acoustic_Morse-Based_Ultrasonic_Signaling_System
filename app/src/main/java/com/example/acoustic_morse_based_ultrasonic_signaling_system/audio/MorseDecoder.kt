package com.example.acoustic_morse_based_ultrasonic_signaling_system.audio

object MorseDecoder {
    private val morseMap = mapOf(
        ".-" to 'A', "-..." to 'B', "-.-." to 'C', "-.." to 'D',
        "." to 'E', "..-." to 'F', "--." to 'G', "...." to 'H',
        ".." to 'I', ".---" to 'J', "-.-" to 'K', ".-.." to 'L',
        "--" to 'M', "-." to 'N', "---" to 'O', ".--." to 'P',
        "--.-" to 'Q', ".-." to 'R', "..." to 'S', "-" to 'T',
        "..-" to 'U', "...-" to 'V', ".--" to 'W', "-..-" to 'X',
        "-.--" to 'Y', "--.." to 'Z'
    )

    fun decode(morseCode: String): String {
        return morseCode.trim().split(" ").map { morseMap[it] ?: '?' }.joinToString("")
    }
}
