package com.example.spendmend.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goal(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,

    val category: GoalCategory,

    val targetAmount: Double,

    val savedAmount: Double,

    /**
     * Stored as epoch milliseconds.
     */
    val targetDate: Long,

    val priority: GoalPriority,

    val notes: String = "",

    val isCompleted: Boolean = false,

    val createdAt: Long = System.currentTimeMillis()

)