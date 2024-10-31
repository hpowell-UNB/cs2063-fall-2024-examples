package mobiledev.unb.ca.pinchandzoomdemo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import android.view.View.OnTouchListener
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.max
import kotlin.math.min


class MainActivity : AppCompatActivity() {
    // ScaleGestureListener is used to handle pinch gestures
    private var scaleGestureDetector: ScaleGestureDetector? = null

    private var imageView: ImageView? = null
    private var handler: Handler? = null
    private var pinchToZoom = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        imageView!!.setOnTouchListener(onTouchListener());

        handler = Handler(Looper.getMainLooper())
        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener(imageView!!))
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        scaleGestureDetector!!.onTouchEvent(ev)
        return true
    }

    private fun resetFlag() {
        handler?.postDelayed(Runnable { pinchToZoom = false }, 500)
    }

    internal inner class ScaleListener internal constructor(private var mImageView: ImageView): SimpleOnScaleGestureListener() {
        private var scaleFactor = 1.0f

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= scaleGestureDetector!!.scaleFactor
            scaleFactor = max(0.5f, min(scaleFactor, 2.0f))
            mImageView.scaleX = scaleFactor
            mImageView.scaleY = scaleFactor
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            pinchToZoom = true
            return super.onScaleBegin(detector)
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            resetFlag()
            super.onScaleEnd(detector)
        }
    }

    private fun onTouchListener(): OnTouchListener {
        return object : OnTouchListener {
            var dX: Float = 0f
            var dY: Float = 0f
            var lastAction: Int = 0

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        dX = view.x - event.rawX
                        dY = view.y - event.rawY
                        lastAction = MotionEvent.ACTION_DOWN
                    }

                    MotionEvent.ACTION_MOVE -> {
                        onTouchEvent(event)
                        if (!pinchToZoom) {
                            view.y = event.rawY + dY
                            view.x = event.rawX + dX
                            lastAction = MotionEvent.ACTION_MOVE
                        }
                    }

                    MotionEvent.ACTION_UP -> {
                        handler?.removeCallbacksAndMessages(null)
                        pinchToZoom = false
                    }

                    MotionEvent.ACTION_POINTER_DOWN -> {
                        dX = view.x - event.rawX
                        dY = view.y - event.rawY
                        lastAction = MotionEvent.ACTION_POINTER_DOWN
                    }

                    else -> return false
                }
                return true
            }
        }
    }
}