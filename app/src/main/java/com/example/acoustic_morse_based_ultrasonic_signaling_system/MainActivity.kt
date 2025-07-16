package com.example.acoustic_morse_based_ultrasonic_signaling_system

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.acoustic_morse_based_ultrasonic_signaling_system.ui.theme.Acoustic_MorseBased_Ultrasonic_Signaling_SystemTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Acoustic_MorseBased_Ultrasonic_Signaling_SystemTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                   
                }
            }
        }
    }
}

