package mobiledev.unb.ca.showrawvalues

import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.TextView
import android.os.Bundle
import android.hardware.SensorEvent

class SensorRawAccelerometerActivity : Activity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var xValueView: TextView? = null
    private var yValueView: TextView? = null
    private var zValueView: TextView? = null
    private var lastUpdateMillis: Long = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        xValueView = findViewById(R.id.x_value_view)
        yValueView = findViewById(R.id.y_value_view)
        zValueView = findViewById(R.id.z_value_view)

        initSensor()
    }

    private fun initSensor() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    // Register listener
    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
        lastUpdateMillis = System.currentTimeMillis()
    }

    // Unregister listener
    override fun onPause() {
        sensorManager.unregisterListener(this)
        super.onPause()
    }

    // Process new reading
    override fun onSensorChanged(event: SensorEvent) {
        // Do something with this sensor value
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val actualTimeMillis = System.currentTimeMillis()
            if (actualTimeMillis - lastUpdateMillis > UPDATE_THRESHOLD) {
                lastUpdateMillis = actualTimeMillis
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                xValueView!!.text = x.toString()
                yValueView!!.text = y.toString()
                zValueView!!.text = z.toString()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes
    }

    companion object {
        private const val UPDATE_THRESHOLD = 500
    }
}