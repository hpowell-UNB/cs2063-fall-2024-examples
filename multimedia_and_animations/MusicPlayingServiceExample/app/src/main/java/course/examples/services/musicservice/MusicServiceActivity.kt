package course.examples.services.musicservice

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

class MusicServiceActivity : Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        // Intent used for starting the MusicService
        val musicServiceIntent = Intent(
            applicationContext,
            MusicService::class.java
        )

        val startButton: Button = findViewById(R.id.start_button)
        startButton.setOnClickListener {
            // Start the MusicService using the Intent
            startService(musicServiceIntent)
        }

        val stopButton: Button = findViewById(R.id.stop_button)
        stopButton.setOnClickListener {
            // Stop the MusicService using the Intent
            stopService(musicServiceIntent)
        }

        // Ensure the permissions are set for posting notifications
        checkNotificationPermissions()
    }

    /**
     * Checks for the appropriate permissions
     */
    private fun checkNotificationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestRuntimePermissions()
            }
        }
    }

    /**
     * Grants the appropriate permissions
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestRuntimePermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.POST_NOTIFICATIONS
        ), NOTIFICATION_ID)
    }

    companion object {
        private const val NOTIFICATION_ID = 1
    }
}