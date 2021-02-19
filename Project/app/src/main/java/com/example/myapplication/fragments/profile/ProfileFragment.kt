package com.example.myapplication.fragments.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.myapplication.MainActivity
import com.example.myapplication.R


class ProfileFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_profile, container, false)


        updateProfileText(view)
        


        val changePasswordBtn = view.findViewById<Button>(R.id.changePwBtn)
        changePasswordBtn.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_updatePwFragment)
        }


        val saveProfBtn = view.findViewById<Button>(R.id.saveProfBtn)
        saveProfBtn.setOnClickListener {
            saveProfile(view)
        }




        return view
    }


    private fun updateProfileText(view: View)
    {
        val prefs = activity?.getSharedPreferences(getString(R.string.SharedPreferences), Context.MODE_PRIVATE) ?: return
        val prefFirstName = prefs.getString("FirstName", "First Name")
        val prefLastName = prefs.getString("LastName", "Last Name")

        view.findViewById<EditText>(R.id.firstName_et).setText(prefFirstName)
        view.findViewById<EditText>(R.id.lastName_et).setText(prefLastName)

    }


    private fun saveProfile(view: View)
    {
        val firstName = view.findViewById<EditText>(R.id.firstName_et).text.toString()
        val lastName = view.findViewById<EditText>(R.id.lastName_et).text.toString()

        val prefs = activity?.getSharedPreferences(getString(R.string.SharedPreferences), Context.MODE_PRIVATE) ?: return
        with(prefs.edit())
        {
            putString("FirstName", firstName)
            putString("LastName", lastName)
            commit()
        }
        Toast.makeText(requireContext(), "Saved Profile!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(activity, MainActivity::class.java))
    }



}