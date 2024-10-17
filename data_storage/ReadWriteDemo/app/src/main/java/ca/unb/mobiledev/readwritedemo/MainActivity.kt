package ca.unb.mobiledev.readwritedemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private lateinit var fileNameEditText: EditText
    private lateinit var fileDataEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fileNameEditText = findViewById(R.id.editFile)
        fileDataEditText = findViewById(R.id.editData)

        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnView = findViewById<Button>(R.id.btnView)

        val fileUtils = FileUtils(applicationContext)

        btnSave.setOnClickListener {
            val fileName = fileNameEditText.text.toString()
            if (TextUtils.isEmpty(fileName)) {
                Toast.makeText(applicationContext,
                    getString(R.string.err_file_name),
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val data = fileDataEditText.text.toString()
            if (TextUtils.isEmpty(data)) {
                Toast.makeText(applicationContext,
                    getString(R.string.err_file_data),
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Write the results to the file
            fileUtils.writeDataToFile(fileName, data)

            // Notification and clean up
            Toast.makeText(applicationContext, getString(R.string.lbl_data_saved), Toast.LENGTH_LONG).show()
            fileNameEditText.text.clear()
            fileDataEditText.text.clear()
        }

        btnView.setOnClickListener {
            val fileName = fileNameEditText.text.toString()
            if (TextUtils.isEmpty(fileName)) {
                Toast.makeText(applicationContext,
                    getString(R.string.err_file_name),
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Read the file details
            val data = fileUtils.readDataFromFile(fileName)

            //Displaying data on EditText
            fileDataEditText.setText(data)
        }
    }
}