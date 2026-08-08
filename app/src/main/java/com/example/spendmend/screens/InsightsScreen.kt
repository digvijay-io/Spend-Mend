package com.example.spendmend.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spendmend.data.model.Category
import com.example.spendmend.data.model.PaymentMethod
import com.example.spendmend.ui.theme.BrandGreen
import java.text.DateFormatSymbols
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun InsightsScreen(
    viewModel: TransactionViewModel = viewModel(),
    onBottomBarVisibilityChanged: (Boolean) -> Unit,
) {

    val month by viewModel.selectedMonth.collectAsState()
    val year by viewModel.selectedYear.collectAsState()

    val income by viewModel.currentMonthIncome.collectAsState()
    val expense by viewModel.currentMonthExpense.collectAsState()
    val savings by viewModel.currentMonthSavings.collectAsState()

    val categorySummary by viewModel.categorySummary.collectAsState()

    val healthScore by viewModel.financialHealthScore.collectAsState()

    val expenseTrend by viewModel.expenseTrend.collectAsState()

    val budgetState by viewModel.budgetUiState.collectAsState()

    val previousExpense by viewModel.previousMonthExpense.collectAsState()

    val savingsRate by viewModel.savingRate.collectAsState()

    val aiInsight by viewModel.aiInsight.collectAsState()

    val topMerchants by viewModel.topMerchants.collectAsState()

    val paymentMethods by viewModel.paymentMethodSummary.collectAsState()

    // On Scroll , hide bottom bar

    val listState = rememberLazyListState()

    LaunchedEffect(listState) {

        var previousIndex = 0
        var previousOffset = 0

        snapshotFlow {
            Pair(
                listState.firstVisibleItemIndex,
                listState.firstVisibleItemScrollOffset
            )
        }.collect { (index, offset) ->

            val scrollingDown =
                index > previousIndex ||
                        (index == previousIndex && offset > previousOffset)

            val scrollingUp =
                index < previousIndex ||
                        (index == previousIndex && offset < previousOffset)

            when {
                scrollingDown -> onBottomBarVisibilityChanged(false)
                scrollingUp -> onBottomBarVisibilityChanged(true)
            }

            previousIndex = index
            previousOffset = offset
        }
    }

    LazyColumn(

        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FB)),

        verticalArrangement = Arrangement.spacedBy(18.dp),

        contentPadding = PaddingValues(
            start = 18.dp,
            end = 18.dp,
            top = 18.dp,
            bottom = 120.dp
        ),

        state = listState,

    ) {

        item {

            AnalyticsHeader(

                selectedMonth = month,

                selectedYear = year,

                onPrevious = {

                    viewModel.previousMonth()

                },

                onNext = {

                    viewModel.nextMonth()

                }

            )

        }

        item {

            FinancialHealthCard(

                score = healthScore,

                income = income,

                expense = expense,

                savings = savings

            )

        }

        item {

            IncomeExpenseCard(

                income = income,

                expense = expense,

                previousExpense = previousExpense,

                trend = expenseTrend

            )

        }

        item {

            CategoryBreakdownCard(

                categorySummary = categorySummary

            )

        }

        item {

            TopCategoriesCard(

                categorySummary = categorySummary

            )

        }

        item {

            MonthlyComparisonCard(

                currentExpense = expense,

                previousExpense = previousExpense,

                budget = budgetState.budget,

                savings = savings,

                savingRate = savingsRate

            )

        }

        item {

            AISpendCoachCard(

                insight = aiInsight

            )

        }

        item {

            TopMerchantsCard(

                merchants = topMerchants

            )

        }

        item {

            PaymentMethodsCard(

                paymentMethods = paymentMethods

            )

        }

        item {

            SmartTipsCard(

                healthScore = healthScore,

                savings = savings,

                expense = expense,

                budget = budgetState.budget

            )

        }

    }

}

