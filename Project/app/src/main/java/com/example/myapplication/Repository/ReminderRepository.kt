package com.example.myapplication.Repository
import androidx.lifecycle.LiveData
import com.example.myapplication.db.ReminderDAO
import com.example.myapplication.model.Reminder

class ReminderRepository(private val reminderDAO: ReminderDAO) {

    val readAllData: LiveData<List<Reminder>> = reminderDAO.readAllData()

    suspend fun addReminder(reminder: Reminder){
        reminderDAO.addReminder(reminder)
    }

    suspend fun updateReminder(reminder: Reminder)
    {
       reminderDAO.updateReminder(reminder)
    }

    suspend fun deleteReminder(reminder: Reminder)
    {
        reminderDAO.deleteReminder(reminder)
    }

    suspend fun deleteAllReminders()
    {
        reminderDAO.deleteAllReminders()
    }

}