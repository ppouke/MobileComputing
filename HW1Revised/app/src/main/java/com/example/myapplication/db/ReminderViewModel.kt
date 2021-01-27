package com.example.myapplication.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    val readAllData : LiveData<List<Reminder>>
    private val repository: ReminderRepository
    init
    {
        val reminderDAO =  AppDatabase.getDatabase(application).reminderDao()
        repository = ReminderRepository(reminderDAO)
        readAllData = repository.readAllData
    }

    fun addReminder(reminder: Reminder)
    {
        viewModelScope.launch(Dispatchers.IO){
            repository.addReminder(reminder)
        }
    }
}