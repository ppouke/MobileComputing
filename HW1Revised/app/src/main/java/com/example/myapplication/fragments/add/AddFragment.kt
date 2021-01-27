package com.example.myapplication.fragments.add

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.db.Reminder
import com.example.myapplication.db.ReminderViewModel


class addFragment : Fragment() {

    private lateinit var mReminderViewModel : ReminderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        mReminderViewModel = ViewModelProvider(this).get(ReminderViewModel::class.java)

        val button = view.findViewById<Button>(R.id.button)
        button.setOnClickListener{
            insertDataToDatabase(view)
        }


        return view
    }


    private fun insertDataToDatabase(view : View){

        val reminderText = view.findViewById<EditText>(R.id.addReminder_et).text.toString()

        if (inputCheck(reminderText)) {
            val reminder = Reminder(0,reminderText)
            //add data to database
            mReminderViewModel.addReminder(reminder)
            Toast.makeText( requireContext(),"Added Reminder!", Toast.LENGTH_LONG).show()
            //navigate back
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        }else{
            Toast.makeText(requireContext(),"Please fill all text fields.", Toast.LENGTH_LONG).show()
        }

    }

    private fun inputCheck(ReminderT :String): Boolean
    {
        return !(TextUtils.isEmpty(ReminderT))
    }


}