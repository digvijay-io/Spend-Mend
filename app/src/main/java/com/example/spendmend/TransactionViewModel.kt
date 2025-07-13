package com.example.spendmend

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val transactionDao = AppDatabase.getDatabase(application).transactionDao()

    // Transactions fetched from the database
    val transactions: StateFlow<List<Transaction>> = transactionDao
        .getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // 🆕 Function to insert a new Transaction
    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionDao.insert(transaction)
        }
    }
}
