package com.example.spendmend.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.spendmend.screens.onboarding.datastore.OnboardingPreferences
import com.example.spendmend.ui.theme.BrandGreen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavHostController
) {

    val prefs = OnboardingPreferences(navController.context)

    LaunchedEffect(Unit) {

        delay(1500)

        val user = FirebaseAuth.getInstance().currentUser

        when {

            user != null -> {

                navController.navigate("main") {
                    popUpTo("splash") { inclusive = true }
                }

            }

            !prefs.isOnboardingCompleted() -> {

                navController.navigate("onboarding") {
                    popUpTo("splash") { inclusive = true }
                }

            }

            else -> {

                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }

            }

        }

    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandGreen),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = "SpendMend",
            color = Color.White,
            fontSize = 40.sp,
            fontWeight = FontWeight.ExtraBold
        )

    }

}