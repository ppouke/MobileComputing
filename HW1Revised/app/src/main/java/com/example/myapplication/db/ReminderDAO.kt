package com.example.myapplication.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.myapplication.model.Reminder

@Dao
interface ReminderDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addReminder(reminder: Reminder)

    @Update
    suspend fun updateReminder(reminder: Reminder)

    @Query("SELECT * FROM reminder_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<Reminder>>


    @Query("DELETE FROM reminder_table WHERE id = :delid")
    fun delete(delid: Int)




}