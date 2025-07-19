package com.example.acoustic_morse_based_ultrasonic_signaling_system.Screens

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acoustic_morse_based_ultrasonic_signaling_system.audio.MorseConverter
import com.example.acoustic_morse_based_ultrasonic_signaling_system.audio.UltrasonicTransmitter
import com.example.acoustic_morse_based_ultrasonic_signaling_system.viewmodel.SOSViewModel
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(viewModel: SOSViewModel) {

    val context = LocalContext.current
    val micPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    var manualMorseInput by remember { mutableStateOf("") }
    var decodedManualMessage by remember { mutableStateOf("") }
    var showMorseManual by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Text(
            text = "🔊 Ultrasonic SOS Communication",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        if (!micPermissionState.status.isGranted) {
            Text(
                text = "Microphone permission is required for receiving messages.",
                color = MaterialTheme.colorScheme.error
            )
            Button(
                onClick = { micPermissionState.launchPermissionRequest() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Grant Microphone Permission")
            }

            return@Column
        }

        /** TRANSMIT SECTION **/
        Text("📤 Transmit Section", fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = viewModel.message,
            onValueChange = { viewModel.updateMessage(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Enter Message to Transmit") }
        )

        Text(
            text = "Morse Code: ${viewModel.morseCode}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
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
            Text(if (viewModel.isTransmitting) "Transmitting..." else "Transmit Message")
        }

        Divider()

        /** RECEIVE SECTION **/
        Text("📥 Receive Section", fontWeight = FontWeight.Bold)

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
            Text(if (viewModel.isReceiving) "Stop Receiving" else "Start Receiving")
        }

        if (viewModel.receivedMessage.isNotEmpty()) {
            Column {
                Text("Received Message:", fontWeight = FontWeight.SemiBold)
                Text(viewModel.receivedMessage)
            }
        }

        if (viewModel.isReceiving) {
            Text("Listening for incoming ultrasonic signals...")
        }

        Divider()

        Text("\uD83D\uDCDD Written Morse Section", fontWeight = FontWeight.Bold)

        /** MORSE MANUAL **/
        Button(
            onClick = { showMorseManual = !showMorseManual },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (showMorseManual) "Hide Morse Manual" else "Show Morse Manual")
        }

        if (showMorseManual) {
            Text(
                text = morseManual,
                fontSize = 12.sp,
                modifier = Modifier.padding(10.dp)
            )
        }

        Divider()

        /** MANUAL DECODER **/
        Text("✍️ Manual Morse Decoder", fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = manualMorseInput,
            onValueChange = { manualMorseInput = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Enter Morse Code (e.g., ... --- ...)") }
        )

        Button(
            onClick = {
                decodedManualMessage = MorseConverter.decode(manualMorseInput.trim())
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Decode Morse")
        }

        if (decodedManualMessage.isNotEmpty()) {
            Text("Decoded Message: $decodedManualMessage")
        }
    }
}

private val morseManual = """
A: .-         N: -.
B: -...       O: ---
C: -.-.       P: .--.
D: -..        Q: --.-
E: .          R: .-.
F: ..-.       S: ...
G: --.        T: -
H: ....       U: ..-
I: ..         V: ...-
J: .---       W: .--
K: -.-        X: -..-
L: .-..       Y: -.--
M: --         Z: --..

0: -----      5: .....
1: .----      6: -....
2: ..---      7: --...
3: ...--      8: ---..
4: ....-      9: ----.
""".trimIndent()
