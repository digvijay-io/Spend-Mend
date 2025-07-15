package com.example.spendmend.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val description: String,
    val date: String,
    val merchant: String,
    val transactionType: String
)
