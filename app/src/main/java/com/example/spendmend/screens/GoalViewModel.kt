package com.example.spendmend.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendmend.data.AppDatabase
import com.example.spendmend.data.model.Goal
import com.example.spendmend.data.GoalRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.ceil

class GoalViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = GoalRepository(
        AppDatabase
            .getDatabase(application)
            .goalDao()
    )

    val activeGoals = repository.activeGoals
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val completedGoals = repository.completedGoals
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val activeGoalsCount = repository.activeGoalsCount
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )

    val completedGoalsCount = repository.completedGoalsCount
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )

    val totalSavedAmount = repository.totalSavedAmount
        .map { it ?: 0.0 }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0.0
        )

    val totalTargetAmount = repository.totalTargetAmount
        .map { it ?: 0.0 }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0.0
        )

    val overallProgress = combine(
        totalSavedAmount,
        totalTargetAmount
    ) { saved, target ->

        if (target == 0.0)
            0f
        else
            (saved / target).toFloat()

    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0f
    )

    suspend fun getGoalById(
        id: Int
    ): Goal? {

        return repository.getGoalById(id)

    }

    fun addGoal(
        goal: Goal
    ) {

        viewModelScope.launch {

            repository.insertGoal(goal)

        }

    }

    fun updateGoal(
        goal: Goal
    ) {

        viewModelScope.launch {

            repository.updateGoal(goal)

        }

    }

    fun deleteGoal(
        goal: Goal
    ) {

        viewModelScope.launch {

            repository.deleteGoal(goal)

        }

    }

    fun updateSavedAmount(
        goal: Goal,
        newAmount: Double
    ) {

        viewModelScope.launch {

            repository.updateSavedAmount(
                goal.id,
                newAmount
            )

            if (newAmount >= goal.targetAmount) {

                repository.markGoalCompleted(
                    goal.id,
                    true
                )

            }

        }

    }

    fun calculateRemainingAmount(
        goal: Goal
    ): Double {

        return (goal.targetAmount - goal.savedAmount)
            .coerceAtLeast(0.0)

    }

    fun calculateProgress(
        goal: Goal
    ): Float {

        if (goal.targetAmount == 0.0)
            return 0f

        return (goal.savedAmount / goal.targetAmount)
            .toFloat()

    }

    fun calculateMonthlySavingNeeded(
        goal: Goal
    ): Double {

        val remaining = calculateRemainingAmount(goal)

        val today = System.currentTimeMillis()

        val diff = goal.targetDate - today

        val months =
            ceil(
                diff / (1000.0 * 60 * 60 * 24 * 30)
            ).toInt()

        if (months <= 0)
            return remaining

        return remaining / months

    }


}