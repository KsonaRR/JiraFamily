package com.example.jirafamily.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.jirafamily.R

class PrioritySpinnerAdapter(context: Context, private val priorities: Array<String>, private val images: IntArray) :
    ArrayAdapter<String>(context, R.layout.spinner_item, priorities) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    private fun createItemView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false)

        val imageView = itemView.findViewById<ImageView>(R.id.spinner_image)
        val textView = itemView.findViewById<TextView>(R.id.spinner_text)

        imageView.setImageResource(images[position])
        textView.text = priorities[position]

        return itemView
    }
}