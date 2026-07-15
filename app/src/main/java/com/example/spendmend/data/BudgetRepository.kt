package com.example.spendmend.data

import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class BudgetRepository(
    private val budgetDao: BudgetDao
) {

    companion object {
        const val DEFAULT_BUDGET = 30000.0
    }

    /**
     * Creates a budget for the current month if it doesn't exist.
     *
     * If a previous month's budget exists, copy its amount.
     * Otherwise use the default budget.
     */
    suspend fun ensureCurrentMonthBudget() {

        val calendar = Calendar.getInstance()

        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentYear = calendar.get(Calendar.YEAR)

        val existing = budgetDao.getBudget(
            currentMonth,
            currentYear
        )

        if (existing != null) return

        val latestBudget = budgetDao.getLatestBudget()

        val amount = latestBudget?.amount ?: DEFAULT_BUDGET

        budgetDao.insertBudget(
            Budget(
                month = currentMonth,
                year = currentYear,
                amount = amount
            )
        )
    }

    /**
     * Observe current month's budget.
     */
    fun getCurrentMonthBudget(): Flow<Budget?> {

        val calendar = Calendar.getInstance()

        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        return budgetDao.getBudgetForMonth(
            month,
            year
        )
    }

    /**
     * Update current month's budget.
     */
    suspend fun updateCurrentMonthBudget(
        amount: Double
    ) {

        val calendar = Calendar.getInstance()

        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        val current = budgetDao.getBudget(
            month,
            year
        )

        if (current == null) {

            budgetDao.insertBudget(
                Budget(
                    month = month,
                    year = year,
                    amount = amount
                )
            )

        } else {

            budgetDao.updateBudget(
                current.copy(
                    amount = amount,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    /**
     * Returns current budget once.
     */
    suspend fun getCurrentBudget(): Budget? {

        val calendar = Calendar.getInstance()

        return budgetDao.getBudget(
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.YEAR)
        )
    }

    fun getBudgetFlow(
        month: Int,
        year: Int
    ): Flow<Budget?> {

        return budgetDao.getBudgetForMonth(
            month,
            year
        )

    }

    suspend fun saveBudget(
        month: Int,
        year: Int,
        amount: Double
    ) {

        val existing = budgetDao.getBudget(
            month,
            year
        )

        if (existing == null) {

            budgetDao.insertBudget(
                Budget(
                    month = month,
                    year = year,
                    amount = amount
                )
            )

        } else {

            budgetDao.updateBudget(
                existing.copy(
                    amount = amount,
                    updatedAt = System.currentTimeMillis()
                )
            )

        }

    }
}