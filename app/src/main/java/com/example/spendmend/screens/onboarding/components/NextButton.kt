package com.example.spendmend.screens.onboarding.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.spendmend.ui.theme.BrandGreen

@Composable
fun NextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = MaterialTheme.shapes.extraLarge,
        colors = ButtonDefaults.buttonColors(
            containerColor = BrandGreen
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 6.dp
        )
    ) {

        AnimatedContent(
            targetState = text,
            transitionSpec = {
                (
                        slideInHorizontally(
                            initialOffsetX = { it / 2 },
                            animationSpec = androidx.compose.animation.core.tween(
                                250,
                                easing = FastOutSlowInEasing
                            )
                        ) + fadeIn()
                        ).togetherWith(
                        slideOutHorizontally(
                            targetOffsetX = { -it / 2 },
                            animationSpec = androidx.compose.animation.core.tween(
                                250,
                                easing = FastOutSlowInEasing
                            )
                        ) + fadeOut()
                    ).using(
                        SizeTransform(clip = false)
                    )
            },
            label = "buttonText"
        ) { value ->

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = value,
                    fontWeight = FontWeight.SemiBold
                )

                Icon(
                    imageVector = Icons.Rounded.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.padding(start = 8.dp)
                )

            }

        }

    }

}