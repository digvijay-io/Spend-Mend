package com.example.spendmend.ui.state

import com.example.spendmend.data.model.BudgetStatus

data class BudgetUiState(

    val budget: Double = 0.0,

    val spent: Double = 0.0,

    val remaining: Double = 0.0,

    val progress: Float = 0f,

    val percentage: Int = 0,

    val status: BudgetStatus = BudgetStatus.SAFE

)