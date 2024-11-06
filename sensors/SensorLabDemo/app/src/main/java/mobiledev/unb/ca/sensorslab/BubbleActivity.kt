package mobiledev.unb.ca.sensorslab

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.math.pow
import kotlin.math.sqrt

class BubbleActivity : Activity(), SensorEventListener {
    // References to SensorManager and accelerometer
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    // Attribute to storing the last sensor update
    private var lastUpdateMillis: Long = 0

    // The Main view
    private var mainViewFrame: RelativeLayout? = null

    // Bubble image's bitmap
    private var imageBitmap: Bitmap? = null

    // Display dimensions
    private var displayWidth = 0
    private var displayHeight = 0

    // Gesture Detector
    private lateinit var gestureDetector: GestureDetector

    // A TextView to hold the player message
    private var playerMessage: TextView? = null
    private var mFilter = NO_FILTER

    // Arrays for storing filtered values
    private val gravity = FloatArray(3)
    private val acceleration = FloatArray(3)

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        // Set up user interface
        mainViewFrame = findViewById(R.id.mainViewFrame)

        // Set up text view
        playerMessage = findViewById(R.id.playerMessage)

        // Load basic bubble Bitmap
        imageBitmap = BitmapFactory.decodeResource(resources, R.drawable.ball)

        // Initialize the sensor for use
        initSensor()

        // Get the screen dimensions
        val (width, height) = getScreenDimensions(this)

        // Subtract diameter of the ball from width and height
        displayWidth = width - SCALED_BITMAP_SIZE
        displayHeight = height - SCALED_BITMAP_SIZE

        // Lock the display in landscape
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    private fun initSensor() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (null == accelerometer) {
            Toast.makeText(applicationContext,
                getString(R.string.accelerometer_error),
                Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun getScreenDimensions(activity: Activity): Pair<Int, Int> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val windowInsets: WindowInsets = windowMetrics.windowInsets
            val insets = windowInsets.getInsetsIgnoringVisibility(
                WindowInsets.Type.navigationBars() or WindowInsets.Type.displayCutout())

            val insetsWidth = insets.right + insets.left
            val insetsHeight = insets.top + insets.bottom

            val b = windowMetrics.bounds
            Pair(b.width() - insetsWidth, b.height() - insetsHeight)
        } else {
            val size = Point()
            @Suppress("DEPRECATION")
            val display = activity.windowManager.defaultDisplay // deprecated in API 30
            @Suppress("DEPRECATION")
            display?.getSize(size) // deprecated in API 30
            Pair(size.x, size.y)
        }
    }

    override fun onResume() {
        super.onResume()

        // Register a listener for the accelerometer
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
        lastUpdateMillis = System.currentTimeMillis()

        // Setup the gesture listener
        setupGestureDetector()
    }

