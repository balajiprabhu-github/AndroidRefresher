package com.balajiprabhu.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class MusicPlayerService : Service() {
    inner class MusicBinder : Binder() {
        fun getService(): MusicPlayerService {
            return this@MusicPlayerService
        }
    }
    private val TAG = "MusicPlayerService"
    private val CHANNEL_ID = "music_player_channel"
    private val NOTIFICATION_ID = 1

    private lateinit var mediaPlayer: MediaPlayer
    
    override fun onBind(intent: Intent?): IBinder? {
        return MusicBinder()
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate: ")
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.sample_music)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: ")
        createNotificationChannel()

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentText("Music Player")
            .setContentText("Playing music in the background")
            .setSmallIcon(R.drawable.icon_music_note)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        mediaPlayer.start()

        return START_STICKY

    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: ")
        super.onDestroy()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Music Player Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    fun getPlaybackPosition(): Int {
        if(!this::mediaPlayer.isInitialized && !mediaPlayer.isPlaying) {
            return 0
        }
        return mediaPlayer.currentPosition
    }
}