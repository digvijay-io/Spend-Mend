package com.example.spendmend.data

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAll(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllLive(): LiveData<List<Transaction>>

    @Query("DELETE FROM transactions")
    suspend fun deleteAll(): Int

    // ✅ FIXED: use correct column name "transactionType"
    @Query("SELECT * FROM transactions WHERE transactionType = 'Expense' ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Transaction>>

    @Query("SELECT SUM(amount) FROM transactions WHERE transactionType = 'Expense'")
    fun getTotalExpense(): Flow<Double?>

    @Query("""
        SELECT category, SUM(amount) as total 
        FROM transactions 
        WHERE transactionType = 'Expense'
        GROUP BY category
    """)
    fun getCategorySummary(): Flow<List<CategorySummary>>
}


