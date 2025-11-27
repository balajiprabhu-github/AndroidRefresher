package com.balajiprabhu.broadcastreceiver.ordered

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

/**
 * High Priority Receiver (Priority = 100)
 * Acts as a Spam Filter.
 */
class SpamFilterReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra(OrderedConstants.EXTRA_MESSAGE) ?: return
        
        Log.d("OrderedBroadcast", "🛡️ SpamFilter received: '$message'")
        
        if (message.contains("spam", ignoreCase = true)) {
            // BLOCK THE BROADCAST!
            // Lower priority receivers will NEVER see this.
            abortBroadcast()
            
            Log.d("OrderedBroadcast", "🚫 BLOCKED spam message!")
            Toast.makeText(context, "🚫 Spam Filter blocked a message!", Toast.LENGTH_SHORT).show()
        } else {
            Log.d("OrderedBroadcast", "✅ Message is clean. Passing to next receiver...")
        }
    }
}
