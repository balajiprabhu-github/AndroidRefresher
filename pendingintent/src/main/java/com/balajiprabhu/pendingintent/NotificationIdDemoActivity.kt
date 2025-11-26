package com.balajiprabhu.pendingintent

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.balajiprabhu.pendingintent.ui.theme.AndroidRefreshTheme

/**
 * REVISION DEMO: Understanding Notification IDs
 * 
 * This activity demonstrates:
 * 1. Same ID → Notifications REPLACE each other
 * 2. Different IDs → Multiple notifications appear
 */
class NotificationIdDemoActivity : ComponentActivity() {

    // Permission launcher - we check permission before each notification using hasNotificationPermission()
    // so no action needed in callback
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _: Boolean ->
        // No action needed - permission is checked before each notification
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Request permission if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        // Create channel
        createNotificationChannel()

        setContent {
            AndroidRefreshTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NotificationIdDemoScreen(
                        modifier = Modifier.padding(innerPadding),
                        onSameIdClick = { showNotificationWithSameId() },
                        onDifferentIdClick = { showNotificationWithDifferentId() },
                        onClearAll = { clearAllNotifications() }
                    )
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                "demo_channel",
                "Demo Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "For notification ID demo"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private var clickCount = 0

    // Shows notifications with SAME ID (1) - they will REPLACE each other
    @SuppressLint("MissingPermission")
    private fun showNotificationWithSameId() {
        clickCount++
        
        val intent = Intent(this, NotificationTargetActivity::class.java).apply {
            putExtra("messageId", "Same ID - Click #$clickCount")
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, "demo_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Same ID Demo")
            .setContentText("Click #$clickCount - I REPLACE previous notification")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // SAME ID = 1 every time
        if (hasNotificationPermission()) {
            NotificationManagerCompat.from(this).notify(1, notification)
        }
    }

    private var notificationIdCounter = 100

    // Shows notifications with DIFFERENT IDs - they will STACK
    @SuppressLint("MissingPermission")
    private fun showNotificationWithDifferentId() {
        notificationIdCounter++
        
        val intent = Intent(this, NotificationTargetActivity::class.java).apply {
            putExtra("messageId", "Different ID - #$notificationIdCounter")
        }

        val pendingIntent = PendingIntent.getActivity(
            this, notificationIdCounter, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, "demo_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle("Different ID Demo")
            .setContentText("Notification #$notificationIdCounter - I'm SEPARATE!")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // DIFFERENT ID each time
        if (hasNotificationPermission()) {
            NotificationManagerCompat.from(this).notify(notificationIdCounter, notification)
        }
    }

    private fun clearAllNotifications() {
        NotificationManagerCompat.from(this).cancelAll()
        clickCount = 0
        notificationIdCounter = 100
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}

@Composable
fun NotificationIdDemoScreen(
    modifier: Modifier,
    onSameIdClick: () -> Unit,
    onDifferentIdClick: () -> Unit,
    onClearAll: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Notification ID Demo",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📱 Same ID (ID = 1)",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Each tap REPLACES the previous notification",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onSameIdClick, modifier = Modifier.fillMaxWidth()) {
                    Text("Send with SAME ID")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📬 Different IDs (101, 102, 103...)",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Each tap creates a NEW notification",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onDifferentIdClick, modifier = Modifier.fillMaxWidth()) {
                    Text("Send with DIFFERENT ID")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(onClick = onClearAll) {
            Text("Clear All Notifications")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Try tapping each button 3-4 times and see the difference!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
