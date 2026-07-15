package com.example.spendmend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.spendmend.screens.LoginScreen
import com.example.spendmend.screens.MainBottomNavScreen
import com.example.spendmend.screens.OtpVerificationScreen
import com.example.spendmend.screens.PhoneAuthScreen
import com.example.spendmend.screens.SignupScreen
import com.example.spendmend.screens.onboarding.OnboardingScreen
import com.example.spendmend.screens.splash.SplashScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            SpendMendApp()
        }
    }
}

@Composable
fun SpendMendApp() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        // Splash
        composable("splash") {
            SplashScreen(navController)
        }

        // Onboarding
        composable("onboarding") {
            OnboardingScreen(navController)
        }

        // Login
        composable("login") {
            LoginScreen(navController)
        }

        // Signup
        composable("signup") {
            SignupScreen(navController)
        }

        // Phone Authentication
        composable("otp") {
            PhoneAuthScreen(navController)
        }

        // OTP Verification
        composable("verifyotp") {
            OtpVerificationScreen(navController)
        }

        // Main App (Bottom Navigation)
        composable("main") {
            MainBottomNavScreen()
        }

    }

}