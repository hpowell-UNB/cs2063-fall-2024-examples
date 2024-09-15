package course.examples.maplocation

import android.app.Activity
import android.os.Bundle
import android.widget.EditText
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Button
import java.lang.Exception

class MapLocation : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Required call through to Activity.onCreate()
        // Restore any saved instance state
        super.onCreate(savedInstanceState)

        // Set content view
        setContentView(R.layout.main)

        // Initialize UI elements
        val addressText = findViewById<EditText>(R.id.location)
        val button = findViewById<Button>(R.id.mapButton)

        // Link UI elements to actions in code        
        button.setOnClickListener {
            try {
                // Process text for network transmission
                var address = addressText.text.toString()
                address = address.replace(' ', '+')

                // Create Intent object for starting Google Maps application 
                val geoIntent = Intent(
                    Intent.ACTION_VIEW, Uri
                        .parse("geo:0,0?q=$address")
                )
                startActivity(geoIntent)
            } catch (e: Exception) {
                // Log any error messages to LogCat using Log.e()
                Log.e(TAG, e.toString())
            }
        }
    }

    companion object {
        private const val TAG = "MapLocation"
    }
}