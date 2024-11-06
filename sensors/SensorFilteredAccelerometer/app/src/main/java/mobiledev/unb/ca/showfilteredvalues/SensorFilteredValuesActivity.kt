package mobiledev.unb.ca.showfilteredvalues

import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.TextView
import android.os.Bundle
import android.hardware.SensorEvent

class SensorFilteredValuesActivity : Activity(), SensorEventListener {
    // References to SensorManager and accelerometer
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastUpdateMillis: Long = 0

    // Arrays for storing filtered values
    private val filteredGravityValuesList = FloatArray(3)
    private val filteredAccelerationValuesList = FloatArray(3)

    // Text views for displaying the values
    private var xValueView: TextView? = null
    private var yValueView: TextView? = null
    private var zValueView: TextView? = null
    private var xGravityView: TextView? = null
    private var yGravityView: TextView? = null
    private var zGravityView: TextView? = null
    private var xAccelView: TextView? = null
    private var yAccelView: TextView? = null
    private var zAccelView: TextView? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        // Get references to the TextView objects
        xValueView = findViewById(R.id.x_value_view)
        yValueView = findViewById(R.id.y_value_view)
        zValueView = findViewById(R.id.z_value_view)
        xGravityView = findViewById(R.id.x_lowpass_view)
        yGravityView = findViewById(R.id.y_lowpass_view)
        zGravityView = findViewById(R.id.z_lowpass_view)
        xAccelView = findViewById(R.id.x_highpass_view)
        yAccelView = findViewById(R.id.y_highpass_view)
        zAccelView = findViewById(R.id.z_highpass_view)

        initSensor()
    }

    private fun initSensor() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    // Register listener
    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer,
            SensorManager.SENSOR_DELAY_UI)
        lastUpdateMillis = System.currentTimeMillis()
    }

    // Unregister listener
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    // Process new reading
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val actualTimeMillis = System.currentTimeMillis()
            if (actualTimeMillis - lastUpdateMillis > 500) {
                lastUpdateMillis = actualTimeMillis
                val rawX = event.values[0]
                val rawY = event.values[1]
                val rawZ = event.values[2]

                // Apply low-pass filter
                filteredGravityValuesList[0] = lowPass(rawX, filteredGravityValuesList[0])
                filteredGravityValuesList[1] = lowPass(rawY, filteredGravityValuesList[1])
                filteredGravityValuesList[2] = lowPass(rawZ, filteredGravityValuesList[2])

                // Apply high-pass filter
                filteredAccelerationValuesList[0] = highPass(rawX, filteredGravityValuesList[0])
                filteredAccelerationValuesList[1] = highPass(rawY, filteredGravityValuesList[1])
                filteredAccelerationValuesList[2] = highPass(rawZ, filteredGravityValuesList[2])

                xValueView!!.text = rawX.toString()
                yValueView!!.text = rawY.toString()
                zValueView!!.text = rawZ.toString()

                xGravityView!!.text = filteredGravityValuesList[0].toString()
                yGravityView!!.text = filteredGravityValuesList[1].toString()
                zGravityView!!.text = filteredGravityValuesList[2].toString()

                xAccelView!!.text = filteredAccelerationValuesList[0].toString()
                yAccelView!!.text = filteredAccelerationValuesList[1].toString()
                zAccelView!!.text = filteredAccelerationValuesList[2].toString()
            }
        }
    }

    // De-emphasize transient forces
    private fun lowPass(current: Float, gravity: Float): Float {
        val alpha = 0.8f
        return gravity * alpha + current * (1 - alpha)
    }

    // De-emphasize constant forces
    private fun highPass(current: Float, gravity: Float): Float {
        return current - gravity
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes
    }
}