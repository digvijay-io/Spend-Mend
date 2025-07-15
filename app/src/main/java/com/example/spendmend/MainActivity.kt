package com.example.spendmend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.spendmend.screens.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SpendMendApp() }
    }
}

@Composable
fun SpendMendApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("walkthrough1") { WalkthroughScreen1(navController) }
        composable("walkthrough2") { WalkthroughScreen2(navController) }
        composable("walkthrough3") { WalkthroughScreen3(navController) }
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignupScreen(navController) }
        composable("otp") { PhoneAuthScreen(navController) }
        composable("verifyotp") { OtpVerificationScreen(navController) }
        composable("home") { HomeScreen() }
        composable("success") { Onboarding(navController) }
    }
}
