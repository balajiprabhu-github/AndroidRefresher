package com.balajiprabhu.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.balajiprabhu.activity.ui.theme.AndroidRefreshTheme
import androidx.lifecycle.compose.currentStateAsState

/** A small, standalone screen for observing the Activity lifecycle from Compose. */
class LifecycleActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AndroidRefreshTheme {
                LifecycleScreen()
            }
        }
    }
}

@Composable
private fun LifecycleScreen() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var lastEvent by remember { mutableStateOf("Nothing received yet") }
    val eventHistory = remember { mutableStateListOf<String>() }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lastEvent = event.targetState.name
            eventHistory += event.name
            Log.d("LifecycleActivity", "Received: ${event.name}")
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Lifecycle object", style = MaterialTheme.typography.headlineMedium)
        Text("Current state: ${lifecycleOwner.lifecycle.currentStateAsState().value}")
        Text("Last event: $lastEvent")
        Text("Event history:", style = MaterialTheme.typography.titleMedium)

        Button(onClick = {
            context.startActivity(Intent(context, RotationActivity::class.java))
        }) {
            Text("Open Rotation Activity")
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            eventHistory.forEachIndexed { index, event ->
                Text("${index + 1}. $event")
            }
        }


        Text("Try opening another Activity, returning here, or rotating the device.")
    }
}
