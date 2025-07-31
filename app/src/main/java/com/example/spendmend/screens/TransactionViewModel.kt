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
        list.filter { it.transactionType.equals("Expense", true) }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val savings: StateFlow<Double> = combine(totalIncome, totalExpense) { income, expense ->
        income - expense
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val categorySummary: StateFlow<Map<String, Double>> = transactions
        .map { list ->
            list.filter { it.transactionType.equals("Expense", true) }
                .groupBy { it.category }
                .mapValues { (_, txns) -> txns.sumOf { it.amount } }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())


    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            dao.insert(transaction)
        }
    }

    fun insertDummyTransactions() {
        viewModelScope.launch {
            val dummyList = listOf(
                Transaction(amount = 499.0, description = "Zomato Order", date = "2025-07-21", merchant = "Zomato", transactionType = "Expense", category = "Food"),
                Transaction(amount = 299.0, description = "Swiggy Order", date = "2025-07-21", merchant = "Swiggy", transactionType = "Expense", category = "Food"),
                Transaction(amount = 6981.0, description = "Flight Booking", date = "2025-07-20", merchant = "Ixigo", transactionType = "Expense", category = "Travel"),
                Transaction(amount = 120.0, description = "Jio Recharge", date = "2025-07-19", merchant = "Jio", transactionType = "Expense", category = "Bills"),
                Transaction(amount = 2049.0, description = "Amazon Purchase", date = "2025-07-18", merchant = "Amazon", transactionType = "Expense", category = "Shopping"),
                Transaction(amount = 25000.0, description = "Salary Credit", date = "2025-07-17", merchant = "Company XYZ", transactionType = "Income", category = "Income")
            )
            dummyList.forEach { dao.insert(it) }
        }
    }
}
