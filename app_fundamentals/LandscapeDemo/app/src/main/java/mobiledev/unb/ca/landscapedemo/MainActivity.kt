package mobiledev.unb.ca.landscapedemo

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            val context = applicationContext
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                Toast.makeText(
                    context,
                    "Running in Portrait Mode",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    "Running in Landscape Mode",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}