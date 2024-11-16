package mobiledev.unb.ca.canvasdemo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

// Extension functions for the screen display size
val Context.displayWidth: Int
    get() = resources.displayMetrics.widthPixels

val Context.displayHeight: Int
    get() = resources.displayMetrics.heightPixels

class BubbleView(context: Context) : View(context) {
    private lateinit var listener: BubbleListener

    // Reference to the thread job
    private var scheduledFuture: ScheduledFuture<*>? = null

    // Painter object to redraw
    private val mPainter = Paint()

    // How fast we are moving
    private var mStepX: Int
    private var mStepY: Int

    // Display dimensions
    private var mCurrX: Float = context.displayWidth / 2.0f
    private var mCurrY: Float = context.displayHeight / 2.0f

    // Reference to the scaled bitmap object
    private var scaledBitmap: Bitmap

    init {
        // Set the bubble image from the drawable resource
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.b64)
        scaledBitmap = Bitmap.createScaledBitmap(bitmap, BITMAP_SIZE, BITMAP_SIZE,false)

        // Pick a random x and y step
        mStepX = generateRandomNumberInRange(-10, 10)
        mStepY = generateRandomNumberInRange(-10, 10)

        // Smooth out the edges
        mPainter.isAntiAlias = true
    }

    fun setListener(listener: BubbleListener) {
        this.listener = listener
    }

    @Synchronized
    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(scaledBitmap, mCurrX, mCurrY, mPainter)
    }

    fun startMovement() {
        // Creates a WorkerThread
        val executor = Executors.newScheduledThreadPool(1)

        // Execute the run() in Worker Thread every REFRESH_RATE milliseconds
        // Save reference to this job in mMoverFuture
        scheduledFuture = executor.scheduleWithFixedDelay({
            val stillOnScreen = moveWhileOnScreen()
            if (stillOnScreen) {
                this@BubbleView.postInvalidate()
            } else {
                stopMovement()
            }
        }, 0, REFRESH_RATE.toLong(), TimeUnit.MILLISECONDS)
    }

    private fun stopMovement() {
        if (null != scheduledFuture) {
            if (!scheduledFuture!!.isDone) {
                scheduledFuture!!.cancel(true)
            }

            listener.onBubbleViewRemoved(this)
        }
    }

    private fun moveWhileOnScreen(): Boolean {
        mCurrX += mStepX
        mCurrY += mStepY

        // Return true if the BubbleView is on the screen
        return mCurrX <= context.displayWidth
                && mCurrX + BITMAP_SIZE >= 0
                && mCurrY <= context.displayHeight
                && mCurrY + BITMAP_SIZE >= 0
    }

    private fun generateRandomNumberInRange(min: Int, max: Int): Int {
        return (min..max).random()
    }

    companion object {
        private const val BITMAP_SIZE = 64
        private const val REFRESH_RATE = 40
    }
}