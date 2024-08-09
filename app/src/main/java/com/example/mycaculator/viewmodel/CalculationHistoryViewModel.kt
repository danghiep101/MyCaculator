package com.example.mycaculator.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycaculator.data.CalculationHistoryDao
import com.example.mycaculator.data.model.CalculationHistory
import kotlinx.coroutines.launch

class CalculationHistoryViewModel(private val dao: CalculationHistoryDao): ViewModel() {
    fun getAllHistory(): LiveData<List<CalculationHistory>>{
        return dao.getAllHistory()
    }

    fun refreshHistory() {
        viewModelScope.launch {

        }
    }
}