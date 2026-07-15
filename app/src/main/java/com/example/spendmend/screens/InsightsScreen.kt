package com.example.spendmend.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState

@Composable
fun PieChart(
    data: List<CategoryExpense>,
    modifier: Modifier = Modifier
) {

    val total = data.sumOf { it.amount.toDouble() }.toFloat()

    if (total <= 0f) return

    val angles = data.map {

        (it.amount / total) * 360f

    }

    val animatedAngles = angles.map {

        animateFloatAsState(

            targetValue = it,

            animationSpec = tween(900),

            label = ""

        )

    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        Canvas(
            modifier = Modifier.matchParentSize()
        ) {

            var startAngle = -90f

            animatedAngles.forEachIndexed { index, sweep ->

                drawArc(
                    color = data[index].color,
                    startAngle = startAngle,
                    sweepAngle = sweep.value,
                    useCenter = false,
                    style = Stroke(
                        width = 48f,
                        cap = StrokeCap.Round
                    ),
                    size = Size(size.width, size.height)
                )

                startAngle += sweep.value

            }

        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Total",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "₹${total.roundToInt()}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

        }

    }

}


@Composable
fun SummaryCard(
    totalExpense: Double
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                "This Month",
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                "₹${totalExpense.roundToInt()}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "Total Expenses",
                color = Color.Gray
            )

        }

    }

}

@Composable
fun CategoryCard(
    expense: CategoryExpense,
    totalExpense: Double
) {

    val progress =
        if (totalExpense == 0.0)
            0f
        else
            expense.amount / totalExpense.toFloat()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            expense.color,
                            CircleShape
                        )
                )

                Spacer(modifier = Modifier.size(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        expense.category.name
                            .replace("_", " ")
                            .lowercase()
                            .replaceFirstChar { it.titlecase() },
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "₹${expense.amount.roundToInt()}",
                        color = Color.Gray
                    )

                }

                Text(
                    "${(progress * 100).roundToInt()}%"
                )

            }

            Spacer(modifier = Modifier.height(14.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )

        }

    }

}

@Composable
fun InsightsScreen(
    viewModel: TransactionViewModel = viewModel()
) {

    val categorySummary by viewModel.categorySummary.collectAsState()

    val totalExpense by viewModel.totalExpense.collectAsState()

    val colors = listOf(
        Color(0xFF4CAF50),
        Color(0xFF2196F3),
        Color(0xFFFF9800),
        Color(0xFFE91E63),
        Color(0xFF9C27B0),
        Color(0xFF009688),
        Color(0xFFFF5722),
        Color(0xFF3F51B5),
        Color(0xFF795548),
        Color(0xFFFFC107)
    )

    val pieData = categorySummary.entries.mapIndexed { index, entry ->

        CategoryExpense(
            category = entry.key,
            amount = entry.value.toFloat(),
            color = colors[index % colors.size]
        )

    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(
            top = 20.dp,
            bottom = 32.dp
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        item {

            Text(
                text = "Insights",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Track where your money goes",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

        }

        item {

            SummaryCard(
                totalExpense = totalExpense
            )

        }

        if (pieData.isNotEmpty()) {

            item {

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {

                    PieChart(
                        data = pieData,
                        modifier = Modifier
                            .fillMaxWidth(0.70f)
                            .aspectRatio(1f)
                    )

                }

            }

            item {

                Text(
                    text = "Expense Breakdown",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

            }

            items(
                pieData.size
            ) { index ->

                CategoryCard(
                    expense = pieData[index],
                    totalExpense = totalExpense
                )

            }

        } else {

            item {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "📊",
                            style = MaterialTheme.typography.displayMedium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "No Insights Yet",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Add a few expenses and SpendMend will automatically generate insights.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )

                    }

                }

            }

        }

    }

}