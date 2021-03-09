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

import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

class ReminderReceiver : BroadcastReceiver() {
    var fragment : Fragment? = null


    private lateinit var geofencingClient: GeofencingClient

    override fun onReceive(context: Context?, intent: Intent?) {
        val uid = intent?.getIntExtra("uid", 0)
        val message = intent?.getStringExtra("message")
        val geo = intent?.getBooleanExtra("geo", false)
        val lat = intent?.getFloatExtra("lat", 66F)
        val long = intent?.getFloatExtra("long", 22F)


        Log.d("REMREC", "RECIEVED")

        //Notify

        if(geo!!){

            Log.d("RECIEVER", "uid : ${uid}")
            geofencingClient = LocationServices.getGeofencingClient(context!!)
            val location = LatLng(lat!!.toDouble(), long!!.toDouble())
            ListFragment.createGeoFence(location, geofencingClient, message!!, context!!, uid!!)
        }
        else{
            ListFragment.showNotification(context!!, message!!)
        }


    }
}