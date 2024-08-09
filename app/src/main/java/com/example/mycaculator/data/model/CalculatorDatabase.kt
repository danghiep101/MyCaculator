package com.example.mycaculator.data.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mycaculator.data.CalculationHistoryDao

@Database(entities = [CalculationHistory::class], version = 1, exportSchema = false)
abstract class CalculatorDatabase : RoomDatabase(){
    abstract fun calculationHistoryDao(): CalculationHistoryDao

    companion object{
        @Volatile
        private var INTANCE: CalculatorDatabase? = null


        fun getDatabase(context: Context): CalculatorDatabase {
            return INTANCE ?: synchronized(this){
                val intance = Room.databaseBuilder(
                    context.applicationContext,
                    CalculatorDatabase::class.java,
                    "caculator_database"
                ).build()
                INTANCE = intance
                intance
            }
        }
    }
}