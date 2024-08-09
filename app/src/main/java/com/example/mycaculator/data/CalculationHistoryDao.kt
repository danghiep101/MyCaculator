package com.example.mycaculator.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mycaculator.data.model.CalculationHistory

@Dao
interface CalculationHistoryDao {
    @Insert
    suspend fun insert(history: CalculationHistory)

    @Query("SELECT * FROM calculation_history ORDER BY  id DESC")
    fun  getAllHistory(): LiveData<List<CalculationHistory>>
}