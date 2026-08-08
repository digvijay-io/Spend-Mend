package com.example.spendmend.data

import androidx.room.*
import com.example.spendmend.data.model.Goal
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal)

    @Update
    suspend fun updateGoal(goal: Goal)

    @Delete
    suspend fun deleteGoal(goal: Goal)

    @Query("SELECT * FROM goals ORDER BY createdAt DESC")
    fun getAllGoals(): Flow<List<Goal>>

    @Query("SELECT * FROM goals WHERE isCompleted = 0 ORDER BY targetDate ASC")
    fun getActiveGoals(): Flow<List<Goal>>

    @Query("SELECT * FROM goals WHERE isCompleted = 1 ORDER BY targetDate DESC")
    fun getCompletedGoals(): Flow<List<Goal>>

    @Query("SELECT * FROM goals WHERE id = :goalId LIMIT 1")
    suspend fun getGoalById(goalId: Int): Goal?

    @Query("DELETE FROM goals WHERE id = :goalId")
    suspend fun deleteGoalById(goalId: Int)

    @Query("UPDATE goals SET savedAmount = :savedAmount WHERE id = :goalId")
    suspend fun updateSavedAmount(
        goalId: Int,
        savedAmount: Double
    )

    @Query("UPDATE goals SET isCompleted = :completed WHERE id = :goalId")
    suspend fun markGoalCompleted(
        goalId: Int,
        completed: Boolean
    )

    @Query("SELECT COUNT(*) FROM goals WHERE isCompleted = 0")
    fun getActiveGoalsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM goals WHERE isCompleted = 1")
    fun getCompletedGoalsCount(): Flow<Int>

    @Query("SELECT SUM(savedAmount) FROM goals")
    fun getTotalSavedAmount(): Flow<Double?>

    @Query("SELECT SUM(targetAmount) FROM goals")
    fun getTotalTargetAmount(): Flow<Double?>

}