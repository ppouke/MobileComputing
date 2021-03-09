package com.example.myapplication.WorkManagers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import android.util.Log

import com.example.myapplication.fragments.list.ListFragment
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceReceiver : BroadcastReceiver() {

    lateinit var text : String
    override fun onReceive(context: Context?, intent: Intent?) {

        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e("GEOREC", errorMessage)
            return
        }


        if(context != null){


            val geofencingTransition = geofencingEvent.geofenceTransition

            if (
                geofencingTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofencingTransition == Geofence.GEOFENCE_TRANSITION_DWELL
            )
            {
                if(intent != null)
                    text = intent.getStringExtra("message")!!
                    Log.d("GEOREC", text)
                    ListFragment.showNotification(context, text)
            }
        }
    }
}