package com.example.acoustic_morse_based_ultrasonic_signaling_system.Screens

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acoustic_morse_based_ultrasonic_signaling_system.audio.UltrasonicTransmitter
import com.example.acoustic_morse_based_ultrasonic_signaling_system.viewmodel.SOSViewModel
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(viewModel: SOSViewModel) {

    val context = LocalContext.current
    val micPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Ultrasonic SOS Communication",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        if (!micPermissionState.status.isGranted) {
            Text(
                text = "Microphone permission is required for receiving messages.",
                color = MaterialTheme.colorScheme.error
            )

            Button(
                onClick = { micPermissionState.launchPermissionRequest() },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Grant Microphone Permission")
            }

        } else {

            OutlinedTextField(
                value = viewModel.message,
                onValueChange = { viewModel.updateMessage(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Enter Message to Transmit") }
            )

            Button(
                onClick = {
                    viewModel.startTransmission()
                    UltrasonicTransmitter.transmitMorse(viewModel.morseCode)
                    viewModel.stopTransmission()
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (viewModel.isTransmitting) "Transmitting..." else "Transmit Message"
                )
            }

            Divider()

            Button(
                onClick = {
                    if (!viewModel.isReceiving) {
                        viewModel.startListening(context)
                    } else {
                        viewModel.stopListening()
                    }
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (viewModel.isReceiving) "Stop Receiving" else "Start Receiving"
                )
            }

            if (viewModel.receivedMessage.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Received Message:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = viewModel.receivedMessage,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            if (viewModel.isReceiving) {
                Text(
                    text = "Listening for incoming ultrasonic signals...",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
