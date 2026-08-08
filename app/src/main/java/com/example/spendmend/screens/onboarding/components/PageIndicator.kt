package com.example.spendmend.screens.onboarding.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.spendmend.ui.theme.BrandGreen

@Composable
fun PageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        repeat(pageCount) { index ->

            val selected = index == currentPage

            val width by animateDpAsState(
                targetValue = if (selected) 26.dp else 8.dp,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "indicatorWidth"
            )

            val color by animateColorAsState(
                targetValue = if (selected)
                    BrandGreen
                else
                    MaterialTheme.colorScheme.outlineVariant,
                label = "indicatorColor"
            )

            Box(
                modifier = Modifier
                    .width(width)
                    .size(height = 8.dp, width = width)
                    .clip(
                        if (selected)
                            RoundedCornerShape(50)
                        else
                            CircleShape
                    )
                    .background(color)
            )

        }

    }

}