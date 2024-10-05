package mobiledev.unb.ca.threading.handlermessages

import android.content.Context
import android.widget.ProgressBar
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.ImageView

class LoadIconTask internal constructor(private val mContext: Context) : Thread() {
    private val mHandler: UIHandler = UIHandler(Looper.getMainLooper())

    override fun run() {
        var message = mHandler.obtainMessage(
            SET_PROGRESS_BAR_VISIBILITY,
            ProgressBar.VISIBLE)
        mHandler.sendMessage(message)
        val mResId = R.drawable.painter
        val tmp = BitmapFactory.decodeResource(mContext.resources, mResId)
        for (i in 1..10) {
            sleep()
            message = mHandler.obtainMessage(PROGRESS_UPDATE, i * 10)
            mHandler.sendMessage(message)
        }
        message = mHandler.obtainMessage(SET_BITMAP, tmp)
        mHandler.sendMessage(message)
        message = mHandler.obtainMessage(
            SET_PROGRESS_BAR_VISIBILITY,
            ProgressBar.INVISIBLE)
        mHandler.sendMessage(message)
    }

    fun setImageView(imageView: ImageView?): LoadIconTask {
        mHandler.setImageView(imageView)
        return this
    }

    fun setProgressBar(progressBar: ProgressBar?): LoadIconTask {
        mHandler.setProgressBar(progressBar)
        return this
    }

    private fun sleep() {
        try {
            val mDelay = 500
            sleep(mDelay.toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    internal class UIHandler(looper: Looper) : Handler(looper) {
        private var mImageView: ImageView? = null
        private var mProgressBar: ProgressBar? = null
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                SET_PROGRESS_BAR_VISIBILITY -> {
                    mProgressBar!!.visibility = (msg.obj as Int)
                }
                PROGRESS_UPDATE -> {
                    mProgressBar!!.progress = (msg.obj as Int)
                }
                SET_BITMAP -> {
                    mImageView!!.setImageBitmap(msg.obj as Bitmap)
                }
            }
        }

        fun setImageView(mImageView: ImageView?) {
            this.mImageView = mImageView
        }

        fun setProgressBar(mProgressBar: ProgressBar?) {
            this.mProgressBar = mProgressBar
        }
    }

    companion object {
        const val SET_PROGRESS_BAR_VISIBILITY = 0
        const val PROGRESS_UPDATE = 1
        const val SET_BITMAP = 2
    }
}