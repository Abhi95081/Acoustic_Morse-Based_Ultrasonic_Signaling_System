package com.example.acoustic_morse_based_ultrasonic_signaling_system

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.acoustic_morse_based_ultrasonic_signaling_system.Screens.HomeScreen
import com.example.acoustic_morse_based_ultrasonic_signaling_system.ui.theme.Acoustic_MorseBased_Ultrasonic_Signaling_SystemTheme
import com.example.acoustic_morse_based_ultrasonic_signaling_system.viewmodel.SOSViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Acoustic_MorseBased_Ultrasonic_Signaling_SystemTheme {
                val sosViewModel: SOSViewModel = viewModel()  // ✅ Create ViewModel instance
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Row(modifier = Modifier.padding(innerPadding)) {
                        HomeScreen(sosViewModel)  // ✅ Pass ViewModel instance
                    }

                }
            }
        }
    }
}

