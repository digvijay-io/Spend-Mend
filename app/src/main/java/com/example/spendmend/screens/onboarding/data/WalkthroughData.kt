package com.example.spendmend.screens.onboarding.data

import com.example.spendmend.R
import com.example.spendmend.screens.onboarding.model.OnboardingItem

val onboardingItems = listOf(

    OnboardingItem(

        title = "Automated Tracking",

        description = "Track expenses without lifting a finger. SpendMend automatically reads financial SMS to keep your records updated.",

        image = R.drawable.onboarding_wallet,

        buttonText = "Next"

    ),

    OnboardingItem(

        title = "Smart Categorization",

        description = "SpendMend intelligently organizes your expenses into categories like food, travel, bills and shopping so you always know where your money goes.",

        image = R.drawable.onboarding_ai,

        buttonText = "Next",

        showAiChip = true,

        aiChipText = "AI MAGIC"

    ),

    OnboardingItem(

        title = "Financial Insights",

        description = "Get AI-powered insights to monitor your spending, build healthier financial habits and confidently achieve your goals.",

        image = R.drawable.onboarding_insights,

        buttonText = "Get Started",

        showSkip = false

    )

)