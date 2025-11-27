package com.balajiprabhu.broadcastreceiver.ordered

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

/**
 * Low Priority Receiver (Priority = 1)
 * Acts as the User's Inbox.
 */
class InboxReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra(OrderedConstants.EXTRA_MESSAGE) ?: return
        
        Log.d("OrderedBroadcast", "📨 Inbox received: '$message'")
        Toast.makeText(context, "📨 Inbox: $message", Toast.LENGTH_SHORT).show()
    }
}
