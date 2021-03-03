package com.example.myapplication.WorkManagers

import android.content.Context
import android.content.Intent
import android.os.Looper
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.myapplication.fragments.list.ListFragment
import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.myapplication.R

class ReminderWorker(appContext : Context, workerParameters: WorkerParameters) : Worker(appContext, workerParameters) {
    override fun doWork(): Result {

        val text = inputData.getString("message")
        ListFragment.showNotification(applicationContext, text!!)


        // Start intent
//        Handler(Looper.getMainLooper()).post({
//            val intent = Intent(applicationContext, ListFragment::class.java)
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            applicationContext.startActivity(intent)
//        })

        return Result.success()
    }


}