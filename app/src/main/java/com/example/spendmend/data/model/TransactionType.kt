package com.example.spendmend.data.model

enum class TransactionType {
    INCOME,
    EXPENSE,
    DEBIT;

    fun equals(other: String, bool: Boolean) {}
}