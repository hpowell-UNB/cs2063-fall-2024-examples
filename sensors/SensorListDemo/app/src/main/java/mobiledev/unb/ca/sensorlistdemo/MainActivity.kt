package mobiledev.unb.ca.sensorlistdemo

import android.hardware.Sensor
import android.os.Bundle
import android.widget.TextView
import android.hardware.SensorManager
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sensorListText: TextView = findViewById(R.id.sensors_list)

        // Retrieve a list of the supported sensors on the device
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL)
        for (currSensor in sensorList) {
            sensorListText.append(currSensor.name.trimIndent() + "\n\n")
        }

        sensorListText.movementMethod = ScrollingMovementMethod()
    }
}