@Composable
private fun AnalyticsHeader(

    selectedMonth: Int,

    selectedYear: Int,

    onPrevious: () -> Unit,

    onNext: () -> Unit

) {

    val monthName =
        DateFormatSymbols().months[selectedMonth]

    Column {

        Row(

            modifier = Modifier.fillMaxWidth(),

            horizontalArrangement = Arrangement.SpaceBetween,

            verticalAlignment = Alignment.CenterVertically

        ) {

            Column(
                modifier = Modifier.padding(top = 32.dp)
            ) {

                Text(

                    text = "Analytics",

                    fontSize = 32.sp,

                    fontWeight = FontWeight.ExtraBold,

                    color = BrandGreen

                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(

                    text = "Your money, explained.",

                    color = Color.Gray,

                    fontSize = 15.sp

                )

            }

            Surface(

                shape = CircleShape,

                color = Color.White,

                shadowElevation = 4.dp

            ) {

                IconButton(
                    onClick = {}
                ) {

                    Icon(

                        Icons.Outlined.Notifications,

                        null,

                        tint = BrandGreen

                    )

                }

            }

        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(

            shape = RoundedCornerShape(20.dp),

            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),

            modifier = Modifier.fillMaxWidth()

        ) {

            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 16.dp
                    ),

                horizontalArrangement = Arrangement.SpaceBetween,

                verticalAlignment = Alignment.CenterVertically

            ) {

                IconButton(
                    onClick = onPrevious
                ) {

                    Icon(
                        Icons.Rounded.ChevronLeft,
                        null
                    )

                }

                Column(

                    horizontalAlignment = Alignment.CenterHorizontally

                ) {

                    Text(

                        monthName,

                        fontWeight = FontWeight.Bold,

                        fontSize = 20.sp

                    )

                    Text(

                        selectedYear.toString(),

                        color = Color.Gray

                    )

                }

                IconButton(
                    onClick = onNext
                ) {

                    Icon(
                        Icons.Rounded.ChevronRight,
                        null
                    )

                }

            }

        }

    }

}

