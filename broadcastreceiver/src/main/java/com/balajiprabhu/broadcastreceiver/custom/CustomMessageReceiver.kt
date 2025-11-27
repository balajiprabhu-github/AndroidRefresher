package com.balajiprabhu.broadcastreceiver.custom

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class CustomMessageReceiver(
    val onMessageReceived: (String, Long) -> Unit
) : BroadcastReceiver() {

    companion object {
        private const val TAG = "CustomMessageReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive called! Action: ${intent?.action}")
        
        when (intent?.action) {
            BroadcastConstants.ACTION_CUSTOM_MESSAGE -> {
                // Extract data from the broadcast
                val message =
                    intent.getStringExtra(BroadcastConstants.EXTRA_MESSAGE) ?: "No message"
                val timestamp = intent.getLongExtra(BroadcastConstants.EXTRA_TIMESTAMP, 0L)

                Log.d(TAG, "Received broadcast: $message at $timestamp")
                
                // Show toast for immediate feedback
                context?.let {
                    Toast.makeText(it, "📨 $message", Toast.LENGTH_SHORT).show()
                }

                // Notify the UI
                onMessageReceived(message, timestamp)
            }
            else -> {
                Log.d(TAG, "Received unknown action: ${intent?.action}")
            }
        }
    }
}