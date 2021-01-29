package com.example.myapplication.fragments.list

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.LoginActivity
import com.example.myapplication.ProfileActivity
import com.example.myapplication.R
import com.example.myapplication.ViewModel.ReminderViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ListFragment : Fragment() {

    private lateinit var mReminderViewModel: ReminderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_list, container, false)


        //Recycler view
        val adapter = ListAdapter()
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        mReminderViewModel = ViewModelProvider( this).get(ReminderViewModel::class.java)
        mReminderViewModel.readAllData.observe(viewLifecycleOwner, Observer { reminder ->
            adapter.setData(reminder)
        })

        val fab = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        fab.setOnClickListener{
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }

        val logoutFab = view.findViewById<FloatingActionButton>(R.id.LogoutButton)
        logoutFab.setOnClickListener{
            val prefs = activity?.getSharedPreferences(getString(R.string.SharedPreferences), Context.MODE_PRIVATE)
            with(prefs?.edit()){
                this?.putInt(getString(R.string.LoginStatus), 0)
                this?.apply()
            }

            Toast.makeText(requireContext(),"Logged Out!",Toast.LENGTH_SHORT).show()
            startActivity(Intent(activity, LoginActivity::class.java))
        }

        //Add Menu
        setHasOptionsMenu(true)

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_delete)
        {
            deleteAllUsers()
        }
        if(item.itemId == R.id.menu_profile){
            startActivity(Intent(activity, ProfileActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteAllUsers()
    {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            mReminderViewModel.deleteAllReminders()
            Toast.makeText(requireContext(), "Removed All reminders", Toast.LENGTH_SHORT).show()

        }
        builder.setNegativeButton("No"){ _, _ ->}
        builder.setTitle("Delete All Reminders ?")
        builder.setMessage("Are you sure you want to delete All reminders? ")
        builder.create().show()
    }

}