@Composable
private fun FinancialHealthCard(

    score: Int,

    income: Double,

    expense: Double,

    savings: Double

) {

    val progress by animateFloatAsState(

        targetValue = score / 100f,

        label = ""

    )

    val status = when {

        score >= 85 -> "Excellent"

        score >= 70 -> "Good"

        score >= 50 -> "Fair"

        else -> "Needs Attention"

    }

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(24.dp),

        colors = CardDefaults.cardColors(

            containerColor = Color.White

        )

    ) {

        Column(

            modifier = Modifier.padding(24.dp),

            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            Text(

                text = "Financial Health",

                style = MaterialTheme.typography.titleMedium,

                fontWeight = FontWeight.Bold

            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(

                contentAlignment = Alignment.Center

            ) {

                CircularProgressIndicator(

                    progress = { progress },

                    modifier = Modifier.size(150.dp),

                    strokeWidth = 12.dp,

                    color = BrandGreen,

                    trackColor = Color(0xFFE8EAF0)

                )

                Column(

                    horizontalAlignment = Alignment.CenterHorizontally

                ) {

                    Text(

                        text = score.toString(),

                        fontSize = 36.sp,

                        fontWeight = FontWeight.Bold

                    )

                    Text(

                        status,

                        color = Color.Gray

                    )

                }

            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceEvenly

            ) {

                FinanceMetric(

                    "Income",

                    income,

                    Color(0xFF16A34A)

                )

                FinanceMetric(

                    "Expense",

                    expense,

                    Color(0xFFDC2626)

                )

                FinanceMetric(

                    "Savings",

                    savings,

                    BrandGreen

                )

            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(

                horizontalArrangement = Arrangement.spacedBy(10.dp)

            ) {

                HealthChip(

                    text = if (score >= 80) "Budget Healthy" else "Watch Budget"

                )

                HealthChip(

                    text = if (savings > 0) "Saving Money" else "No Savings"

                )

            }

        }

    }

}

@Composable
private fun FinanceMetric(

    title: String,

    amount: Double,

    color: Color

) {

    Column(

        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Text(

            text = title,

            fontSize = 13.sp,

            color = Color.Gray

        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(

            text = "₹%,.0f".format(amount),

            fontWeight = FontWeight.Bold,

            color = color,

            fontSize = 18.sp

        )

    }

}

@Composable
private fun HealthChip(

    text: String

) {

    Surface(

        color = Color(0xFFF1F5F9),

        shape = RoundedCornerShape(50)

    ) {

        Text(

            modifier = Modifier.padding(

                horizontal = 14.dp,

                vertical = 8.dp

            ),

            text = text,

            fontSize = 13.sp

        )

    }

}

@Composable
private fun IncomeExpenseCard(

    income: Double,

    expense: Double,

    previousExpense: Double,

    trend: List<Float>

) {

    val difference = expense - previousExpense

    val percentage =

        if (previousExpense == 0.0)

            0

        else

            ((difference / previousExpense) * 100).toInt()

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(24.dp),

        colors = CardDefaults.cardColors(

            containerColor = Color.White

        )

    ) {

        Column(

            modifier = Modifier.padding(22.dp)

        ) {

            Text(

                text = "Income vs Expense",

                style = MaterialTheme.typography.titleMedium,

                fontWeight = FontWeight.Bold

            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceBetween

            ) {

                FinanceInfoCard(

                    title = "Income",

                    amount = income,

                    color = Color(0xFF16A34A)

                )

                FinanceInfoCard(

                    title = "Expense",

                    amount = expense,

                    color = Color(0xFFDC2626)

                )

            }

            Spacer(modifier = Modifier.height(22.dp))

            ExpenseComparisonChip(

                percentage = percentage

            )

            Spacer(modifier = Modifier.height(20.dp))

            ExpenseTrendChart(

                trend = trend

            )

        }

    }

}

@Composable
private fun FinanceInfoCard(

    title: String,

    amount: Double,

    color: Color

) {

    Card(

        shape = RoundedCornerShape(18.dp),

        modifier = Modifier.width(145.dp),

        colors = CardDefaults.cardColors(

            containerColor = Color(0xFFF8FAFC)

        )

    ) {

        Column(

            modifier = Modifier.padding(16.dp)

        ) {

            Text(

                text = title,

                color = Color.Gray,

                fontSize = 13.sp

            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(

                text = "₹%,.0f".format(amount),

                fontWeight = FontWeight.Bold,

                color = color,

                fontSize = 22.sp

            )

        }

    }

}

@Composable
private fun ExpenseComparisonChip(

    percentage: Int

) {

    val isIncrease = percentage > 0

    val bg =

        if (isIncrease)

            Color(0xFFFFEBEE)

        else

            Color(0xFFE8F5E9)

    val textColor =

        if (isIncrease)

            Color(0xFFC62828)

        else

            Color(0xFF2E7D32)

    Surface(

        shape = RoundedCornerShape(50),

        color = bg

    ) {

        Text(

            modifier = Modifier.padding(

                horizontal = 16.dp,

                vertical = 8.dp

            ),

            text =

                if (isIncrease)

                    "▲ ${kotlin.math.abs(percentage)}% higher than last month"

                else

                    "▼ ${kotlin.math.abs(percentage)}% lower than last month",

            color = textColor,

            fontWeight = FontWeight.SemiBold,

            fontSize = 13.sp

        )

    }

}

@Composable
private fun ExpenseTrendChart(

    trend: List<Float>

) {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),

        shape = RoundedCornerShape(18.dp),

        colors = CardDefaults.cardColors(

            containerColor = Color(0xFFF8FAFC)

        )

    ) {

        Box(

            modifier = Modifier.fillMaxSize(),

            contentAlignment = Alignment.Center

        ) {

            Column(

                horizontalAlignment = Alignment.CenterHorizontally

            ) {

                Text(

                    text = "Expense Trend",

                    fontWeight = FontWeight.SemiBold

                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(

                    text = "${trend.size} days tracked",

                    color = Color.Gray

                )

            }

        }

    }

}

@Composable
private fun CategoryBreakdownCard(

    categorySummary: Map<Category, Double>

) {

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(24.dp),

        colors = CardDefaults.cardColors(

            containerColor = Color.White

        )

    ) {

        Column(

            modifier = Modifier.padding(22.dp)

        ) {

            Text(

                text = "Category Breakdown",

                style = MaterialTheme.typography.titleMedium,

                fontWeight = FontWeight.Bold

            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(

                modifier = Modifier.fillMaxWidth(),

                verticalAlignment = Alignment.CenterVertically,

                horizontalArrangement = Arrangement.SpaceBetween

            ) {

                DonutChart(

                    categorySummary = categorySummary

                )

                Spacer(modifier = Modifier.width(16.dp))

                CategoryLegend(

                    categorySummary = categorySummary

                )

            }

        }

    }

}

@Composable
private fun DonutChart(

    categorySummary: Map<Category, Double>

) {

    val colors = listOf(

        Color(0xFF4CAF50),
        Color(0xFF2196F3),
        Color(0xFFFF9800),
        Color(0xFFE91E63),
        Color(0xFF9C27B0),
        Color(0xFF607D8B)

    )

    val total = categorySummary.values.sum()

    Canvas(

        modifier = Modifier.size(160.dp)

    ) {

        if (total == 0.0) {

            drawCircle(

                color = Color.LightGray,

                style = Stroke(24f)

            )

            return@Canvas

        }

        var startAngle = -90f

        categorySummary.values.forEachIndexed { index, amount ->

            val sweep = ((amount / total) * 360f).toFloat()

            drawArc(

                color = colors[index % colors.size],

                startAngle = startAngle,

                sweepAngle = sweep,

                useCenter = false,

                style = Stroke(

                    width = 24f,

                    cap = StrokeCap.Round

                )

            )

            startAngle += sweep

        }

    }

}

@Composable
private fun CategoryLegend(

    categorySummary: Map<Category, Double>

) {

    val colors = listOf(

        Color(0xFF4CAF50),
        Color(0xFF2196F3),
        Color(0xFFFF9800),
        Color(0xFFE91E63),
        Color(0xFF9C27B0),
        Color(0xFF607D8B)

    )

    Column(

        verticalArrangement = Arrangement.spacedBy(12.dp)

    ) {

        categorySummary.entries.forEachIndexed { index, entry ->

            Row(

                verticalAlignment = Alignment.CenterVertically

            ) {

                Box(

                    modifier = Modifier

                        .size(12.dp)

                        .background(

                            colors[index % colors.size],

                            CircleShape

                        )

                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {

                    Text(

                        text = entry.key.name.replace("_", " "),

                        fontWeight = FontWeight.SemiBold,

                        fontSize = 13.sp

                    )

                    Text(

                        text = "₹%,.0f".format(entry.value),

                        color = Color.Gray,

                        fontSize = 12.sp

                    )

                }

            }

        }

    }

}

@Composable
private fun TopCategoriesCard(

    categorySummary: Map<Category, Double>

) {

    val totalExpense = categorySummary.values.sum()

    val sortedCategories = categorySummary.toList()

        .sortedByDescending { it.second }

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(24.dp),

        colors = CardDefaults.cardColors(

            containerColor = Color.White

        )

    ) {

        Column(

            modifier = Modifier.padding(22.dp)

        ) {

            Text(

                text = "Top Spending Categories",

                style = MaterialTheme.typography.titleMedium,

                fontWeight = FontWeight.Bold

            )

            Spacer(modifier = Modifier.height(20.dp))

            if (sortedCategories.isEmpty()) {

                Text(

                    text = "No expenses available.",

                    color = Color.Gray

                )

            } else {

                sortedCategories.take(5).forEachIndexed { index, category ->

                    CategoryProgressItem(

                        rank = index + 1,

                        category = category.first,

                        amount = category.second,

                        totalExpense = totalExpense

                    )

                    if (index != sortedCategories.take(5).lastIndex)

                        Spacer(modifier = Modifier.height(18.dp))

                }

            }

        }

    }

}

@Composable
private fun CategoryProgressItem(

    rank: Int,

    category: Category,

    amount: Double,

    totalExpense: Double

) {

    val progress =

        if (totalExpense == 0.0)

            0f

        else

            (amount / totalExpense).toFloat()

    Column {

        Row(

            modifier = Modifier.fillMaxWidth(),

            horizontalArrangement = Arrangement.SpaceBetween,

            verticalAlignment = Alignment.CenterVertically

        ) {

            Row(

                verticalAlignment = Alignment.CenterVertically

            ) {

                Surface(

                    modifier = Modifier.size(34.dp),

                    shape = CircleShape,

                    color = BrandGreen.copy(alpha = 0.12f)

                ) {

                    Box(

                        contentAlignment = Alignment.Center

                    ) {

                        Text(

                            text = rank.toString(),

                            fontWeight = FontWeight.Bold,

                            color = BrandGreen

                        )

                    }

                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {

                    Text(

                        text = category.name.replace("_", " "),

                        fontWeight = FontWeight.SemiBold

                    )

                    Text(

                        text = "₹%,.0f".format(amount),

                        color = Color.Gray,

                        fontSize = 12.sp

                    )

                }

            }

            Text(

                text = "${(progress * 100).toInt()}%",

                fontWeight = FontWeight.Bold,

                color = BrandGreen

            )

        }

        Spacer(modifier = Modifier.height(10.dp))

        LinearProgressIndicator(

            progress = { progress },

            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),

            color = BrandGreen,

            trackColor = Color(0xFFE9EEF5)

        )

    }
}

@Composable
private fun MonthlyComparisonCard(

    currentExpense: Double,

    previousExpense: Double,

    budget: Double,

    savings: Double,

    savingRate: Int

) {

    val difference = currentExpense - previousExpense

    val percentage =

        if (previousExpense == 0.0)

            0

        else

            ((difference / previousExpense) * 100).toInt()

    val budgetUsed =

        if (budget == 0.0)

            0

        else

            ((currentExpense / budget) * 100).toInt()

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(24.dp),

        colors = CardDefaults.cardColors(

            containerColor = Color.White

        )

    ) {

        Column(

            modifier = Modifier.padding(22.dp)

        ) {

            Text(

                text = "Monthly Comparison",

                style = MaterialTheme.typography.titleMedium,

                fontWeight = FontWeight.Bold

            )

            Spacer(modifier = Modifier.height(22.dp))

            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceEvenly

            ) {

                ComparisonStat(

                    title = "Last Month",

                    value = previousExpense

                )

                ComparisonStat(

                    title = "This Month",

                    value = currentExpense

                )

            }

            Spacer(modifier = Modifier.height(22.dp))

            ComparisonBanner(

                percentage = percentage

            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(

                text = "Budget Usage",

                fontWeight = FontWeight.SemiBold

            )

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(

                progress = { (budgetUsed / 100f).coerceIn(0f, 1f) },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),

                color = BrandGreen,

                trackColor = Color(0xFFE8EDF5)

            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(

                text = "$budgetUsed% of ₹${"%,.0f".format(budget)} budget used",

                color = Color.Gray,

                fontSize = 13.sp

            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceBetween

            ) {

                Column {

                    Text(

                        "Savings",

                        color = Color.Gray,

                        fontSize = 13.sp

                    )

                    Text(
                        "$savingRate%",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = BrandGreen
                    )

                }

                Column(

                    horizontalAlignment = Alignment.End

                ) {

                    Text(

                        "Saving Rate",

                        color = Color.Gray,

                        fontSize = 13.sp

                    )

                    Text(

                        "${savingRate.toInt()}%",

                        fontWeight = FontWeight.Bold,

                        fontSize = 20.sp,

                        color = BrandGreen

                    )

                }

            }

        }

    }

}

