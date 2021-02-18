package com.example.myapplication.fragments.update

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.TextUtils
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.ViewModel.ReminderViewModel
import com.example.myapplication.model.Reminder
import kotlinx.android.synthetic.main.custom_row.view.*
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_update.*
import kotlinx.android.synthetic.main.fragment_update.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import java.util.jar.Manifest

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class UpdateFragment : Fragment() {

    private val args by navArgs<UpdateFragmentArgs>()

    private lateinit var mReminderViewModel: ReminderViewModel

    private var stringURI : String? = null




    val recordAudioRequestCode = 100
    val requestImageRequestCode = 111


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_update, container, false)

        mReminderViewModel = ViewModelProvider(this).get(ReminderViewModel::class.java)




        //set correct text and image
        view.findViewById<EditText>(R.id.UpdateReminder_et).setText(args.currentReminder.message)

        var URI = Uri.parse(args.currentReminder.URI)

        if(URI != null){

            Glide.with(this)
                .load(URI)
                .into(view.imageUPreview)

        }

        val updateBtn = view.findViewById<Button>(R.id.UpdateButton)
        updateBtn.setOnClickListener{
            updateItem(view)
        }

        val ciButton = view.findViewById<Button>(R.id.loadUImage)
        ciButton.setOnClickListener {
            Glide.with(this).clear(view.imageUPreview)
            pickImage()
        }


        //initialize microphone

        if(ActivityCompat.checkSelfPermission(view.context, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkMicPermission()
        }
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())




        view.micButton.setOnClickListener {
            startActivityForResult(speechRecognizerIntent, recordAudioRequestCode)


        }

        //Add menu
        setHasOptionsMenu(true)


        return view
    }

    private fun updateItem(view: View){

        val reminder = view.findViewById<EditText>(R.id.UpdateReminder_et).text.toString()

        val current = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        val formatted = current.format(formatter)

        val prefs = activity?.getSharedPreferences(getString(R.string.SharedPreferences), Context.MODE_PRIVATE)?: return

        val curUser = prefs.getString(getString(R.string.CurrentUser),null)


        var uri = args.currentReminder.URI

        if(stringURI != null){
            uri = stringURI as String
        }

        if(inputCheck((reminder)))
        {
            //Create Reminder Object
            val updatedReminder = Reminder(args.currentReminder.id, reminder, "locX", "locY", 0f, formatted.toString(), curUser.toString(), false, uri)
            //Update Reminder Object
            mReminderViewModel.updateReminder(updatedReminder)
            Toast.makeText(requireContext(),"Updated Reminder!", Toast.LENGTH_SHORT).show()
            //Navigate back
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)

        }else{
            Toast.makeText(requireContext(),"Please fill out fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun inputCheck(ReminderT :String): Boolean
    {
        return !(TextUtils.isEmpty(ReminderT))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_delete){
            deleteReminder()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteReminder()
    {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            mReminderViewModel.deleteReminder(args.currentReminder)
            Toast.makeText(requireContext(), "Removed ${args.currentReminder.message}", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }
        builder.setNegativeButton("No"){ _, _ ->}
        builder.setTitle("Delete ${args.currentReminder.message}?")
        builder.setMessage("Are you sure you want to delete ${args.currentReminder.message}? ")
        builder.create().show()
    }


    private fun pickImage(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, requestImageRequestCode)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            requestImageRequestCode -> {
                if (resultCode == Activity.RESULT_OK && data != null){
                    val contentURI = data!!.data
                    stringURI = contentURI.toString()
                    imageUPreview.setImageURI(contentURI)

                }
            }

            recordAudioRequestCode -> {
                if(resultCode == Activity.RESULT_OK && data != null){
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    UpdateReminder_et.setText(result?.get(0))

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