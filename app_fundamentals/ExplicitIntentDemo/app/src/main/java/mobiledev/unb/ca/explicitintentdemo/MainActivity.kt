package mobiledev.unb.ca.explicitintentdemo

import android.content.ActivityNotFoundException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private var mButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mButton = findViewById<View>(R.id.button) as Button

        mButton!!.setOnClickListener {
            val intent = Intent(this@MainActivity, ActivityTwo::class.java)
            try {
                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "Unable to start the activity")
            }
        }
    }

    companion object {
        // String for LogCat documentation
        private const val TAG = "Explicit Intent Demo"
    }
}