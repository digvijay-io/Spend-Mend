package com.example.spendmend.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "budgets",
    indices = [
        Index(value = ["month", "year"], unique = true)
    ]
)
data class Budget(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /**
     * Month (1-12)
     */
    val month: Int,

    /**
     * Example: 2026
     */
    val year: Int,

    /**
     * Monthly budget amount
     */
    val amount: Double,

    /**
     * Created timestamp
     */
    val createdAt: Long = System.currentTimeMillis(),

    /**
     * Updated timestamp
     */
    val updatedAt: Long = System.currentTimeMillis()
)