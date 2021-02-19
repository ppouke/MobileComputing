package com.example.myapplication.fragments.Login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.math.MathUtils
import androidx.navigation.fragment.findNavController
import com.example.myapplication.MainActivity
import com.example.myapplication.R


class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_login, container, false)



        val suBtn = view.findViewById<Button>(R.id.SignUpBtn)
        suBtn.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        val loginBtn = view.findViewById<Button>(R.id.LoginBtn)
        loginBtn.setOnClickListener{
            //Authenticate and save login Status

            if(authenticate(view)){
                val prefs = activity?.getSharedPreferences(getString(R.string.SharedPreferences),Context.MODE_PRIVATE)
                with (prefs?.edit())
                {
                    (this?.putInt(getString(R.string.LoginStatus), 1))
                    this?.apply()
                }
                Toast.makeText(requireContext(), "Logged In!",Toast.LENGTH_SHORT).show()
                startActivity(Intent(activity, MainActivity::class.java))
            }else{
                Toast.makeText(requireContext(), "Invalid Password", Toast.LENGTH_SHORT).show()
            }

        }

        return view
    }

    override fun onResume(){
        super.onResume()
        checkLoginStatus()
    }

    private fun checkLoginStatus(){
        val prefs = activity?.getSharedPreferences(getString(R.string.SharedPreferences), Context.MODE_PRIVATE) ?: return
        val loginStatus = prefs.getInt(getString(R.string.LoginStatus),0)
        if(loginStatus == 1)
        {
            Toast.makeText(requireContext(), "Logged In!",Toast.LENGTH_SHORT).show()
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)

        }
    }


    private fun authenticate(view: View):Boolean{
        val userId = view.findViewById<EditText>(R.id.user_et).text.toString()
        val password = view.findViewById<EditText>(R.id.password_et).text.toString()

        val prefs = activity?.getSharedPreferences(getString(R.string.SharedPreferences),Context.MODE_PRIVATE) ?: return false

        val foundPass = prefs.getString(userId, null)
        prefs.edit().putString(getString(R.string.CurrentUser),userId).apply()

        return if(!(TextUtils.isEmpty(foundPass))){

            foundPass == password
        }else{
            Toast.makeText(requireContext(), "No such user in the database", Toast.LENGTH_LONG).show()
            false

        }

    }




}