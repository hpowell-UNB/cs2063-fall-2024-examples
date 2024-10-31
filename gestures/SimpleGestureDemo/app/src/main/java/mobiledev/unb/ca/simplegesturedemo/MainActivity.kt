package mobiledev.unb.ca.simplegesturedemo

import android.os.Bundle
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var gestureDetector: GestureDetector
    private var textView: TextView? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.textview)

        gestureDetector = GestureDetector(this, MyGestureListener())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    internal inner class MyGestureListener : SimpleOnGestureListener() {
        override fun onDown(event: MotionEvent): Boolean {
            textView!!.setText(R.string.lbl_on_down)
            return true
        }

        override fun onFling(
            event1: MotionEvent?, event2: MotionEvent,
            velocityX: Float, velocityY: Float,
        ): Boolean {
            val text = getString(R.string.lbl_on_fling, velocityX, velocityY)
            textView!!.text = text
            return true
        }

        override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
            textView!!.setText(R.string.lbl_on_single_tap_confirmed)
            return true
        }
    }
}