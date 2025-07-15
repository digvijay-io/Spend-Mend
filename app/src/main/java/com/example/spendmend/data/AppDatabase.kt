package com.example.spendmend.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

@androidx.room.Database(entities = [Transaction::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "transaction_db"
                ).build()
                INSTANCE = db
                db
            }
        }
    }

}
