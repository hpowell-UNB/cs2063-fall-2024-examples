package mobiledev.unb.ca.threading.handlerrunnable

import android.os.Bundle
import android.widget.Toast
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HandlerRunnableActivity : AppCompatActivity() {
    private lateinit var loadButton: Button

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        loadButton = findViewById<Button>(R.id.loadButton)
        loadButton.setOnClickListener { onClickLoadButton() }

        val otherButton = findViewById<Button>(R.id.otherButton)
        otherButton.setOnClickListener { onClickOtherButton() }
    }

    fun onClickLoadButton() {
        loadButton.isEnabled = false
        val loadIconTask: LoadIconTask = LoadIconTask(this)
            .setImageView(findViewById(R.id.imageView))
            .setProgressBar(findViewById(R.id.progressBar))
        loadIconTask.start()
    }

    fun onClickOtherButton() {
        Toast.makeText(this@HandlerRunnableActivity, "I'm Working",
            Toast.LENGTH_SHORT).show()
    }
}