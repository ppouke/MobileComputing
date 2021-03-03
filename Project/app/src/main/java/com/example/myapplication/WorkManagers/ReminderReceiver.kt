package com.example.myapplication.WorkManagers

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.myapplication.R
import com.example.myapplication.fragments.list.ListAdapter
import com.example.myapplication.fragments.list.ListFragment

class ReminderReceiver : BroadcastReceiver() {
    var fragment : Fragment? = null
    override fun onReceive(context: Context?, intent: Intent?) {
        val uid = intent?.getIntExtra("uid", 0)
        val message = intent?.getStringExtra("message")

        Log.d("RECIEVER", "uid : ${uid}")

        //Notify
        ListFragment.showNotification(context!!, message!!)


        //update when in listfragment

        // Start intent
//        Handler(Looper.getMainLooper()).post({
//            val intent = Intent(applicationContext, ListFragment::class.java)
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            applicationContext.startActivity(intent)
//        })


    }
}