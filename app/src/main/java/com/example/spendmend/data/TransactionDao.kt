package com.example.spendmend.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.spendmend.data.model.Bank
import com.example.spendmend.data.model.Category
import com.example.spendmend.data.model.PaymentMethod
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    // ----------------------------
    // INSERT
    // ----------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction)

    // ----------------------------
    // UPDATE
    // ----------------------------

    @Update
    suspend fun update(transaction: Transaction)

    // ----------------------------
    // DELETE
    // ----------------------------

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()

    // ----------------------------
    // GET ALL
    // ----------------------------

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAll(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllLive(): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): Transaction?

    // ----------------------------
    // EXPENSES
    // ----------------------------

    @Query("""
        SELECT * FROM transactions
        WHERE transactionType = 'EXPENSE'
        ORDER BY date DESC
    """)
    fun getAllExpenses(): Flow<List<Transaction>>

    @Query("""
        SELECT SUM(amount)
        FROM transactions
        WHERE transactionType = 'EXPENSE'
    """)
    fun getTotalExpense(): Flow<Double?>

    // ----------------------------
    // INCOME
    // ----------------------------

    @Query("""
        SELECT * FROM transactions
        WHERE transactionType = 'INCOME'
        ORDER BY date DESC
    """)
    fun getAllIncome(): Flow<List<Transaction>>

    @Query("""
        SELECT SUM(amount)
        FROM transactions
        WHERE transactionType = 'INCOME'
    """)
    fun getTotalIncome(): Flow<Double?>

    // ----------------------------
    // CATEGORY
    // ----------------------------

    @Query("""
        SELECT category, SUM(amount) AS total
        FROM transactions
        WHERE transactionType = 'EXPENSE'
        GROUP BY category
        ORDER BY total DESC
    """)
    fun getCategorySummary(): Flow<List<CategorySummary>>

    @Query("""
        SELECT * FROM transactions
        WHERE category = :category
        ORDER BY date DESC
    """)
    fun getTransactionsByCategory(category: Category): Flow<List<Transaction>>

    // ----------------------------
    // BANK
    // ----------------------------

    @Query("""
        SELECT * FROM transactions
        WHERE bankName = :bank
        ORDER BY date DESC
    """)
    fun getTransactionsByBank(bank: Bank): Flow<List<Transaction>>

    // ----------------------------
    // PAYMENT METHOD
    // ----------------------------

    @Query("""
        SELECT * FROM transactions
        WHERE paymentMethod = :paymentMethod
        ORDER BY date DESC
    """)
    fun getTransactionsByPaymentMethod(
        paymentMethod: PaymentMethod
    ): Flow<List<Transaction>>

    // ----------------------------
    // SEARCH
    // ----------------------------

    @Query("""
        SELECT * FROM transactions
        WHERE merchant LIKE '%' || :query || '%'
        OR description LIKE '%' || :query || '%'
        ORDER BY date DESC
    """)
    fun searchTransactions(query: String): Flow<List<Transaction>>

    @Query("SELECT EXISTS(SELECT 1 FROM transactions WHERE smsHash = :hash)")
    suspend fun exists(hash: String): Boolean
}