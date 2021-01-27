package com.example.myapplication.fragments.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.db.Reminder

class ListAdapter :RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var reminderList = emptyList<Reminder>()

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_row,parent,false))
    }

    override fun getItemCount(): Int {
        return reminderList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = reminderList[position]
        holder.itemView.findViewById<TextView>(R.id.id_txt).text = currentItem.id.toString()
        holder.itemView.findViewById<TextView>(R.id.reminder_txt).text = currentItem.reminder.toString()

    }

    fun setData(reminder: List<Reminder>)
    {
        this.reminderList = reminder
        notifyDataSetChanged()

    }
}