package com.example.spendmend.screens.onboarding.model

import androidx.annotation.DrawableRes

data class OnboardingItem(

    val title: String,

    val description: String,

    @DrawableRes
    val image: Int,

    val buttonText: String,

    val showAiChip: Boolean = false,

    val aiChipText: String = "",

    val showSkip: Boolean = true

)