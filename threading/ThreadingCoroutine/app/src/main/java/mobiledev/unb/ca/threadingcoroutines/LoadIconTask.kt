package mobiledev.unb.ca.threadingcoroutines

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.ProgressBar
import kotlinx.coroutines.*

class LoadIconTask(activity: MainActivity) {
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
        MainScope().launch {
            // Show the progress bar
            progressBar!!.visibility = ProgressBar.VISIBLE

            val bitmap = doLoadBitmap(appContext, progressBar)
            imageView!!.setImageBitmap(bitmap)

            // Hide the progress bar
            progressBar!!.visibility = ProgressBar.INVISIBLE
        }
    }

    private suspend fun doLoadBitmap(appContext: Context,
                                     progressBar: ProgressBar?): Bitmap? {
        // Create a new coroutine to move the execution off the UI thread
        return withContext(Dispatchers.IO) {
            // Simulating long-running operation
            for (i in 1 until DOWNLOAD_TIME) {
                sleep()
                progressBar!!.progress = i * 10
            }

            // Decode the image and return it
            val bitmap = BitmapFactory.decodeResource(appContext.resources, PAINTER)
            bitmap
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

    companion object {
        private val PAINTER = R.drawable.painter
        private const val DOWNLOAD_TIME = 10 // Download time simulation
    }
}