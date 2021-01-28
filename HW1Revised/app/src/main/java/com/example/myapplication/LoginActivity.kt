package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupActionBarWithNavController(findNavController(R.id.fragemnt_login))
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragemnt_login)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}