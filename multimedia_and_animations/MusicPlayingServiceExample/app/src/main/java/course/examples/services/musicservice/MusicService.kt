package course.examples.services.musicservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.media.MediaPlayer
import android.content.Intent
import android.content.Context
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat

class MusicService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var mStartId = 0

    override fun onCreate() {
        super.onCreate()

        // Set up the Media Player
        mediaPlayer = MediaPlayer.create(this, R.raw.badnews)
        if (null != mediaPlayer) {
            mediaPlayer!!.isLooping = false

            // Stop Service when music has finished playing
            mediaPlayer!!.setOnCompletionListener {
                // Stop Service if it was started with this ID
                // Otherwise let other start commands proceed
                stopSelf(mStartId)
            }
        }
        val context = applicationContext
        val channelId = context.packageName + ".channel_01"

        // Create the notification channel
        createNotificationChannel(context, channelId)

        // Create a notification area notification so the user
        // can get back to the MusicServiceClient
        startBackgroundService(context, channelId)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (null != mediaPlayer) {
            // ID for this start command
            mStartId = startId
            if (mediaPlayer!!.isPlaying) {
                // Rewind to beginning of song
                mediaPlayer!!.seekTo(0)
            } else {
                // Start playing song
                mediaPlayer!!.start()
            }
        }

        // Don't automatically restart this Service if it is killed
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        if (null != mediaPlayer) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
        }
    }

    // Can't bind to this Service
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createNotificationChannel(context: Context, channelId: String) {
        // Set the user visible channel name
        val name: CharSequence = context.getString(R.string.channel_name)

        // Set the user visible channel description
        val description = context.getString(R.string.channel_description)

        // Set the channel importance
        val importance = NotificationManager.IMPORTANCE_NONE

        // Create the NotificationChannel object
        val channel = NotificationChannel(channelId, name, importance)
        channel.description = description
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        channel.enableLights(true)
        channel.enableVibration(false)

        // Sets the notification light color for notifications posted to this
        // channel, if the device supports this feature.
        channel.lightColor = Color.GREEN

        // Register the channel with the system
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun startBackgroundService(context: Context, channelId: String) {
        val pendingIntent: PendingIntent = getPendingIntent(context)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setContentIntent(pendingIntent)
            .build()

        // Put this Service in a foreground state, so it won't
        // readily be killed by the system
        ServiceCompat.startForeground(
            this,  // service
            NOTIFICATION_ID, // id; cannot be 0
            notification,  // notification
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            } else {
                0
            },  // foregroundServiceType

        )
    }

    private fun getPendingIntent(context: Context): PendingIntent {
        // Set the tap action
        val notificationIntent = Intent(
            context,
            MusicServiceActivity::class.java
        )

        return PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    companion object {
        private const val NOTIFICATION_ID = 1
    }
}