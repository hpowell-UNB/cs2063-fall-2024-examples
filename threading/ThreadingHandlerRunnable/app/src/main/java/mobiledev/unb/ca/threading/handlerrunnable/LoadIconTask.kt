package mobiledev.unb.ca.threading.handlerrunnable

import android.content.Context
import android.widget.ProgressBar
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.widget.ImageView

class LoadIconTask internal constructor(private val mAppContext: Context) : Thread() {
    private val mHandler: Handler = Handler(Looper.getMainLooper())
    private var imageView: ImageView? = null
    private var progressBar: ProgressBar? = null
    private val mBitmapResID = R.drawable.painter

    fun setImageView(imageView: ImageView?): LoadIconTask {
        this.imageView = imageView
        return this
    }

    fun setProgressBar(progressBar: ProgressBar?): LoadIconTask {
        this.progressBar = progressBar
        return this
    }

    override fun run() {
        mHandler.post { progressBar!!.visibility = ProgressBar.VISIBLE }

        // Simulating long-running operation
        for (i in 1..10) {
            sleep()
            mHandler.post { progressBar!!.progress = i * 10 }
        }
        mHandler.post {
            imageView!!.setImageBitmap(
                BitmapFactory.decodeResource(mAppContext.resources, mBitmapResID))
        }
        mHandler.post { progressBar!!.visibility = ProgressBar.INVISIBLE }
    }

    private fun sleep() {
        try {
            val mDelay = 500
            sleep(mDelay.toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

}