package com.example.spendmend.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spendmend.data.model.Bank
import com.example.spendmend.data.model.Category
import com.example.spendmend.data.model.PaymentMethod
import com.example.spendmend.data.model.TransactionType


@Entity(tableName = "transactions")
data class Transaction(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Basic Information
    val amount: Double,
    val merchant: String,
    val description: String,

    // Transaction Details
    val category: Category = Category.OTHER,
    val transactionType: TransactionType,
    val paymentMethod: PaymentMethod = PaymentMethod.OTHER,
    val bankName: Bank = Bank.OTHER,
    val accountNumber: String = "",

    // Date
    val date: Long,

    // SMS
    val isAutoDetected: Boolean = false,
    val smsBody: String? = null,
    val smsHash: String = "",      // <-- ADD THIS
    val confidence: Float = 1f,

    // Optional
    val balanceAfterTransaction: Double? = null,
    val note: String = "",
    val receiptImage: String? = null,

    // Sync
    val isSynced: Boolean = false,

    // Audit
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)