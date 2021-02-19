package com.example.myapplication.fragments.add

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.model.Reminder
import com.example.myapplication.ViewModel.ReminderViewModel
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_add.view.*
import kotlinx.android.synthetic.main.fragment_update.*
import kotlinx.android.synthetic.main.fragment_update.view.*
import kotlinx.android.synthetic.main.fragment_update.view.micButton
import java.util.*
import kotlin.reflect.typeOf


class addFragment : Fragment() {

    private lateinit var mReminderViewModel : ReminderViewModel


    private var stringURI : String? = null

    val requestImageRequestCode = 111
    val recordAudioRequestCode = 100

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



        return view
    }


    private fun insertDataToDatabase(view : View){

        val reminderText = view.findViewById<EditText>(R.id.addReminder_et).text.toString()

        var uri = "default"

        if(stringURI != null){

            uri = stringURI as String
        }


        if (inputCheck(reminderText)) {
            val reminder = Reminder(0,reminderText, "locX", "locY", 0f, "Today", "user", false, uri)
            //add data to database
            mReminderViewModel.addReminder(reminder)
            Toast.makeText( requireContext(),"Added Reminder!", Toast.LENGTH_LONG).show()
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.RECORD_AUDIO), recordAudioRequestCode)
        }
    }


}