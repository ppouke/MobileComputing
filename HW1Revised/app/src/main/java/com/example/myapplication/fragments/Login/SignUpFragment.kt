package com.example.myapplication.fragments.Login

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R



class SignUpFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_sign_up, container, false)

        val saveSignUp = view.findViewById<Button>(R.id.SaveSignUpBtn)

        saveSignUp.setOnClickListener{
            saveSignUp(view)

        }

        return view
    }


    private fun saveSignUp(view: View)
    {
        val userToSave = view.findViewById<EditText>(R.id.addUser_et).text.toString()
        val passwordToSave = view.findViewById<EditText>(R.id.addPassword_et).text.toString()
        //probably should encrypt this but it'll do for now

        if(inputCheck(userToSave, passwordToSave)){
            val prefs = activity?.getSharedPreferences(getString(R.string.SharedPreferences), Context.MODE_PRIVATE) ?:return
            with( prefs.edit()){
                putString(userToSave, passwordToSave)
                apply()
                Toast.makeText(requireContext(), "Account Made for ${userToSave}", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
            }

        }else {
            Toast.makeText(requireContext(), "Please Fill in all fields", Toast.LENGTH_SHORT).show()

        }




    }


    fun inputCheck(user : String, password : String):Boolean
    {
        return !(TextUtils.isEmpty(user) && !(TextUtils.isEmpty(password)))
    }

}