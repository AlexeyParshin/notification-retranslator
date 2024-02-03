package com.petp.bankapp.activities.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.petp.bankapp.R

class AppsAdapter(private val context: Context, private val dataSource: List<ApplicationInfo>, private val checkedApps: Set<String>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = inflater.inflate(R.layout.list_item_app, null, true)
        val textView = rowView.findViewById<TextView>(R.id.appName)
        val checkBox = rowView.findViewById<CheckBox>(R.id.appCheckBox)

        val app = getItem(position) as ApplicationInfo
        textView.text = app.loadLabel(context.packageManager).toString()
        checkBox.isChecked = checkedApps.contains(app.packageName)

        return rowView
    }
}