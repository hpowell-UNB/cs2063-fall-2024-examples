package mobiledev.unb.ca.threading.executortask

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import java.util.concurrent.Executors

class LoadIconTask(activity: ThreadExecutorActivity) {
    private val appContext: Context = activity.applicationContext
    private var imageView: ImageView? = null
    private var progressBar: ProgressBar? = null
    fun setImageView(imageView: ImageView?): LoadIconTask {
        this.imageView = imageView
        return this
    }

    fun setProgressBar(progressBar: ProgressBar?): LoadIconTask {
        this.progressBar = progressBar
        return this
    }

    fun execute() {
        // Show the progress bar
        // NOTE: Not in background thread; does not need to use handler.post
        progressBar!!.visibility = ProgressBar.VISIBLE

        // Perform background call to read the information from the URL
        Executors.newSingleThreadExecutor().execute {
            // Here we are using a Runnable class
            val handler = Handler(Looper.getMainLooper())

            // Simulating long-running operation
            for (i in 1 until DOWNLOAD_TIME) {
                sleep()
                // NOTE: In background thread; must use handler.post
                handler.post { progressBar!!.progress = i * 10 }
            }

            val bitmap = BitmapFactory.decodeResource(appContext.resources, PAINTER)

            // Update the display
            // NOTE: In background thread; must use handler.post
            handler.post { updateDisplay(bitmap) }
        }
    }

    private fun sleep() {
        try {
            val mDelay = 500
            Thread.sleep(mDelay.toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun updateDisplay(bitmap: Bitmap) {
        // Show the icon
        // NOTE: Already included in handler.post operation
        imageView!!.setImageBitmap(bitmap)

        //  Reset the progress bar, and make it disappear
        // NOTE: Already included in handler.post operation
        progressBar!!.visibility = ProgressBar.INVISIBLE

        //  Create a Toast indicating that the image is loaded
        Toast.makeText(appContext,
            "Image Loaded",
            Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val DOWNLOAD_TIME = 10 // Download time simulation
        private val PAINTER = R.drawable.painter
    }
}