    override fun onSensorChanged(event: SensorEvent) {
        // Apply a low- and high-pass filter to the raw sensor values
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val actualTime = System.currentTimeMillis()
            if (actualTime - lastUpdateMillis > REFRESH_RATE) {
                lastUpdateMillis = actualTime
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                // Isolate the force of gravity with the low-pass filter.
                gravity[0] = applyLowPassFilter(x, gravity[0])
                gravity[1] = applyLowPassFilter(y, gravity[1])
                gravity[2] = applyLowPassFilter(z, gravity[2])

                // Remove the gravity contribution with the high-pass filter.
                acceleration[0] = applyHighPassFilter(x, gravity[0])
                acceleration[1] = applyHighPassFilter(y, gravity[1])
                acceleration[2] = applyHighPassFilter(z, gravity[2])

                // If there is a BubbleView, use its setSpeedAndDirection() method
                // to set its speed and direction based on the sensor values and the
                // current setting of mFilter, which will be one of NO_FILTER, LOW_PASS_FILTER, or HIGH_PASS_FILTER.
                val bubbleView = mainViewFrame!!.getChildAt(0) as? BubbleView
                if (null != bubbleView) {
                    when (mFilter) {
                        NO_FILTER -> {
                            bubbleView.setSpeedAndDirection(x, y)
                        }
                        LOW_PASS_FILTER -> {
                            bubbleView.setSpeedAndDirection(gravity[0], gravity[1])
                        }
                        HIGH_PASS_FILTER -> {
                            bubbleView.setSpeedAndDirection(acceleration[0], acceleration[1])
                        }
                        else -> {
                            Log.w(TAG, "Unknown filter type $mFilter.  No action taken")
                        }
                    }
                }
            }
        }
    }

    // Nothing to do here, just note that onAccuracyChanged must be implemented
    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
        // For now we will leave this unimplemented.
    }

    // De-emphasize transient forces
    private fun applyLowPassFilter(current: Float, gravity: Float): Float {
        val alpha = 0.8f
        return gravity * alpha + current * (1 - alpha)
    }

    // De-emphasize constant forces
    private fun applyHighPassFilter(current: Float, gravity: Float): Float {
        return current - gravity
    }

    // Set up GestureDetector
    private fun setupGestureDetector() {
        gestureDetector = GestureDetector(this, object : SimpleOnGestureListener() {
            // If there is a BubbleView, and a single tap intersects it, remove it.
            // If there are no BubbleViews, create a new BubbleView at the tap's location
            // and add it to mFrame.
            override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
                val x = event.rawX
                val y = event.rawY
                val numBubbles = mainViewFrame!!.childCount
                if (numBubbles != 0) {
                    val bv = mainViewFrame!!.getChildAt(0) as BubbleView
                    if (bv.intersects(x, y)) {
                        bv.stopMovement()
                    }
                } else {
                    val context = mainViewFrame!!.context
                    val bubbleView = BubbleView(context, x, y)
                    mainViewFrame!!.addView(bubbleView)
                    bubbleView.startMovement()
                    setPlayerMessage()
                }
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                // Cycle through the filter options
                mFilter = (mFilter + 1) % 3
                setPlayerMessage()
            }
        })
    }

    // Update the message that is displayed to the player
    fun setPlayerMessage() {
        val message: String = if (mainViewFrame!!.childCount == 0) {
            getString(R.string.lbl_tap_to_create_ball)
        } else {
            when (mFilter) {
                NO_FILTER -> {
                    getString(R.string.no_filter_message)
                }
                LOW_PASS_FILTER -> {
                    getString(R.string.lowpass_filter_message)
                }
                else -> {
                    getString(R.string.highpass_filter_message)
                }
            }
        }

        playerMessage!!.text = message
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    override fun onPause() {
        super.onPause()

        // Unregister the sensor event listener
        sensorManager.unregisterListener(this)
    }

    // BubbleView is a View that displays a bubble.
    // This class handles animating, drawing, and popping amongst other actions.
    inner class BubbleView internal constructor(context: Context?, x: Float, y: Float) :
        View(context) {
        private val painter = Paint()
        private var moverFuture: ScheduledFuture<*>? = null
        private val scaledBitmap: Bitmap = Bitmap.createScaledBitmap(imageBitmap!!, Companion.SCALED_BITMAP_SIZE,
            Companion.SCALED_BITMAP_SIZE, true)

        // location and direction of the bubble
        private var xPos: Float
        private var yPos: Float
        private val radius: Float = Companion.SCALED_BITMAP_SIZE / 2.0f

        // Speed of bubble
        private var mDx: Float
        private var mDy: Float

        // Rotation and speed of rotation of the bubble
        private var rotate: Long = 0
        private val speedOfRotation: Long

        // setSpeedAndDirection called by onSensorChanged(), values based on
        // accelerometer data and scaled
        fun setSpeedAndDirection(x: Float, y: Float) {
            // NOTE the layout is set to landscape.  This means that the coordinate
            // system does not change, however, the range for x and y are essentially reversed
            mDx = y
            mDy = x

            // TODO
            //  Once your app is working, experiment with alternative
            //  ways of calculating mDx and mDy based on x and y to change
            //  the "feel". Some ideas are shown below.

            // Example 1: Uncomment this to make the ball go faster!
            //mDx = 2 * y;
            //mDy = 2 * x;

            // Example 2: Uncomment this to make the ball accelerate based on sensor
            // input. You can also scale the contribution of x and y.
            //mDx += y
            //mDy += x
        }

        // Start moving the BubbleView & updating the display
        fun startMovement() {
            // Creates a WorkerThread
            val executor = Executors.newScheduledThreadPool(1)

            // Execute the run() in Worker Thread every REFRESH_RATE
            // milliseconds
            // Save reference to this job in mMoverFuture
            moverFuture = executor.scheduleWithFixedDelay({
                doMove()
                this@BubbleView.postInvalidate()
            }, 0, Companion.REFRESH_RATE.toLong(), TimeUnit.MILLISECONDS)
        }

        fun stopMovement() {
            if (null != moverFuture) {
                if (!moverFuture!!.isDone) {
                    moverFuture!!.cancel(true)
                }

                // This work will be performed on the UI Thread
                mainViewFrame!!.post {
                    mainViewFrame!!.removeView(this@BubbleView)
                    setPlayerMessage()
                }
            }
        }

        // Return true if x and y intersect the position of the Bubble
        @Synchronized
        fun intersects(x: Float, y: Float): Boolean {
            val centerX = xPos + radius
            val centerY = yPos + radius
            return sqrt((centerX - x).toDouble().pow(2.0) + (centerY - y).toDouble().pow(2.0)) <= radius
        }

        // Draw the Bubble at its current location
        @Synchronized
        override fun onDraw(canvas: Canvas) {
            canvas.save()
            rotate += speedOfRotation
            canvas.rotate(rotate.toFloat(), xPos + radius, yPos + radius)
            canvas.drawBitmap(scaledBitmap, xPos, yPos, painter)
            canvas.restore()
        }

        // Move the Bubble
        @Synchronized
        private fun doMove() {
            // Don't let the bubble go beyond the edge of the screen
            // Set the speed to 0 if the bubble hits an edge.
            xPos += mDx
            if (xPos >= displayWidth) {
                xPos = displayWidth.toFloat()
                mDx = 0f
            } else if (xPos <= 0) {
                xPos = 0f
                mDx = 0f
            }

            yPos += mDy
            if (yPos >= displayHeight) {
                yPos = displayHeight.toFloat()
                mDy = 0f
            } else if (yPos <= 0) {
                yPos = 0f
                mDy = 0f
            }
        }

        init {
            // Adjust position to center the bubble under user's finger
            xPos = x - radius
            yPos = y - radius

            // Set speed to 0 initially; it will be updated soon based on sensor input
            mDx = 0f
            mDy = 0f

            // Set speed of rotation to 5
            speedOfRotation = 5
            painter.isAntiAlias = true
        }
    }

    companion object {
        private const val TAG = "BubbleActivity"
        private const val NO_FILTER = 0
        private const val LOW_PASS_FILTER = 1
        private const val HIGH_PASS_FILTER = 2

        private const val BITMAP_SIZE = 128
        private const val REFRESH_RATE = 5
        private const val SCALED_BITMAP_SIZE = BITMAP_SIZE * 2
    }
}