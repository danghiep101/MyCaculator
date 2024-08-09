package com.example.mycaculator.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mycaculator.data.model.CalculationHistory
import com.example.mycaculator.data.model.CalculatorDatabase
import kotlinx.coroutines.launch

import net.objecthunter.exp4j.ExpressionBuilder
import java.text.DecimalFormat

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val _workingText = MutableLiveData<String>()
    val workingText: LiveData<String> = _workingText

    private val _resultText = MutableLiveData<String>()
    val resultText: LiveData<String> = _resultText

    private val _textHistory = MutableLiveData<String>()
    val textHistory: LiveData<String> = _textHistory
   private val database = CalculatorDatabase.getDatabase(application)
    private val historyDao = database.calculationHistoryDao()

    init {
        _workingText.value = ""
        _resultText.value = ""
        _textHistory.value = ""
    }

    fun onNumberButtonClicked(text: String) {
        when (text) {
            "AC" -> {
                _workingText.value = ""
                _resultText.value = ""
                _textHistory.value = ""
            }

            "C" -> {
                _workingText.value = _workingText.value?.dropLast(1)
            }

            else -> {
                _workingText.value += text
                calculateAction()
            }
        }
    }

    fun onOperationButtonClicked(text: String) {
        val currentText = _workingText.value ?: ""
        when (text) {
            "+", "-", "*", "/", "%" -> {
                _workingText.value = currentText + text
            }
            "=" -> {
                equalAction()
            }
        }
    }

    private fun equalAction() {
        try {
            val result = _resultText.value?: return
            val workingText = _workingText.value?: return
            _textHistory.value = "$workingText\n=$result"

            viewModelScope.launch{
                historyDao.insert(CalculationHistory(expression = workingText, result = result))
            }
        } catch (e: Exception) {
            Log.d("CALCULATE", "Exception: ${e.message}")
        }
    }

    private fun calculateAction() {
        val currentText = _workingText.value ?: ""
        try {
            if (currentText.isEmpty()) {
                throw IllegalArgumentException("Invalid Expression")
            }
            val validExpression = fixExpression(currentText)
            val result = evaluateExpression(validExpression)
            val formattedResult = DecimalFormat("0.#########").format(result)
            _resultText.value = formattedResult
        } catch (e: Exception) {
            Log.e("tag", "Exception: ${e.message}")
        }
    }

    private fun evaluateExpression(expression: String): Double {
        return ExpressionBuilder(expression).build().evaluate().toDouble()
    }

    private fun fixExpression(expression: String): String {
        val regex = "^[*/%]+".toRegex()
        var fixedExpression = expression
        if (regex.containsMatchIn(fixedExpression)) {
            fixedExpression = fixedExpression.replace(regex, "0$0")
        }
        return fixedExpression
    }


}