package com.example.spendmend.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import com.example.spendmend.screens.onboarding.components.OnboardingPage
import com.example.spendmend.screens.onboarding.components.PageIndicator
import com.example.spendmend.screens.onboarding.data.WalkthroughData
import com.example.spendmend.screens.onboarding.datastore.OnboardingPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(
    navController: NavController,
) {

    val context = androidx.compose.ui.platform.LocalContext.current

    val onboardingPreferences = remember {
        OnboardingPreferences(context)
    }

    val pages = WalkthroughData.pages

    val pagerState = rememberPagerState(
        pageCount = { pages.size }
    )

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {

        AnimatedVisibility(
            visible = pagerState.currentPage != pages.lastIndex,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(
                    top = 12.dp,
                    end = 16.dp
                )
        ) {

            TextButton(
                onClick = {

                    onboardingPreferences.saveOnboardingCompleted()

                    navController.navigate("login") {

                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }

                }
            ) {

                Text(
                    text = "Skip",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

            }

        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->

            OnboardingPage(
                item = pages[page],
                currentPage = pagerState.currentPage,
                pageCount = pages.size,
                buttonText = if (page == pages.lastIndex) {
                    "Get Started"
                } else {
                    "Continue"
                },
                pageIndicator = {
                    PageIndicator(
                        pageCount = pages.size,
                        currentPage = pagerState.currentPage
                    )
                },
                onButtonClick = {

                    coroutineScope.launch {

                        if (page < pages.lastIndex) {

                            pagerState.animateScrollToPage(page + 1)

                        } else {

                            onboardingPreferences.saveOnboardingCompleted()

                            delay(400)

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

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            AnimatedVisibility(
                visible = pagerState.currentPage == pages.lastIndex,
                enter = fadeIn(),
                exit = fadeOut()
            ) {

                Text(
                    text = "You're all set ✨",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

            }

        }

    }

}
