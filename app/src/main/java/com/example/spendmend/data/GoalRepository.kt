package com.example.spendmend.data

import com.example.spendmend.data.GoalDao
import com.example.spendmend.data.model.Goal
import kotlinx.coroutines.flow.Flow

class GoalRepository(
    private val goalDao: GoalDao
) {

    val activeGoals: Flow<List<Goal>> =
        goalDao.getActiveGoals()

    val completedGoals: Flow<List<Goal>> =
        goalDao.getCompletedGoals()

    val allGoals: Flow<List<Goal>> =
        goalDao.getAllGoals()

    val activeGoalsCount: Flow<Int> =
        goalDao.getActiveGoalsCount()

    val completedGoalsCount: Flow<Int> =
        goalDao.getCompletedGoalsCount()

    val totalSavedAmount: Flow<Double?> =
        goalDao.getTotalSavedAmount()

    val totalTargetAmount: Flow<Double?> =
        goalDao.getTotalTargetAmount()

    suspend fun insertGoal(goal: Goal) {
        goalDao.insertGoal(goal)
    }

    suspend fun updateGoal(goal: Goal) {
        goalDao.updateGoal(goal)
    }

    suspend fun deleteGoal(goal: Goal) {
        goalDao.deleteGoal(goal)
    }

    suspend fun deleteGoalById(id: Int) {
        goalDao.deleteGoalById(id)
    }

    suspend fun getGoalById(id: Int): Goal? {
        return goalDao.getGoalById(id)
    }

    suspend fun updateSavedAmount(
        goalId: Int,
        savedAmount: Double
    ) {
        goalDao.updateSavedAmount(
            goalId,
            savedAmount
        )
    }

    suspend fun markGoalCompleted(
        goalId: Int,
        completed: Boolean
    ) {
        goalDao.markGoalCompleted(
            goalId,
            completed
        )
    }
}