package com.example.myapplication.fragments.add

import android.Manifest
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.text.TextUtils
import android.text.format.DateFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.example.myapplication.R
import com.example.myapplication.model.Reminder
import com.example.myapplication.ViewModel.ReminderViewModel
import com.example.myapplication.db.AppDatabase
import com.example.myapplication.fragments.list.ListFragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_add.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


const val GEOFENCE_RADIUS = 200
const val GEOFENCE_ID = "REMINDER_GEOFENCE_ID"
const val GEOFENCE_EXPIRATION = 10 * 24 * 60 * 60 * 1000 // 10 days
const val GEOFENCE_DWELL_DELAY =  10 * 1000 // 10 secs // 2 minutes
const val GEOFENCE_LOCATION_REQUEST_CODE = 12345
const val CAMERA_ZOOM_LEVEL = 15f
const val LOCATION_REQUEST_CODE = 123


class addFragment : Fragment(), TimePickerDialog.OnTimeSetListener, OnMapReadyCallback {

    private lateinit var mReminderViewModel : ReminderViewModel


    private var stringURI : String? = null

    private var timeSet : Calendar = Calendar.getInstance()

    private lateinit var mMap : GoogleMap

    var latitude : Float = 66F
    var longitude : Float = 25F

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var geofencingClient: GeofencingClient


    val requestImageRequestCode = 111
    val recordAudioRequestCode = 100
    val recordLocationRequestCode = 122

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        mReminderViewModel = ViewModelProvider(this).get(ReminderViewModel::class.java)





        val button = view.findViewById<Button>(R.id.button)
        button.setOnClickListener{
            insertDataToDatabase(view)
        }

        val ciButton = view.findViewById<Button>(R.id.loadImage)
        ciButton.setOnClickListener {
            pickImage()
        }

        //initialize microphone

        if(ActivityCompat.checkSelfPermission(view.context, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkMicPermission()
        }
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())




        view.micButtonAdd.setOnClickListener {
            startActivityForResult(speechRecognizerIntent, recordAudioRequestCode)
        }


        view.setTimer.setOnClickListener {
            pickTime()
        }



        //init map

        if(ActivityCompat.checkSelfPermission(view.context, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED){
            checkMapPermission()
        }
        else{
            Log.d("ADDFRAG", "GOT LOC PERMISSION")
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.selectMap) as SupportMapFragment
        mapFragment.getMapAsync(this)


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        geofencingClient = LocationServices.getGeofencingClient(requireContext())


        return view
    }


    private fun insertDataToDatabase(view : View){

        val reminderText = view.findViewById<EditText>(R.id.addReminder_et).text.toString()

        var uri = "default"

        if(stringURI != null){

            uri = stringURI as String
        }


        //time now
        val createTime = Calendar.getInstance()


        //map
        val latLng = LatLng(latitude.toDouble(), longitude.toDouble())

        if (inputCheck(reminderText)) {
            val seen = !view.mapToggle.isChecked // map toggle is not checked -> show even if not in geofence
            val reminder = Reminder(0,reminderText, latitude, longitude, timeSet.timeInMillis, createTime.timeInMillis, "user", seen, uri)
            //add data to database


            mReminderViewModel.addReminder(reminder)

            GlobalScope.launch {

                val db = Room.databaseBuilder(
                        requireContext(),
                        AppDatabase::class.java,
                        "com.example.myapplication.database").build()
                val uuid = db.reminderDao().addReminder(reminder).toInt()
                db.close()
                Log.d("FRAGID", "${uuid}")


                // Make notification

                if((timeSet.timeInMillis > createTime.timeInMillis && view.toggleTime.isChecked))
                {
                    if(view.mapToggle.isChecked)
                    {

                        ListFragment.setReminder(requireContext(),uuid, timeSet.timeInMillis, reminderText,latLng, true)
                    }
                    else{

                        ListFragment.setReminder(requireContext(),uuid, timeSet.timeInMillis, reminderText,latLng, false)
                    }

                }

                else if(view.mapToggle.isChecked){

                    ListFragment.createGeoFence(latLng, geofencingClient, reminderText, requireContext(), uuid)
                }

            }



            Toast.makeText( requireContext(),"Added Reminder for ${timeSet.time}", Toast.LENGTH_LONG).show()
            //navigate back
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        }else{
            Toast.makeText(requireContext(),"Please fill all text fields.", Toast.LENGTH_LONG).show()
        }



    }

    private fun inputCheck(ReminderT :String): Boolean
    {
        return !(TextUtils.isEmpty(ReminderT))
    }

    private fun pickImage(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, 111)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            requestImageRequestCode -> {
                if (resultCode == Activity.RESULT_OK && data != null){
                    val contentURI = data!!.data
                    stringURI = contentURI.toString()
                    imagePreview.setImageURI(contentURI)

                }
            }

            recordAudioRequestCode -> {
                if(resultCode == Activity.RESULT_OK && data != null){
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    addReminder_et.setText(result?.get(0))

                }
            }
        }

    }


    private fun checkMicPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.RECORD_AUDIO), recordAudioRequestCode)
        }
    }


    private fun checkMapPermission() {
        Log.d("ADDFRAG", "REQUESTING PERMISSION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
           ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION), recordLocationRequestCode)
        }

    }

    private fun pickTime(){

        var c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        TimePickerDialog(requireActivity(), this, hour, minute, DateFormat.is24HourFormat(requireActivity())).show()

    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {

        var c = Calendar.getInstance()

        c.set(
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH),
                hourOfDay,
                minute
        )

        setTimer.setText("Set To ${hourOfDay}:${minute}")
        timeSet = c

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        (childFragmentManager.findFragmentById(R.id.selectMap) as? CustomMapFragment)?.let {
            it.listener = object : CustomMapFragment.OnTouchListener {
                override fun onTouch() {

                    val mScrollView = view!!.findViewById<ScrollView>(R.id.addScrollView)
                    mScrollView.requestDisallowInterceptTouchEvent(true)
                }
            }
        }






        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            if(it != null){
                with(mMap){
                    val latLng = LatLng(it.latitude, it.longitude)
                    moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, CAMERA_ZOOM_LEVEL))
                    Log.d("LOCATION", "SET")

                }
            }
            else{
                with(mMap){
                    moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(65.toDouble(), 25.toDouble()), CAMERA_ZOOM_LEVEL))
                    Log.d("LOCATION", "DEF")
                }
            }
        }


        setMapLongClick(mMap)
    }



    fun setMapLongClick(map : GoogleMap){

        map.setOnMapLongClickListener {
            val snippet = String.format(
                    Locale.getDefault(),
                    "Lat: %1$.5f, Lng: %2$.5f",
                    it.latitude,
                    it.longitude)

            map.clear()
            map.addMarker(
                    MarkerOptions()
                            .position(it)
                            .title("Set Location")
                            .snippet(snippet)
            ).showInfoWindow()
            map.addCircle(
                    CircleOptions()
                            .center(it)
                            .strokeColor(Color.argb(50, 70, 70, 70))
                            .fillColor(Color.argb(70, 150, 150, 150))
                            .radius(GEOFENCE_RADIUS.toDouble())
            )

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(it, CAMERA_ZOOM_LEVEL))
            latitude = it.latitude.toFloat()
            longitude = it.longitude.toFloat()

        }

    }





}