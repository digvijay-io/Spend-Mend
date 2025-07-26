package com.example.spendmend.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.roundToInt

@Composable
fun PieChart(
    data: List<CategoryExpense>,
    modifier: Modifier = Modifier
) {
    val total = data.sumOf { it.amount.toDouble() }.toFloat()
    val angles = data.map { 360 * (it.amount / total) }

    // Animate each slice
    val animatedAngles = angles.map {
        animateFloatAsState(targetValue = it, animationSpec = tween(durationMillis = 1000))
    }

    Canvas(modifier = modifier) {
        var startAngle = -90f
        animatedAngles.forEachIndexed { index, sweep ->
            drawArc(
                color = data[index].color,
                startAngle = startAngle,
                sweepAngle = sweep.value,
                useCenter = true
            )
            startAngle += sweep.value
        }
    }
}

@Composable
fun InsightsScreen(viewModel: TransactionViewModel = viewModel()) {
    val categoryData = viewModel.categorySummary.collectAsState().value
    val totalExpense = viewModel.totalExpense.collectAsState().value

    // Material color palette
    val categoryColors = listOf(
        Color(0xFFFF9800), // Orange
        Color(0xFF2196F3), // Blue
        Color(0xFF4CAF50), // Green
        Color(0xFF9C27B0), // Purple
        Color(0xFFE91E63)  // Pink
    )

    // Convert to data class list
    val pieData = categoryData.entries.mapIndexed { index, entry ->
        CategoryExpense(
            category = entry.key,
            amount = entry.value.toFloat(),
            color = categoryColors[index % categoryColors.size]
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Total Expense: ₹${totalExpense}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        PieChart(
            data = pieData,
            modifier = Modifier.size(250.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ✅ Legend
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            pieData.forEach { category ->
                val percentage = if (totalExpense > 0) {
                    ((category.amount / totalExpense) * 100).roundToInt()
                } else 0
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(category.color, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${category.category}: ₹${category.amount} (${percentage}%)",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
