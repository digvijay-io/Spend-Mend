package com.example.spendmend.screens.onboarding.datastore

import android.content.Context

class OnboardingPreferences(context: Context) {

    private val prefs =
        context.getSharedPreferences("spendmend_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ONBOARDING = "onboarding_completed"
    }

    fun saveOnboardingCompleted() {
        prefs.edit().putBoolean(KEY_ONBOARDING, true).apply()
    }

    fun isOnboardingCompleted(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING, false)
    }
}