package course.examples.ui.button

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class ButtonActivity : Activity() {
    private var mButton: Button? = null
    private var mTextView: TextView? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        // Get a reference to the Press Me Button
        mButton = findViewById(R.id.button)
        mTextView = findViewById(R.id.textView)

        // Reset the text when restarting the app
        if (null != savedInstanceState) {
            mTextView?.text = getString(R.string.got_pressed_string, mCount)
        }

        // Set an OnClickListener on this Button
        // Called each time the user clicks the Button
        mButton?.setOnClickListener { _: View? ->
            // Maintain a count of user presses
            // Display count as text on the screen
            mTextView?.text = getString(R.string.got_pressed_string, ++mCount)
        }
    }

    companion object {
        private var mCount = 0
    }
}