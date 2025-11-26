package com.balajiprabhu.pendingintent

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.balajiprabhu.pendingintent.ui.theme.AndroidRefreshTheme

class NotificationSenderActivity : ComponentActivity() {

    // CONCEPT 1: Permission Launcher for Android 13+
    // This is registered BEFORE onCreate to handle runtime permission
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if(isGranted) {
            showNotification()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // CONCEPT 2: Create notification channel (required for Android O+)
        createNotificationChannel()

        setContent {
            AndroidRefreshTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NotificationSenderScreen(modifier = Modifier.padding(innerPadding)) {
                        handleNotificationClick()
                    }
                }
            }
        }
    }

    // CONCEPT 3: Check Android version for permission requirement
    private fun handleNotificationClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ requires POST_NOTIFICATIONS permission
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            // Older versions don't need runtime permission
            showNotification()
        }
    }

    // CONCEPT 4: Notification Channel (categorizes notifications)
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "messaging_channel"
            val name = "Messages"
            val descriptionText = "Notifications for new messages"
            val importance = NotificationManager.IMPORTANCE_HIGH // Changed to HIGH for better visibility

            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // CONCEPT 5: Creating PendingIntent and showing notification
    private fun showNotification() {
        // Step 1: Create the Intent that will open NotificationTargetActivity
        val intent = Intent(this, NotificationTargetActivity::class.java).apply {
            // Pass data through Intent extras
            putExtra("messageId", "MSG_12345")
            // Clear back stack and create new task
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Step 2: Wrap Intent in PendingIntent
        // THIS IS THE KEY CONCEPT - PendingIntent is a token that grants permission
        val pendingIntent = PendingIntent.getActivity(
            this,                    // Context
            0,                       // Request code (for identifying this PendingIntent)
            intent,                  // The Intent to execute
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            // FLAG_UPDATE_CURRENT: Update existing PendingIntent if it exists
            // FLAG_IMMUTABLE: Cannot be modified (required for Android 12+, more secure)
        )

        // Step 3: Build the notification
        val notification = NotificationCompat.Builder(this, "messaging_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle("New Message")
            .setContentText("You have a new message!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)  // THIS is where PendingIntent is used!
            .setAutoCancel(true)  // Dismiss notification when tapped
            .build()

        // Step 4: Show the notification
        val notificationManager = NotificationManagerCompat.from(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(1, notification)
            }
        } else {
            notificationManager.notify(1, notification)
        }
    }
}


@Composable
fun NotificationSenderScreen(
    modifier: Modifier,
    onButtonClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "PendingIntent Demo",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(onClick = { onButtonClick() }) {
            Text(text = "Send Notification")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Tap button → See notification → Tap notification → Opens target activity",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
