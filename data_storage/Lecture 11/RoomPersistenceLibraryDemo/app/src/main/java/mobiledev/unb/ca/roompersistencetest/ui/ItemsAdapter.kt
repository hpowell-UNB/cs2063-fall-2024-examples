package mobiledev.unb.ca.roompersistencetest.ui

import android.content.Context
import android.widget.ArrayAdapter
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import mobiledev.unb.ca.roompersistencetest.R
import android.widget.TextView
import mobiledev.unb.ca.roompersistencetest.entity.Item

class ItemsAdapter(context: Context, items: List<Item>) : ArrayAdapter<Item>(
    context, 0, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get the data item for this position
        var view = convertView
        val item = getItem(position)

        // Check if an existing view is being reused, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_layout, parent, false)
        }

        // Lookup view for data population
        val tvName = view!!.findViewById<TextView>(R.id.item_textview)
        val tvNum = view.findViewById<TextView>(R.id.num_textview)

        // Populate the data into the template view using the data object
        tvName.text = item!!.name
        tvNum.text = item.num.toString()

        // Return the completed view to render on screen
        return view
    }
}
