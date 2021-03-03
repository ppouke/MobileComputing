package com.example.myapplication.fragments.list

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.work.Data

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapplication.LoginActivity
import com.example.myapplication.ProfileActivity
import com.example.myapplication.R
import com.example.myapplication.ViewModel.ReminderViewModel
import com.example.myapplication.WorkManagers.ReminderReceiver
import com.example.myapplication.WorkManagers.ReminderWorker
import com.example.myapplication.model.Reminder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class ListFragment : Fragment() {


    private lateinit var mReminderViewModel: ReminderViewModel

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

            if(!showAll){

                var shownReminders = mutableListOf<Reminder>()
                for(r in reminder){
                    val setTime = r.reminder_time

                    if(setTime - curTime <= 0){
                        shownReminders.add(r)
                    }
                }
                adapter.setData(shownReminders)
            }

            else{
                adapter.setData(reminder)
            }

        })

    }


    fun refresh(){

        val view = requireView()

        setRecyclerView(view, view.findViewById<SwitchCompat>(R.id.showAll).isChecked)

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


        fun setReminder(context: Context, uid : Int, timeInMillis: Long, message: String){


            Log.d("ReminderFRAG", "SetReminder for ${message}")

            val intent = Intent(context, ReminderReceiver::class.java)
            intent.putExtra("uid", uid)
            intent.putExtra("message", message)

            val pendingIntent = PendingIntent.getBroadcast(context, uid, intent, PendingIntent.FLAG_ONE_SHOT)

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(AlarmManager.RTC, timeInMillis, pendingIntent)

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

