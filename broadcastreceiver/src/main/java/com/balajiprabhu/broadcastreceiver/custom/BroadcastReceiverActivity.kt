package com.balajiprabhu.broadcastreceiver.custom

import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

data class ReceivedMessage(
    val message: String,
    val timestamp: Long,
    val id: String = UUID.randomUUID().toString()
)

class BroadcastReceiverActivity : ComponentActivity() {

    private var receivedMessages = mutableStateListOf<ReceivedMessage>()

    private val customMessageReceiver = CustomMessageReceiver { message: String, timestamp: Long ->
        // Add to the list (newest first)
        receivedMessages.add(0, ReceivedMessage(message, timestamp))
        Log.d("BroadcastReceiver", "Received message: $message (total: ${receivedMessages.size})")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    BroadcastReceiverScreen(
                        receivedMessages = receivedMessages,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter(BroadcastConstants.ACTION_CUSTOM_MESSAGE)
        
        // Android 13+ requires explicit export flag
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(customMessageReceiver, intentFilter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(customMessageReceiver, intentFilter)
        }
        Log.d("BroadcastReceiver", "Receiver registered for ${BroadcastConstants.ACTION_CUSTOM_MESSAGE}")
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(customMessageReceiver)
        Log.d("BroadcastReceiver", "Receiver unregistered")
    }
}

@Composable
fun BroadcastReceiverScreen(
    receivedMessages: List<ReceivedMessage>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Header
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.CenterHorizontally),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Custom Broadcast Receiver",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Listening for custom broadcasts",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Message count
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Messages Received:",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${receivedMessages.size}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Messages list
        if (receivedMessages.isEmpty()) {
            // Empty state
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📭",
                        style = MaterialTheme.typography.displayMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Waiting for broadcasts...",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Open the Sender activity and send some messages!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(receivedMessages, key = { it.id }) { message ->
                    MessageCard(message = message)
                }
            }
        }
    }
}

@Composable
fun MessageCard(message: ReceivedMessage) {
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
    val timeString = dateFormat.format(Date(message.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = message.message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "⏰ Received at: $timeString",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}