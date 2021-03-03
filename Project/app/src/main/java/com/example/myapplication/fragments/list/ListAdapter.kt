package com.example.myapplication.fragments.list

import android.content.ContentProvider
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.fragments.add.addFragment
import com.example.myapplication.model.Reminder
import kotlinx.android.synthetic.main.custom_row.view.*
import java.lang.Exception
import java.util.*

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


        holder.itemView.findViewById<TextView>(R.id.reminder_txt).text = currentItem.message
        holder.itemView.findViewById<TextView>(R.id.id_txt).text = currentItem.id.toString()







        // show image if available
        var URI = Uri.parse(currentItem.URI)

        if(URI != null){

            Glide.with(holder.itemView.context)
                    .load(URI)
                    .into(holder.itemView.listImage)

        }
        else{
            holder.itemView.findViewById<ImageView>(R.id.listImage).isVisible = false
        }

        holder.itemView.rowLayout.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToUpdateFragment(currentItem)
            holder.itemView.findNavController().navigate(action)
        }

    }

    fun setData(reminder: List<Reminder>)
    {
        this.reminderList = reminder
        notifyDataSetChanged()

    }




}