package com.example.spendmend.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendmend.data.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class IncomeViewModel : ViewModel() {

    private lateinit var dataStoreManager: DataStoreManager
    private val _income = MutableStateFlow(0.0)
    val income: StateFlow<Double> = _income

    fun loadIncome(context: Context) {
        dataStoreManager = DataStoreManager(context)
        viewModelScope.launch {
            dataStoreManager.income.collect {
                _income.value = it
            }
        }
    }

    // ✅ Add this function
    fun saveIncome(context: Context, amount: Double) {
        viewModelScope.launch {
            dataStoreManager.setIncome(amount)
            _income.value = amount
        }
    }
}
