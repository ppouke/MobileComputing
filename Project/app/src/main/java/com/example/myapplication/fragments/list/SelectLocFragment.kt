package com.example.myapplication.fragments.list

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_select_loc.*
import kotlinx.android.synthetic.main.fragment_select_loc.view.*
import java.util.*

class SelectLocFragment : Fragment() {



    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val prefs = activity?.getSharedPreferences(
            getString(R.string.SharedPreferences),
            Context.MODE_PRIVATE
        )
        val lat = prefs?.getFloat("Latitude", 65.08238F)!!.toDouble()
        val long = prefs?.getFloat("Longitude", 25.44262F)!!.toDouble()


        val snippet = String.format(
            Locale.getDefault(),
            "Lat: %1$.5f, Lng: %2$.5f",
            lat,
            long
        )
        val position = LatLng(lat, long)

        googleMap.addMarker(
            MarkerOptions().position(position).title("My Position").snippet(snippet)
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15F))

        setMapLongClick(googleMap)


        
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_select_loc, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        LocationServices.getFusedLocationProviderClient(requireContext()).flushLocations()
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)





    }


    private fun setMapLongClick(map: GoogleMap){

        map.setOnMapLongClickListener {
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Lng: %2$.5f",
                it.latitude,
                it.longitude
            )

            map.clear()

            map.addMarker(
                MarkerOptions()
                    .position(it)
                    .title("My Position")
                    .snippet(snippet)
            )


            val prefs = activity?.getSharedPreferences(
                getString(R.string.SharedPreferences),
                Context.MODE_PRIVATE
            )
            with(prefs!!.edit()){

                putFloat("Latitude", it.latitude.toFloat())
                putFloat("Longitude", it.longitude.toFloat())
                apply()
            }


            val location = Location("flp")
            location.latitude = it.latitude
            location.longitude = it.longitude
            location.time = System.currentTimeMillis()
            location.accuracy = 3.0f
            location.elapsedRealtimeNanos = System.nanoTime()

            SetMockLocation(location)



        }
    }

    private fun SetMockLocation(location: Location){

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
                LocationServices.getFusedLocationProviderClient(requireContext()).setMockMode(!TrueLoc.isChecked)
                LocationServices.getFusedLocationProviderClient(requireContext()).setMockLocation(location)
                LocationServices.getFusedLocationProviderClient(requireContext())
            }
        } else {
            Log.d("MOCK", "MOCKSERVICE")
            LocationServices.getFusedLocationProviderClient(requireContext()).setMockMode(!TrueLoc.isChecked)
            LocationServices.getFusedLocationProviderClient(requireContext()).setMockLocation(location)
        }

    }
}