package com.example.spendmend.screens.onboarding.data

import com.example.spendmend.R
import com.example.spendmend.screens.onboarding.model.OnboardingItem

object WalkthroughData {

    val pages = listOf(

        OnboardingItem(

            image = R.drawable.walkthrough_1,

            title = "Track Every Rupee",

            description =
                "Automatically organize your expenses and understand where your money goes."
        ),

        OnboardingItem(

            image = R.drawable.walkthrough_2,

            title = "Create Smart Budgets",

            description =
                "Set monthly budgets, control spending, and achieve your financial goals."
        ),

        OnboardingItem(

            image = R.drawable.walkthrough_3,

            title = "AI Powered Insights",

            description =
                "Discover spending trends with intelligent charts and personalized insights."
        )

    )
}