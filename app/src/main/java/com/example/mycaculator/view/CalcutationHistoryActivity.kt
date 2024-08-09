package com.example.mycaculator.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.mycaculator.data.CalculationHistoryDao
import com.example.mycaculator.viewmodel.CalculationHistoryViewModel
import com.example.mycaculator.viewmodel.CalculationHistoryViewModelFactory
import com.example.mycaculator.data.model.CalculatorDatabase
import com.example.mycaculator.databinding.ActivityCalcutationHistoryBinding

class CalcutationHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalcutationHistoryBinding
    private val viewModel : CalculationHistoryViewModel by viewModels {
        CalculationHistoryViewModelFactory(getDao())
    }
    private lateinit var caculationHistoryDao: CalculationHistoryDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalcutationHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.getAllHistory().observe(this){
            binding.textResultHistory.text = it.joinToString("\n")
            { "${it.expression} = ${it.result}" }
        }

    }
    private fun getDao(): CalculationHistoryDao {
        val database = CalculatorDatabase.getDatabase(this)
        return database.calculationHistoryDao()
    }
}