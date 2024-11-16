package mobiledev.unb.ca.canvasdemo

import android.app.Activity
import android.widget.RelativeLayout
import android.os.Bundle

class MainActivity : Activity(), BubbleListener {
    private var mainFrame: RelativeLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainFrame = findViewById(R.id.frame)
    }

    override fun onBubbleViewRemoved(bubbleView: BubbleView) {
        updateDisplay(bubbleView)
    }

    private fun updateDisplay(bubbleView: BubbleView) {
        // This work will be performed on the UI Thread
        mainFrame!!.post {
            //  Remove the BubbleView from the screen
            mainFrame!!.removeView(bubbleView)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            // Start the animation with 5 balls
            for (i in 0..4) {
                val bubbleView = BubbleView(applicationContext)
                bubbleView.setListener(this@MainActivity)
                bubbleView.startMovement()
                mainFrame?.addView(bubbleView)
            }
        }
    }
}