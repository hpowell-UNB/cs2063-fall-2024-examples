package mobiledev.unb.ca.roompersistencetest

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import mobiledev.unb.ca.roompersistencetest.entity.Item
import mobiledev.unb.ca.roompersistencetest.ui.ItemsAdapter
import mobiledev.unb.ca.roompersistencetest.ui.MainActivityViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var itemsAdapter: ItemsAdapter
    private lateinit var mListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Add the listener events
        mListView = findViewById(R.id.listview)
        mListView.onItemLongClickListener =
            OnItemLongClickListener { _: AdapterView<*>, _: View, position: Int, _: Long ->
                val item: Item? = mainActivityViewModel.allItems.value?.get(position)
                if (item != null) {
                    deleteItem(item)
                }
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

        mainActivityViewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]

        mainActivityViewModel.allItems.observe(this) { items ->
            if (items != null) {
                itemsAdapter = ItemsAdapter(applicationContext, items)
                mListView.adapter = itemsAdapter
            }
            itemsAdapter.notifyDataSetChanged()
        }
    }

    private fun addItem(item: String, num: String) {
        mainActivityViewModel.insert(item, num.toInt())
    }

    private fun deleteItem(item: Item) {
        mainActivityViewModel.delete(item)
    }
}