package com.example.spendmend.data

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert suspend fun insert(transaction: Transaction)
    @Query("SELECT * FROM transactions") fun getAll(): Flow<List<Transaction>>
    @Query("SELECT * FROM transactions") fun getAllLive(): LiveData<List<Transaction>>
    @Query("DELETE FROM transactions") suspend fun deleteAll(): Int
}
