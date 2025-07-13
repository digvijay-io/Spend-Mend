package com.example.spendmend

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert
    suspend fun insert(transaction: Transaction)

    @Query("SELECT * FROM transactions")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions")
    fun getAllTransactionsLive(): LiveData<List<Transaction>>

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions(): Int
}
