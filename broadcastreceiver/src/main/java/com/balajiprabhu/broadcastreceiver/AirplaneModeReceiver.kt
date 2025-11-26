package com.balajiprabhu.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

/**
 * A simple BroadcastReceiver that listens for Airplane Mode changes.
 * 
 * @param onAirplaneModeChanged Callback function triggered when mode changes
 */
class AirplaneModeReceiver(
    private val onAirplaneModeChanged: (Boolean) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // 1. Check if the action is what we expect
        if (intent.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
            
            // 2. Extract data from the intent
            // "state" is a boolean extra sent with this broadcast
            val isAirplaneModeEnabled = intent.getBooleanExtra("state", false)
            
            // 3. Perform action (Update UI, show Toast, etc.)
            val message = if (isAirplaneModeEnabled) "Airplane Mode ON ✈️" else "Airplane Mode OFF 📡"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            
            // 4. Notify the UI via callback
            onAirplaneModeChanged(isAirplaneModeEnabled)
        }
    }
}
