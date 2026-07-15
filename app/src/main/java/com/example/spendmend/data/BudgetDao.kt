package com.example.spendmend.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    /**
     * Observe the budget for a specific month.
     */
    @Query("""
        SELECT * FROM budgets
        WHERE month = :month
        AND year = :year
        LIMIT 1
    """)
    fun getBudgetForMonth(
        month: Int,
        year: Int
    ): Flow<Budget?>

    /**
     * Get the budget once (used by repository).
     */
    @Query("""
        SELECT * FROM budgets
        WHERE month = :month
        AND year = :year
        LIMIT 1
    """)
    suspend fun getBudget(
        month: Int,
        year: Int
    ): Budget?

    /**
     * Insert new monthly budget.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(
        budget: Budget
    )

    /**
     * Update existing budget.
     */
    @Update
    suspend fun updateBudget(
        budget: Budget
    )

    /**
     * Latest budget created.
     * Used when a new month starts.
     */
    @Query("""
        SELECT * FROM budgets
        ORDER BY year DESC, month DESC
        LIMIT 1
    """)
    suspend fun getLatestBudget(): Budget?

    /**
     * All budgets (future analytics/settings screen).
     */
    @Query("""
        SELECT * FROM budgets
        ORDER BY year DESC, month DESC
    """)
    fun getAllBudgets(): Flow<List<Budget>>

    /**
     * Delete all budgets.
     */
    @Query("DELETE FROM budgets")
    suspend fun deleteAll()
}