// DataStoreManager.kt
package com.example.spendmend.data

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore // ✅ Correct import
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.incomeDataStore by preferencesDataStore(name = "user_prefs")

class DataStoreManager(private val context: Context) {

    companion object {
        val MONTHLY_INCOME_KEY = doublePreferencesKey("monthly_income")
    }

    val income: Flow<Double> = context.incomeDataStore.data.map { prefs: Preferences ->
        prefs[MONTHLY_INCOME_KEY] ?: 0.0
    }

    suspend fun setIncome(value: Double) {
        context.incomeDataStore.edit { prefs ->
            prefs[MONTHLY_INCOME_KEY] = value
        }
    }
}
