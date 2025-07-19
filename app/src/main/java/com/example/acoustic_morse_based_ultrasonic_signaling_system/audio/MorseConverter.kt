package com.example.acoustic_morse_based_ultrasonic_signaling_system.audio

object MorseConverter {

    private val charToMorse = mapOf(
        'A' to ".-",     'B' to "-...",   'C' to "-.-.",
        'D' to "-..",    'E' to ".",      'F' to "..-.",
        'G' to "--.",    'H' to "....",   'I' to "..",
        'J' to ".---",   'K' to "-.-",    'L' to ".-..",
        'M' to "--",     'N' to "-.",     'O' to "---",
        'P' to ".--.",   'Q' to "--.-",   'R' to ".-.",
        'S' to "...",    'T' to "-",      'U' to "..-",
        'V' to "...-",   'W' to ".--",    'X' to "-..-",
        'Y' to "-.--",   'Z' to "--..",
        '0' to "-----",  '1' to ".----",  '2' to "..---",
        '3' to "...--",  '4' to "....-",  '5' to ".....",
        '6' to "-....",  '7' to "--...",  '8' to "---..",
        '9' to "----."
    )

    private val morseToChar = charToMorse.entries.associate { (char, morse) -> morse to char }

    fun toMorse(message: String): String {
        return message.uppercase().mapNotNull { char ->
            if (char == ' ') " / " else charToMorse[char]
        }.joinToString(" ")
    }

    fun decode(morseCode: String): String {
        return morseCode.trim().split(" ").map { code ->
            when (code) {
                "/" -> " "  // Handle space between words
                else -> morseToChar[code] ?: ""
            }
        }.joinToString("")
    }
}
