package com.example.spendmend.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.spendmend.screens.onboarding.components.NextButton
import com.example.spendmend.screens.onboarding.components.OnboardingPage
import com.example.spendmend.screens.onboarding.components.PageIndicator
import com.example.spendmend.screens.onboarding.components.SkipButton
import com.example.spendmend.screens.onboarding.data.onboardingItems
import com.example.spendmend.screens.onboarding.datastore.OnboardingPreferences
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    navController: NavController
) {

    val context = LocalContext.current

    val onboardingPreferences = remember {
        OnboardingPreferences(context)
    }

    val pagerState = rememberPagerState(
        pageCount = { onboardingItems.size }
    )

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {

        SkipButton(
            visible = onboardingItems[pagerState.currentPage].showSkip,
            onClick = {

                onboardingPreferences.saveOnboardingCompleted()

                navController.navigate("login") {

                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }

                    launchSingleTop = true

                }

            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 20.dp, end = 24.dp)
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = 72.dp,
                bottom = 140.dp
            )
        ) { page ->

            OnboardingPage(
                item = onboardingItems[page]
            )

        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 24.dp),

            horizontalAlignment = Alignment.CenterHorizontally,

            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            PageIndicator(
                pageCount = onboardingItems.size,
                currentPage = pagerState.currentPage
            )

            NextButton(

                text = onboardingItems[pagerState.currentPage].buttonText,

                onClick = {

                    scope.launch {

                        val isLastPage =
                            pagerState.currentPage == onboardingItems.lastIndex

                        if (!isLastPage) {

                            pagerState.animateScrollToPage(
                                pagerState.currentPage + 1
                            )

                        } else {

                            onboardingPreferences.saveOnboardingCompleted()

                            navController.navigate("login") {

                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }

                                launchSingleTop = true
                                restoreState = true

                            }

                        }

                    }

                }

            )

        }

    }

}