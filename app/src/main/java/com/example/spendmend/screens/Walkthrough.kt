package com.example.spendmend.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.spendmend.R
import kotlinx.coroutines.delay
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SplashScreen(navController: NavHostController) {
    LaunchedEffect(true) {
        delay(2000) // Optional delay for showing splash
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("walkthrough1") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Spend Mend", fontSize = 32.sp, color = Color.White)
    }
}

@Composable
fun WalkthroughScreen1(navController: NavHostController) {
    CommonWalkthrough(
        imageRes = R.drawable.walkthrough_1,
        title = "Welcome to Spend Mend",
        description = "Track, Save and Grow your Money like Magic",
        onNext = { navController.navigate("walkthrough2") },
        onSkip = { navController.navigate("login") { popUpTo("splash") { inclusive = true } } }
    )
}

@Composable
fun WalkthroughScreen2(navController: NavHostController) {
    CommonWalkthrough(
        imageRes = R.drawable.walkthrough_2,
        title = "Set Smart Budgets",
        description = "Spend wisely and save more.",
        onNext = { navController.navigate("walkthrough3") },
        onSkip = { navController.navigate("login") { popUpTo("splash") { inclusive = true } } }
    )
}

@Composable
fun WalkthroughScreen3(navController: NavHostController) {
    CommonWalkthrough(
        imageRes = R.drawable.walkthrough_3,
        title = "",
        description = "Master your money, one smart move at a time. Track, budget, and save with confidence.",
        onNext = { navController.navigate("login") },
        onSkip = { navController.navigate("login") { popUpTo("splash") { inclusive = true } } }
    )
}

@Composable
fun CommonWalkthrough(
    imageRes: Int,
    title: String,
    description: String,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(250.dp).padding(bottom = 16.dp)
                )
                Text(text = title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = description, fontSize = 16.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 8.dp))
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onNext,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF239947)),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Text("Next")
                }
                Text(
                    text = "Skip",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 12.dp, bottom = 8.dp).clickable { onSkip() }
                )
            }
        }
    }
}

@Composable
fun Onboarding(navController: NavHostController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Image(
                painter = painterResource(id = R.drawable.success), // Replace with your drawable
                contentDescription = "Success Illustration",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Success !!!",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"Take charge of your finances — track, save, and grow.\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B050))
                ) {
                    Text("Let’s Start", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

