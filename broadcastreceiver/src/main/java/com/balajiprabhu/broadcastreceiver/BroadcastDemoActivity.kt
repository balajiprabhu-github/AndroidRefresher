package com.balajiprabhu.broadcastreceiver

import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.balajiprabhu.broadcastreceiver.custom.BroadcastSenderActivity

class BroadcastDemoActivity : ComponentActivity() {

    // State to hold the current airplane mode status
    private var isAirplaneModeOn by mutableStateOf(false)

    // Initialize our receiver
    private val airplaneModeReceiver = AirplaneModeReceiver { isOn ->
        isAirplaneModeOn = isOn
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            MaterialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BroadcastDemoScreen(
                        isAirplaneModeOn = isAirplaneModeOn,
                        onNavigateToStatic = {
                            startActivity(Intent(this, StaticReceiverDemoActivity::class.java))
                        },
                        onNavigateToCustom = {
                            startActivity(Intent(this, BroadcastSenderActivity::class.java))
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // DYNAMIC REGISTRATION
        // We register the receiver here so it's active when the activity is visible
        val filter = IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        
        // Android 13+ requires explicit export flag
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(airplaneModeReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(airplaneModeReceiver, filter)
        }
    }

    override fun onStop() {
        super.onStop()
        // IMPORTANT: Always unregister to prevent memory leaks!
        unregisterReceiver(airplaneModeReceiver)
    }
}

@Composable
fun BroadcastDemoScreen(
    isAirplaneModeOn: Boolean,
    onNavigateToStatic: () -> Unit,
    onNavigateToCustom: () -> Unit,
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
            imageVector = Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Dynamic BroadcastReceiver",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Listening for AIRPLANE_MODE_CHANGED",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isAirplaneModeOn) 
                    MaterialTheme.colorScheme.errorContainer 
                else 
                    MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isAirplaneModeOn) "✈️ ON" else "📡 OFF",
                    style = MaterialTheme.typography.displayMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isAirplaneModeOn) "Airplane Mode is Enabled" else "Airplane Mode is Disabled",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Toggle Airplane Mode in your Quick Settings panel to see this update!",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.outline
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = onNavigateToStatic,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Learn about Static Receivers →")
        }

        Button(
            onClick = onNavigateToCustom,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Custom Broadcasts →")
        }

        Spacer(modifier = Modifier.height(16.dp))

        val context = androidx.compose.ui.platform.LocalContext.current
        Button(
            onClick = { 
                context.startActivity(Intent(context, com.balajiprabhu.broadcastreceiver.ordered.OrderedBroadcastDemoActivity::class.java))
            },
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("Ordered Broadcasts (Spam Filter) →")
        }
    }
}
