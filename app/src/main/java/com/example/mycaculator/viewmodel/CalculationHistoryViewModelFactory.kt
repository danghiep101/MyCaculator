package com.example.mycaculator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mycaculator.data.CalculationHistoryDao

class CalculationHistoryViewModelFactory(private val dao: CalculationHistoryDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalculationHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalculationHistoryViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}