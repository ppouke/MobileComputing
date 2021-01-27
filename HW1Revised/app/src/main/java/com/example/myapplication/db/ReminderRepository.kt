package com.example.myapplication.db
import androidx.lifecycle.LiveData
class ReminderRepository(private val reminderDAO: ReminderDAO) {

    val readAllData: LiveData<List<Reminder>> = reminderDAO.readAllData()

    suspend fun addReminder(reminder: Reminder){
        reminderDAO.addReminder(reminder)
    }

}