package com.example.spendmend.screens.onboarding.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendmend.ui.theme.BrandGreen

@Composable
fun AIMagicChip(

    text: String

) {

    Surface(

        shape = CircleShape,

        color = BrandGreen.copy(alpha = .10f)

    ) {

        Row(

            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp),

            horizontalArrangement = Arrangement.Center,

            verticalAlignment = Alignment.CenterVertically

        ) {

            Icon(

                imageVector = Icons.Rounded.AutoAwesome,

                contentDescription = null,

                tint = BrandGreen

            )

            Text(

                text = "  $text",

                color = BrandGreen,

                fontSize = 11.sp,

                fontWeight = FontWeight.Bold

            )

        }

    }

}