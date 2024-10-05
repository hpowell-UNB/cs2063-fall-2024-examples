package mobiledev.unb.ca.threading.executortask

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ThreadExecutorActivity : AppCompatActivity() {
    private lateinit var loadButton: Button

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        loadButton = findViewById(R.id.loadButton)
        loadButton.setOnClickListener { onClickLoadButton() }

        val otherButton = findViewById<Button>(R.id.otherButton)
        otherButton.setOnClickListener { onClickOtherButton() }
    }

    fun onClickLoadButton() {
        loadButton.isEnabled = false
        val loadIconTask: LoadIconTask = LoadIconTask(this)
            .setImageView(findViewById(R.id.imageView))
            .setProgressBar(findViewById(R.id.progressBar))
        loadIconTask.execute()
    }

    fun onClickOtherButton() {
        Toast.makeText(this@ThreadExecutorActivity, "I'm Working",
            Toast.LENGTH_SHORT).show()
    }
}