package com.example.myapplication.fragments.update

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
import androidx.navigation.fragment.navArgs
import com.example.myapplication.R
import com.example.myapplication.ViewModel.ReminderViewModel
import com.example.myapplication.model.Reminder

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class UpdateFragment : Fragment() {

    private val args by navArgs<UpdateFragmentArgs>()

    private lateinit var mReminderViewModel: ReminderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_update, container, false)

        mReminderViewModel = ViewModelProvider(this).get(ReminderViewModel::class.java)

        view.findViewById<EditText>(R.id.UpdateReminder_et).setText(args.currentReminder.reminder)

        val updateBtn = view.findViewById<Button>(R.id.UpdateButton)
        updateBtn.setOnClickListener{
            updateItem(view)
        }


        return view
    }

    private fun updateItem(view: View){

        val reminder = view.findViewById<EditText>(R.id.UpdateReminder_et).text.toString()

        if(inputCheck((reminder)))
        {
            //Create Reminder Object
            val updatedReminder = Reminder(args.currentReminder.id, reminder)
            //Update Reminder Object
            mReminderViewModel.updateReminder(updatedReminder)
            Toast.makeText(requireContext(),"Updated Reminder!", Toast.LENGTH_SHORT).show()
            //Navigate back
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)

        }else{
            Toast.makeText(requireContext(),"Please fill out fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun inputCheck(ReminderT :String): Boolean
    {
        return !(TextUtils.isEmpty(ReminderT))
    }

}