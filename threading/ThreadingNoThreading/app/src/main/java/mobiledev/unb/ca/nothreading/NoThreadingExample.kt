package mobiledev.unb.ca.nothreading

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import android.graphics.BitmapFactory
import android.view.View
import android.widget.Button
import android.widget.ImageView

class NoThreadingExample : Activity() {
    private var mIView: ImageView? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        mIView = findViewById<View>(R.id.imageView) as ImageView?

        val loadButton = findViewById<Button>(R.id.loadButton)
        loadButton.setOnClickListener { onClickLoadButton() }

        val otherButton = findViewById<Button>(R.id.otherButton)
        otherButton.setOnClickListener { onClickOtherButton() }
    }

    fun onClickOtherButton() {
        Toast.makeText(
            this@NoThreadingExample, "I'm Working",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun onClickLoadButton() {
        try {
            // Accentuates slow operation; this will block any actions
            Thread.sleep(5000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        mIView!!.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.painter))
    }
}