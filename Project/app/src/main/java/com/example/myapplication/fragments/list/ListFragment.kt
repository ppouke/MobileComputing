package com.example.myapplication.fragments.list

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.work.Data

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapplication.LoginActivity
import com.example.myapplication.ProfileActivity
import com.example.myapplication.R
import com.example.myapplication.ViewModel.ReminderViewModel
import com.example.myapplication.WorkManagers.GeofenceReceiver
import com.example.myapplication.WorkManagers.ReminderReceiver
import com.example.myapplication.WorkManagers.ReminderWorker
import com.example.myapplication.model.Reminder
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class ListFragment : Fragment() {


    private lateinit var mReminderViewModel: ReminderViewModel


    private lateinit var map: GoogleMap
    private val TAG = ListFragment::class.java.simpleName
    private val REQUEST_LOCATION_PERMISSION = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_list, container, false)



        //Recycler view

        var toggle = view.findViewById<SwitchCompat>(R.id.showAll)

        setRecyclerView(view, toggle.isChecked)

        toggle.setOnCheckedChangeListener { _, isChecked ->

            setRecyclerView(view, toggle.isChecked)

        }


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
        if(item.itemId == R.id.menu_refresh){
            setRecyclerView(requireView(), requireView().findViewById<SwitchCompat>(R.id.showAll).isChecked)
            updateMockLocation()
        }
        if(item.itemId == R.id.menu_map){
            findNavController().navigate(R.id.action_listFragment_to_selectLocFragment)
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

    fun setRecyclerView(view : View, showAll : Boolean){

        val adapter = ListAdapter()
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        mReminderViewModel = ViewModelProvider( this).get(ReminderViewModel::class.java)
        mReminderViewModel.readAllData.observe(viewLifecycleOwner, Observer { reminder ->

            val curTime = Calendar.getInstance().timeInMillis


            val prefs = activity?.getSharedPreferences(getString(R.string.SharedPreferences), Context.MODE_PRIVATE)
            val lat = prefs?.getFloat("Latitude", 65.08238F)!!.toDouble()
            val long = prefs?.getFloat("Longitude", 25.44262F)!!.toDouble()
            val curLoc = LatLng(lat, long)

            if(!showAll){

                var shownReminders = mutableListOf<Reminder>()
                for(r in reminder){
                    val setTime = r.reminder_time

                    val remLoc = LatLng(r.location_x.toDouble(), r.location_y.toDouble())

                    if(setTime - curTime <= 0){

                        if(checkIfInsideGeoFence(curLoc, remLoc) || r.reminder_seen){

                            shownReminders.add(r)
                        }
                    }
                }
                adapter.setData(shownReminders)
            }

            else{
                adapter.setData(reminder)
            }

        })

    }

    private fun checkIfInsideGeoFence(p0 : LatLng, p1 : LatLng) : Boolean{
        val dist = SphericalUtil.computeDistanceBetween(p0,p1)
        return dist <= 200
    }


    private fun updateMockLocation(){

        GlobalScope.launch {

            Log.d("LISTFRAG","UPDATING MOCK LOCATION 1")
            val prefs = activity?.getSharedPreferences(getString(R.string.SharedPreferences), Context.MODE_PRIVATE)
            val lat = prefs?.getFloat("Latitude", 65.08238F)!!.toDouble()
            val long = prefs?.getFloat("Longitude", 25.44262F)!!.toDouble()

            val location = Location("flp")
            location.latitude = lat
            location.longitude = long
            location.time = System.currentTimeMillis()
            location.accuracy = 3.0f
            location.elapsedRealtimeNanos = System.nanoTime()


            setMockLocation(location)

            delay(5000)

            Log.d("LISTFRAG","UPDATING MOCK LOCATION 2")

            setMockLocation(location)

            delay(5000)

            Log.d("LISTFRAG","UPDATING MOCK LOCATION 3")

            setMockLocation(location)


        }
    }


    private fun setMockLocation(location : Location){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                            requireContext(), android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(
                                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ),
                        ListFragment.GEOFENCE_LOCATION_REQUEST_CODE
                )
            } else {

                Log.d("MOCK", "MOCKSERVICE")
                LocationServices.getFusedLocationProviderClient(requireContext()).setMockMode(true)
                LocationServices.getFusedLocationProviderClient(requireContext()).setMockLocation(location)

            }
        } else {
            Log.d("MOCK", "MOCKSERVICE")
            LocationServices.getFusedLocationProviderClient(requireContext()).setMockMode(true)
            LocationServices.getFusedLocationProviderClient(requireContext()).setMockLocation(location)


        }

    }






    companion object{

        fun showNotification(context: Context, message: String){

            Log.d("NOTIFRAG", message)

            val CHANNEL_ID = "REMINDER_APP_NOTIFICATION_CHANNEL"
            var notificationId = Random.nextInt(10,1000)+5


            val intent = Intent(context, ListFragment::class.java)

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)



            var notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_wysiwyg_24)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setGroup(CHANNEL_ID)

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val channel = NotificationChannel(
                        CHANNEL_ID,
                        context.getString(R.string.app_name),
                        NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = context.getString(R.string.app_name)
                }
                notificationManager.createNotificationChannel(channel)
            }

            notificationManager.notify(notificationId, notificationBuilder.build())
        }


        fun setReminder(context: Context, uid : Int, timeInMillis: Long, message: String, latLng: LatLng, geo : Boolean){


            Log.d("ReminderFRAG", "SetReminder for ${message}")

            val intent = Intent(context, ReminderReceiver::class.java)
            intent.putExtra("uid", uid)
            intent.putExtra("message", message)
            intent.putExtra("geo", geo)
            intent.putExtra("lat", latLng.latitude.toFloat())
            intent.putExtra("long", latLng.longitude.toFloat())


            val pendingIntent = PendingIntent.getBroadcast(context, uid, intent, PendingIntent.FLAG_ONE_SHOT)

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(AlarmManager.RTC, timeInMillis, pendingIntent)

        }

        const val GEOFENCE_RADIUS = 200
        const val GEOFENCE_ID = "REMINDER_GEOFENCE_ID"
        const val GEOFENCE_EXPIRATION = 10 * 24 * 60 * 60 * 1000 // 10 days
        const val GEOFENCE_DWELL_DELAY =  10 * 1000 // 10 secs // 2 minutes
        const val GEOFENCE_LOCATION_REQUEST_CODE = 12345


        fun createGeoFence(location : LatLng, geoFencingClient: GeofencingClient, message: String, context: Context, uid: Int){


            val geofence = Geofence.Builder()
                .setRequestId(GEOFENCE_ID)
                .setCircularRegion(location.latitude, location.longitude, GEOFENCE_RADIUS.toFloat())
                .setExpirationDuration(GEOFENCE_EXPIRATION.toLong())
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(GEOFENCE_DWELL_DELAY)
                .build()

            val geofenceRequest = GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build()

            val intent = Intent(context, GeofenceReceiver::class.java)
                .putExtra("message", message)

            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (ContextCompat.checkSelfPermission(
                        context, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("GEOFENCING", "NO PERMISSION")
                } else {
                    geoFencingClient.addGeofences(geofenceRequest, pendingIntent).run {
                        addOnFailureListener{
                            Log.d("GEOFENCING","FAIL")

                        }
                        addOnSuccessListener {
                            Log.d("SUCCESS",this.isComplete.toString())
                        }
                    }
                    Log.d("GEOFENCING", "THROUGH")

                }
            } else { geoFencingClient.addGeofences(geofenceRequest, pendingIntent)

            }


        }

        fun setReminderWithWorkManager(context: Context, uid: Int, timeInMillis: Long, message: String){

            val reminderParameters = Data.Builder().putString("message", message).putInt("uid", uid).build()

            Log.d("LISTWORKER", "Setting work manager for ${uid}")

            //get min from now to reminder

            var minutesFromNow = 0L

            if(timeInMillis > System.currentTimeMillis())
                minutesFromNow = timeInMillis - System.currentTimeMillis()

            val reminderRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                    .setInputData(reminderParameters)
                    .setInitialDelay(minutesFromNow, TimeUnit.MILLISECONDS)
                    .build()

            WorkManager.getInstance(context).enqueue(reminderRequest)
        }


        fun cancelReminder(context: Context,  pendingIntentId : Int){
            val intent = Intent(context, ReminderReceiver::class.java)

            Toast.makeText(context, "Removed notification with id: ${pendingIntentId}",Toast.LENGTH_LONG).show()

            val pendingIntent = PendingIntent.getBroadcast(context, pendingIntentId, intent, PendingIntent.FLAG_ONE_SHOT )

            var alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            alarmManager.cancel(pendingIntent)
        }


    }


}