@Composable
private fun ComparisonStat(

    title: String,

    value: Double

) {

    Column(

        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Text(

            text = title,

            color = Color.Gray,

            fontSize = 13.sp

        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(

            text = "₹${"%,.0f".format(value)}",

            fontWeight = FontWeight.Bold,

            fontSize = 22.sp

        )

    }

}

@Composable
private fun ComparisonBanner(

    percentage: Int

) {

    val increase = percentage > 0

    val background =

        if (increase)

            Color(0xFFFFF3F3)

        else

            Color(0xFFF2FBF4)

    val color =

        if (increase)

            Color(0xFFD32F2F)

        else

            Color(0xFF2E7D32)

    Surface(

        color = background,

        shape = RoundedCornerShape(16.dp)

    ) {

        Text(

            modifier = Modifier.padding(16.dp),

            text =

                if (increase)

                    "📈 Spending increased by ${kotlin.math.abs(percentage)}% compared to last month."

                else

                    "📉 Spending decreased by ${kotlin.math.abs(percentage)}% compared to last month.",

            color = color,

            fontWeight = FontWeight.SemiBold

        )

    }

}

@Composable
private fun AISpendCoachCard(

    insight: String

) {

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(24.dp),

        colors = CardDefaults.cardColors(

            containerColor = BrandGreen

        )

    ) {

        Column(

            modifier = Modifier.padding(22.dp)

        ) {

            Row(

                verticalAlignment = Alignment.CenterVertically

            ) {

                Surface(

                    shape = CircleShape,

                    color = Color.White.copy(alpha = 0.18f)

                ) {

                    Box(

                        modifier = Modifier
                            .size(46.dp),

                        contentAlignment = Alignment.Center

                    ) {

                        Text(

                            text = "🤖",

                            fontSize = 22.sp

                        )

                    }

                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {

                    Text(

                        text = "AI Spend Coach",

                        color = Color.White,

                        fontWeight = FontWeight.Bold,

                        fontSize = 20.sp

                    )

                    Text(

                        text = "Personalized financial advice",

                        color = Color.White.copy(alpha = 0.85f),

                        fontSize = 13.sp

                    )

                }

            }

            Spacer(modifier = Modifier.height(20.dp))

            Surface(

                shape = RoundedCornerShape(18.dp),

                color = Color.White.copy(alpha = 0.12f)

            ) {

                Text(

                    text = insight,

                    modifier = Modifier.padding(18.dp),

                    color = Color.White,

                    lineHeight = 24.sp,

                    fontSize = 15.sp

                )

            }

        }

    }
}

@Composable
private fun TopMerchantsCard(

    merchants: List<Pair<String, Double>>

) {

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(24.dp),

        colors = CardDefaults.cardColors(

            containerColor = Color.White

        )

    ) {

        Column(

            modifier = Modifier.padding(22.dp)

        ) {

            Text(

                text = "Top Merchants",

                style = MaterialTheme.typography.titleMedium,

                fontWeight = FontWeight.Bold

            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(

                text = "Where most of your money goes",

                color = Color.Gray,

                fontSize = 13.sp

            )

            Spacer(modifier = Modifier.height(20.dp))

            if (merchants.isEmpty()) {

                Text(

                    text = "No merchant data available.",

                    color = Color.Gray

                )

            } else {

                val totalSpent = merchants.sumOf { it.second }

                merchants.take(5).forEachIndexed { index, merchant ->

                    MerchantItem(

                        merchant = merchant.first,

                        amount = merchant.second,

                        percentage = if (totalSpent == 0.0)
                            0f
                        else
                            (merchant.second / totalSpent).toFloat(),

                        rank = index + 1

                    )

                    if (index != merchants.take(5).lastIndex)

                        Spacer(modifier = Modifier.height(18.dp))

                }

            }

        }

    }

}

@Composable
private fun MerchantItem(

    merchant: String,

    amount: Double,

    percentage: Float,

    rank: Int

) {

    Column {

        Row(

            modifier = Modifier.fillMaxWidth(),

            verticalAlignment = Alignment.CenterVertically

        ) {

            Surface(

                modifier = Modifier.size(44.dp),

                shape = CircleShape,

                color = BrandGreen.copy(alpha = 0.12f)

            ) {

                Box(

                    contentAlignment = Alignment.Center

                ) {

                    Text(

                        text = merchant.firstOrNull()?.uppercase() ?: "?",

                        fontWeight = FontWeight.Bold,

                        color = BrandGreen

                    )

                }

            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(

                modifier = Modifier.weight(1f)

            ) {

                Text(

                    text = merchant,

                    fontWeight = FontWeight.SemiBold,

                    maxLines = 1

                )

                Text(

                    text = "Rank #$rank",

                    color = Color.Gray,

                    fontSize = 12.sp

                )

            }

            Column(

                horizontalAlignment = Alignment.End

            ) {

                Text(

                    text = "₹${"%,.0f".format(amount)}",

                    fontWeight = FontWeight.Bold

                )

                Text(

                    text = "${(percentage * 100).toInt()}%",

                    color = BrandGreen,

                    fontSize = 12.sp

                )

            }

        }

        Spacer(modifier = Modifier.height(10.dp))

        LinearProgressIndicator(

            progress = { percentage.coerceIn(0f, 1f) },

            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),

            color = BrandGreen,

            trackColor = Color(0xFFE8EDF5)

        )

    }

}

@Composable
private fun PaymentMethodsCard(

    paymentMethods: Map<PaymentMethod, Double>

) {

    val total = paymentMethods.values.sum()

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(24.dp),

        colors = CardDefaults.cardColors(

            containerColor = Color.White

        )

    ) {

        Column(

            modifier = Modifier.padding(22.dp)

        ) {

            Text(

                text = "Payment Methods",

                style = MaterialTheme.typography.titleMedium,

                fontWeight = FontWeight.Bold

            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(

                text = "How you usually pay",

                color = Color.Gray,

                fontSize = 13.sp

            )

            Spacer(modifier = Modifier.height(20.dp))

            if (paymentMethods.isEmpty()) {

                Text(

                    text = "No payment history available.",

                    color = Color.Gray

                )

            } else {

                paymentMethods.entries
                    .sortedByDescending { it.value }
                    .forEach { entry ->

                        PaymentMethodItem(

                            method = entry.key,

                            amount = entry.value,

                            percentage = if (total == 0.0)
                                0f
                            else
                                (entry.value / total).toFloat()

                        )

                        Spacer(modifier = Modifier.height(18.dp))

                    }

            }

        }

    }

}

@Composable
private fun PaymentMethodItem(

    method: PaymentMethod,

    amount: Double,

    percentage: Float

) {

    val icon = when (method) {

        PaymentMethod.UPI -> "📱"

        PaymentMethod.CREDIT_CARD -> "💳"

        PaymentMethod.DEBIT_CARD -> "🏦"

        PaymentMethod.CASH -> "💵"

        PaymentMethod.WALLET -> "👛"

        else -> "💰"

    }

    Column {

        Row(

            modifier = Modifier.fillMaxWidth(),

            verticalAlignment = Alignment.CenterVertically

        ) {

            Text(

                text = icon,

                fontSize = 28.sp

            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(

                modifier = Modifier.weight(1f)

            ) {

                Text(

                    text = method.name.replace("_", " "),

                    fontWeight = FontWeight.SemiBold

                )

                Text(

                    text = "₹${"%,.0f".format(amount)}",

                    color = Color.Gray,

                    fontSize = 12.sp

                )

            }

            Text(

                text = "${(percentage * 100).toInt()}%",

                color = BrandGreen,

                fontWeight = FontWeight.Bold

            )

        }

        Spacer(modifier = Modifier.height(10.dp))

        LinearProgressIndicator(

            progress = { percentage.coerceIn(0f, 1f) },

            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),

            color = BrandGreen,

            trackColor = Color(0xFFE8EDF5)

        )

    }

}

@Composable
private fun SmartTipsCard(

    healthScore: Int,

    savings: Double,

    expense: Double,

    budget: Double

) {

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(24.dp),

        colors = CardDefaults.cardColors(

            containerColor = Color.White

        )

    ) {

        Column(

            modifier = Modifier.padding(22.dp)

        ) {

            Text(

                text = "Smart Tips",

                style = MaterialTheme.typography.titleMedium,

                fontWeight = FontWeight.Bold

            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(

                text = "Recommendations based on your spending",

                color = Color.Gray,

                fontSize = 13.sp

            )

            Spacer(modifier = Modifier.height(22.dp))

            SmartTipItem(

                icon = "💰",

                title = "Budget Status",

                description =

                    if (expense <= budget)

                        "You're within your monthly budget. Great job!"

                    else

                        "You've exceeded your budget. Try reducing discretionary spending."

            )

            Spacer(modifier = Modifier.height(16.dp))

            SmartTipItem(

                icon = "📈",

                title = "Financial Health",

                description =

                    if (healthScore >= 80)

                        "Excellent financial health. Keep maintaining this habit."

                    else if (healthScore >= 60)

                        "You're doing well. A little more saving can improve your score."

                    else

                        "Focus on reducing unnecessary expenses to improve your financial health."

            )

            Spacer(modifier = Modifier.height(16.dp))

            SmartTipItem(

                icon = "🏦",

                title = "Savings",

                description =

                    if (savings > 0)

                        "You've saved ₹${"%,.0f".format(savings)} this month."

                    else

                        "Try setting aside a small amount every week to build savings."

            )

        }

    }

}

@Composable
private fun SmartTipItem(

    icon: String,

    title: String,

    description: String

) {

    Row(

        verticalAlignment = Alignment.Top

    ) {

        Surface(

            modifier = Modifier.size(44.dp),

            shape = CircleShape,

            color = BrandGreen.copy(alpha = 0.12f)

        ) {

            Box(

                contentAlignment = Alignment.Center

            ) {

                Text(

                    text = icon,

                    fontSize = 22.sp

                )

            }

        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(

            modifier = Modifier.weight(1f)

        ) {

            Text(

                text = title,

                fontWeight = FontWeight.Bold,

                fontSize = 16.sp

            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(

                text = description,

                color = Color.Gray,

                lineHeight = 20.sp

            )

        }

    }

}