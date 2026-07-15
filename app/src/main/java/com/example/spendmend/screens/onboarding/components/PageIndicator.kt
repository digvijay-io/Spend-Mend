package com.example.spendmend.screens.onboarding.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
    currentPage: Int
) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        repeat(pageCount) { index ->

            val width by animateDpAsState(
                targetValue = if (index == currentPage) 28.dp else 8.dp,
                label = ""
            )

            val color by animateColorAsState(
                targetValue = if (index == currentPage)
                    BrandGreen
                else
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                label = ""
            )

            Box(
                modifier = Modifier
                    .width(width)
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(color)
            )
        }
    }
}