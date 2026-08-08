package com.example.spendmend.screens.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

    val prefs = remember {
        OnboardingPreferences(navController.context)
    }

    var showLogo by remember { mutableStateOf(false) }
    var showTagline by remember { mutableStateOf(false) }
    var showFooter by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "logo")

    val logoScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.025f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1800,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LaunchedEffect(Unit) {

        delay(120)

        showLogo = true

        delay(180)

        showTagline = true

        delay(180)

        showFooter = true

        delay(1000)

        val user = FirebaseAuth.getInstance().currentUser

        when {

            user != null -> {

                navController.navigate("main") {
                    popUpTo("splash") {
                        inclusive = true
                    }
                }

            }

            !prefs.isOnboardingCompleted() -> {

                navController.navigate("onboarding") {
                    popUpTo("splash") {
                        inclusive = true
                    }
                }

            }

            else -> {

                navController.navigate("login") {
                    popUpTo("splash") {
                        inclusive = true
                    }
                }

            }

        }

    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-56).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                AnimatedVisibility(
                    visible = showLogo,
                    enter = fadeIn(
                        animationSpec = tween(
                            durationMillis = 650,
                            easing = FastOutSlowInEasing
                        )
                    ) +
                            slideInVertically(
                                initialOffsetY = { 60 },
                                animationSpec = tween(
                                    durationMillis = 650,
                                    easing = FastOutSlowInEasing
                                )
                            )
                ) {

                    Text(
                        text = "SpendMend",
                        modifier = Modifier
                            .scale(logoScale)
                            .semantics {
                                heading()
                            },
                        color = BrandGreen,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    )

                }

                Spacer(modifier = Modifier.height(14.dp))

                AnimatedVisibility(
                    visible = showTagline,
                    enter = fadeIn(
                        animationSpec = tween(600)
                    ) +
                            slideInVertically(
                                initialOffsetY = { 24 },
                                animationSpec = tween(600)
                            )
                ) {

                    Text(
                        text = "Smart AI-Powered Expense\nTracking",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 25.sp
                    )

                }

            }

            AnimatedVisibility(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 36.dp),
                visible = showFooter,
                enter = fadeIn(
                    animationSpec = tween(700)
                )
            ) {

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = BrandGreen,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Text(
                        text = "The Quietly Brilliant Assistant",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                }

            }

        }

    }

}