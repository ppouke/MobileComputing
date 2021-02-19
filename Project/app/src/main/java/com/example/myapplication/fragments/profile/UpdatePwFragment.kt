package com.example.myapplication.fragments.profile

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R

class UpdatePwFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_update_pw, container, false)

        val saveNewPassBtn = view.findViewById<Button>(R.id.saveNewPwBtn)
        saveNewPassBtn.setOnClickListener {
            saveNewPass(view)
        }

        return view
    }

    private fun saveNewPass(view: View)
    {
        val oldPass = view.findViewById<EditText>(R.id.oldPw_et).text.toString()
        val newPass = view.findViewById<EditText>(R.id.newPw_et).text.toString()
        val NewPassAg = view.findViewById<EditText>(R.id.newPwAgain_et).text.toString()

        val prefs = activity?.getSharedPreferences(getString(R.string.SharedPreferences), Context.MODE_PRIVATE)?: return

        val curUser = prefs.getString(getString(R.string.CurrentUser),null)
        val trueOldPass = prefs.getString(curUser, null)

        if(trueOldPass == oldPass){

            if(newPass == NewPassAg){

                prefs.edit().putString(curUser, newPass).commit()
                Toast.makeText(requireContext(), "Updated Password for $curUser", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_updatePwFragment_to_profileFragment)

            }else{
                Toast.makeText(requireContext(), "new passwords do not match!", Toast.LENGTH_LONG).show()
            }

        }else{
            Toast.makeText(requireContext(),"Old password is incorrect!", Toast.LENGTH_LONG).show()
        }



    }

}