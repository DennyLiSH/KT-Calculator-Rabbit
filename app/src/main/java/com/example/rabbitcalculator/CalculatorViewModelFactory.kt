package com.example.rabbitcalculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rabbitcalculator.calculator.presentation.CalculatorViewModel
import com.example.rabbitcalculator.di.AppContainer

/**
 * CalculatorViewModel 工厂
 */
class CalculatorViewModelFactory(
    private val appContainer: AppContainer
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalculatorViewModel::class.java)) {
            return appContainer.createCalculatorViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
