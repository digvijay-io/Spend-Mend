package com.example.spendmend.screens

import androidx.compose.ui.graphics.Color
import com.example.spendmend.data.model.Category

data class CategoryExpense(
    val category: Category,
    val amount: Float,
    val color: Color
)
