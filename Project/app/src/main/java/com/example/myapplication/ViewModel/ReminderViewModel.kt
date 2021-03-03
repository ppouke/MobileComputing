package com.example.myapplication.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.db.AppDatabase
import com.example.myapplication.Repository.ReminderRepository
import com.example.myapplication.model.Reminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    val readAllData : LiveData<List<Reminder>>
    val readLast : LiveData<Reminder>
    private val repository: ReminderRepository
    init
    {
        val reminderDAO =  AppDatabase.getDatabase(application).reminderDao()
        repository = ReminderRepository(reminderDAO)
        readAllData = repository.readAllData
        readLast = repository.readLast
    }

    fun addReminder(reminder: Reminder)
    {
        viewModelScope.launch(Dispatchers.IO){
            repository.addReminder(reminder)
        }
    }

    fun updateReminder(reminder: Reminder){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateReminder(reminder)
        }
    }

    fun deleteReminder(reminder: Reminder)
    {
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteReminder(reminder)
        }
    }

    fun deleteAllReminders()
    {
        viewModelScope.launch(Dispatchers.IO)
        {
            repository.deleteAllReminders()
        }
    }
}