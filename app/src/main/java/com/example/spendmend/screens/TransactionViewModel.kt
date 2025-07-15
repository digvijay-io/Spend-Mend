package com.example.spendmend.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendmend.data.AppDatabase
import com.example.spendmend.data.Transaction
import com.example.spendmend.data.TransactionDao
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).transactionDao()

    val transactions: StateFlow<List<Transaction>> = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalIncome: StateFlow<Double> = transactions.map { list ->
        list.filter { it.transactionType.equals("credit", true) }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpense: StateFlow<Double> = transactions.map { list ->
        list.filter { it.transactionType.equals("debit", true) }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val savings: StateFlow<Double> = combine(totalIncome, totalExpense) { income, expense ->
        income - expense
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            dao.insert(transaction)
        }
    }
}
