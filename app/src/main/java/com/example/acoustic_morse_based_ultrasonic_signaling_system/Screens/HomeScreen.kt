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
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "🔊 Ultrasonic SOS Communication",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        if (!micPermissionState.status.isGranted) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Microphone permission is required for receiving messages.",
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { micPermissionState.launchPermissionRequest() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Grant Microphone Permission")
                    }
                }
            }
            return@Column
        }

        /** TRANSMIT SECTION **/
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("📤 Transmit Section", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                OutlinedTextField(
                    value = viewModel.message,
                    onValueChange = { viewModel.updateMessage(it) },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    label = { Text("Message to Transmit") }
                )

                Text(
                    text = "Morse Code: ${viewModel.morseCode}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Button(
                    onClick = {
                        viewModel.startTransmission()
                        UltrasonicTransmitter.transmitMorse(viewModel.morseCode)
                        viewModel.stopTransmission()
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(if (viewModel.isTransmitting) "Transmitting..." else "Transmit Message")
                }
            }
        }

        /** RECEIVE SECTION **/
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("📥 Receive Section", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                Button(
                    onClick = {
                        if (!viewModel.isReceiving) {
                            viewModel.startListening(context)
                        } else {
                            viewModel.stopListening()
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Text(if (viewModel.isReceiving) "Stop Receiving" else "Start Receiving")
                }

                if (viewModel.receivedMessage.isNotEmpty()) {
                    Column(Modifier.padding(top = 8.dp)) {
                        Text("Received Message:", fontWeight = FontWeight.SemiBold)
                        Text(viewModel.receivedMessage)
                    }
                }

                if (viewModel.isReceiving) {
                    Text(
                        "Listening for incoming ultrasonic signals...",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        /** WRITTEN MORSE MANUAL **/
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("📖 Morse Manual", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                Button(
                    onClick = { showMorseManual = !showMorseManual },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(if (showMorseManual) "Hide Morse Manual" else "Show Morse Manual")
                }

                if (showMorseManual) {
                    Text(
                        text = morseManual,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }

        /** MANUAL DECODER **/
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("✍️ Manual Morse Decoder", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                OutlinedTextField(
                    value = manualMorseInput,
                    onValueChange = { manualMorseInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    label = { Text("Enter Morse Code (e.g., ... --- ...)") }
                )

                Button(
                    onClick = {
                        decodedManualMessage = MorseConverter.decode(manualMorseInput.trim())
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Decode Morse")
                }

                if (decodedManualMessage.isNotEmpty()) {
                    Text(
                        "Decoded Message: $decodedManualMessage",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
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
