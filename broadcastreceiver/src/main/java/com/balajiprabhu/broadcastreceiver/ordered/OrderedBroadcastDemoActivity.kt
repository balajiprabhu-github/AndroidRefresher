package com.balajiprabhu.broadcastreceiver.ordered

import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

class OrderedBroadcastDemoActivity : ComponentActivity() {

    private val spamFilterReceiver = SpamFilterReceiver()
    private val inboxReceiver = InboxReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    OrderedBroadcastScreen(
                        onSendClean = { sendOrderedMessage("Hello Friend! 👋") },
                        onSendSpam = { sendOrderedMessage("Buy cheap SPAM now! 🥫") },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun sendOrderedMessage(message: String) {
        val intent = Intent(OrderedConstants.ACTION_ORDERED_MESSAGE).apply {
            putExtra(OrderedConstants.EXTRA_MESSAGE, message)
            setPackage(packageName) // Explicit for Android 8+
        }

        // Send Ordered Broadcast
        // permission = null
        // appOp = null
        // resultReceiver = null (we don't need final result here)
        // scheduler = null (run on main thread)
        // initialCode = 0
        // initialData = null
        // initialExtras = null
        sendOrderedBroadcast(intent, null)
    }

    override fun onStart() {
        super.onStart()
        
        // Register Spam Filter (High Priority)
        val spamFilter = IntentFilter(OrderedConstants.ACTION_ORDERED_MESSAGE).apply {
            priority = 100 // HIGH PRIORITY
        }
        
        // Register Inbox (Low Priority)
        val inboxFilter = IntentFilter(OrderedConstants.ACTION_ORDERED_MESSAGE).apply {
            priority = 1 // LOW PRIORITY
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(spamFilterReceiver, spamFilter, RECEIVER_NOT_EXPORTED)
            registerReceiver(inboxReceiver, inboxFilter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(spamFilterReceiver, spamFilter)
            registerReceiver(inboxReceiver, inboxFilter)
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(spamFilterReceiver)
        unregisterReceiver(inboxReceiver)
    }
}

@Composable
fun OrderedBroadcastScreen(
    onSendClean: () -> Unit,
    onSendSpam: () -> Unit,
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
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Ordered Broadcasts",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Priority Chain: Filter (100) → Inbox (1)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onSendClean,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send Clean Message")
        }
        Text(
            text = "Result: Filter passes it → Inbox shows it",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        Button(
            onClick = onSendSpam,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Send Spam Message")
        }
        Text(
            text = "Result: Filter BLOCKS it → Inbox never sees it",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
