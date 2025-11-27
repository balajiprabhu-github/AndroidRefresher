package com.balajiprabhu.broadcastreceiver.custom

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

class BroadcastSenderActivity : ComponentActivity() {

    private var broadcastCount by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    BroadcastSenderScreen(
                        broadcastCount = broadcastCount,
                        onSendSimpleMessage = {
                            sendCustomBroadcast("Hello from Sender! 👋")
                        },
                        onSendUrgentMessage = {
                            sendCustomBroadcast("⚠️ URGENT: Important broadcast!")
                        },
                        onNavigateToReceiver = {
                            startActivity(Intent(this, BroadcastReceiverActivity::class.java))
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }




    private fun sendCustomBroadcast(message: String) {
        val intent = Intent(BroadcastConstants.ACTION_CUSTOM_MESSAGE).apply {
            putExtra(BroadcastConstants.EXTRA_MESSAGE, message)
            putExtra(BroadcastConstants.EXTRA_TIMESTAMP, System.currentTimeMillis())
            // IMPORTANT: Make the broadcast explicit for Android 8+
            // This ensures it reaches receivers in the same app
            setPackage(packageName)
        }
        
        Log.d("BroadcastSender", "Sending broadcast:")
        Log.d("BroadcastSender", "  Action: ${intent.action}")
        Log.d("BroadcastSender", "  Package: ${intent.`package`}")
        Log.d("BroadcastSender", "  Message: $message")
        
        sendBroadcast(intent)
        broadcastCount++
        Log.d("BroadcastSender", "Broadcast sent! (count: $broadcastCount)")
    }


}

@Composable
fun BroadcastSenderScreen(
    broadcastCount: Int,
    onSendSimpleMessage: () -> Unit,
    onSendUrgentMessage: () -> Unit,
    onNavigateToReceiver: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Send,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Custom Broadcast Sender",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Send custom broadcasts to other components",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Broadcast counter
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$broadcastCount",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "Broadcasts Sent",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Send buttons
        Button(
            onClick = onSendSimpleMessage,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send Simple Message")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onSendUrgentMessage,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Send Urgent Message")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // NEW: Delayed button for testing
        Button(
            onClick = { 
                // Create a handler to send after 5 seconds
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    onSendSimpleMessage()
                }, 5000) 
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Text("⏳ Send in 5 Seconds (Test Mode)")
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(
            onClick = onNavigateToReceiver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Open Receiver Activity →")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "💡 Tip: Open the Receiver activity to see broadcasts in real-time!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )
    }
}