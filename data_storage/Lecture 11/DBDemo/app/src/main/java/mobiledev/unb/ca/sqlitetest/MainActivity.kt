package mobiledev.unb.ca.sqlitetest

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.AdapterView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.content.DialogInterface
import android.os.Handler
import android.view.View
import android.widget.EditText
import android.view.WindowManager
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import androidx.appcompat.widget.Toolbar
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var mListView: ListView
    private lateinit var executor: ExecutorService
    private lateinit var handler: Handler

    private lateinit var dbManager: DBManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set the executor service
        executor = Executors.newSingleThreadExecutor()
        handler = Handler(Looper.getMainLooper())

        // Set the toolbar actions
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Add the listener events
        mListView = findViewById(R.id.listview)
        mListView.setOnItemLongClickListener { _: AdapterView<*>?, _: View?, _: Int, id: Long ->
            deleteItem(
                id.toInt())
            true
        }
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val builder = AlertDialog.Builder(this@MainActivity)
            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_layout, null)

            builder.setView(dialogView)
                .setPositiveButton(getString(R.string.dialog_button_add)) { _: DialogInterface?, _: Int ->
                    val itemEditText = dialogView.findViewById<EditText>(R.id.item_edit_text)
                    val numEditText = dialogView.findViewById<EditText>(R.id.number_edit_text)
                    val item = itemEditText.text.toString()
                    val num = numEditText.text.toString()
                    addItem(item, num)
                }
                .setNegativeButton(getString(R.string.dialog_button_cancel)) { _: DialogInterface?, _: Int -> }

            val alertDialog = builder.create()
            alertDialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            alertDialog.show()
        }

        // Create a new DBManager object
        dbManager = DBManager(this)
        setUpListView()
    }

    public override fun onDestroy() {
        super.onDestroy()
        dbManager.close()
    }

    private fun setUpListView() {
        // Cursor query attributes
        val queryFrom = arrayOf(DatabaseHelper.ITEM, DatabaseHelper.NUM)
        val queryTo = intArrayOf(R.id.item_textview, R.id.num_textview)

        executor.execute {
            // Perform background call to retrieve the records
            val cursor = dbManager.listAllRecords()

            handler.post {
                // Update the UI with the results
                val adapter = SimpleCursorAdapter(this,
                    R.layout.list_layout,
                    cursor,
                    queryFrom,
                    queryTo,
                    0)
                adapter.notifyDataSetChanged()
                mListView.adapter = adapter
            }
        }
    }

    private fun addItem(item: String, num: String) {
        executor.execute {
            // Perform background call to save the record
            dbManager.insertRecord(item, num)

            // Update the UI with the results
            handler.post { setUpListView() }
        }
    }

    private fun deleteItem(id: Int) {
        executor.execute {
            // Perform background call to remove the record
            dbManager.deleteRecord(id)

            // Update the UI with the results
            handler.post { setUpListView() }
        }
    }
}