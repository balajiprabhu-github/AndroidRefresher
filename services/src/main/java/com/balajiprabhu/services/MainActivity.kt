package com.balajiprabhu.services

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.balajiprabhu.services.ui.theme.AndroidRefreshTheme
import kotlinx.coroutines.delay
import kotlin.text.format
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val TAG = "MainActivity"
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startMusicService()
        }
    }

    private var musicService by mutableStateOf<MusicPlayerService?>(null)
    private var isBound by mutableStateOf(false)

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            // Service connected
            Log.d(TAG, "onServiceConnected: ")
            val binder = service as MusicPlayerService.MusicBinder
            musicService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "onServiceDisconnected: ")
            // Service disconnected
            musicService = null
            isBound = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidRefreshTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ServiceControllerScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        musicService = musicService,
                        isBound = isBound,
                        onPlayClick = {
                            checkAndRequestNotificationPermission()
                        },
                        onStopClick = {
                            stopAndUnbindService()
                        }
                    )
                }
            }
        }
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                )
                        == PackageManager.PERMISSION_GRANTED -> {
                    startMusicService()
                }

                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            startMusicService()
        }
    }

    private fun startMusicService() {
        val intent = Intent(this, MusicPlayerService::class.java)
        startService(intent)
    }

    private fun stopAndUnbindService() {
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
        val intent = Intent(this, MusicPlayerService::class.java)
        stopService(intent)
    }

    override fun onStart() {
        super.onStart()
        Intent(this, MusicPlayerService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }
}
fun formatTime(millis: Int): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}
@Composable
fun ServiceControllerScreen(
    modifier: Modifier,
    musicService: MusicPlayerService?,
    isBound: Boolean,
    onPlayClick: () -> Unit,
    onStopClick: () -> Unit
) {
    var playbackPosition by remember { mutableIntStateOf(0) }

    if (isBound) {
        LaunchedEffect(Unit) {
            while (true) {
                playbackPosition = musicService?.getPlaybackPosition() ?: 0
                delay(1000L)
            }
        }
    }
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = {
            onPlayClick()
        }) {
            Text(text = "Play music")
        }

        Button(onClick = { onStopClick() }
        ) {
            Text(text = "Stop music")
        }

        if (isBound) {
            Text(text = "Playback position: ${formatTime(playbackPosition)}")
        }
    }
}

@Composable
fun StartService(onPlayClick: () -> Unit, modifier: Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val context = LocalContext.current
        Button(onClick = {
            val intent = Intent(context, MusicPlayerService::class.java)
            context.startService(intent)
        }) {
            Text(text = "Play music")
        }

        Button(onClick = {
            val intent = Intent(context, MusicPlayerService::class.java)
            context.stopService(intent)
        }) {
            Text(text = "Stop music")
        }
    }
}