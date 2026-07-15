package com.example.spendmend.screens

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spendmend.core.SmsImporter
import com.example.spendmend.data.Transaction
import com.example.spendmend.data.model.BudgetStatus
import com.example.spendmend.data.model.Category
import com.example.spendmend.data.model.TransactionType
import com.example.spendmend.screens.components.BudgetBottomSheet
import com.example.spendmend.ui.state.BudgetUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun getMonthName(month: Int): String {

    return java.text.DateFormatSymbols()
        .months[month]

}

@Composable
fun HomeScreen(
    viewModel: TransactionViewModel = viewModel()
) {

    val income by viewModel.currentMonthIncome.collectAsState()

    val expense by viewModel.currentMonthExpense.collectAsState()

    val savings by viewModel.currentMonthSavings.collectAsState()

    val monthTransactions by viewModel.currentMonthTransactions.collectAsState()

    val categorySummary by viewModel.categorySummary.collectAsState()

    val selectedMonth by viewModel.selectedMonth.collectAsState()

    val selectedYear by viewModel.selectedYear.collectAsState()

    val budgetState by viewModel.budgetUiState.collectAsState()

    var showBudgetSheet by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    val smsPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {

                CoroutineScope(Dispatchers.IO).launch {

                    SmsImporter.importExistingSms(context)

                }

            }

        }



    LaunchedEffect(Unit) {

        smsPermissionLauncher.launch(
            Manifest.permission.READ_SMS
        )

    }

    LaunchedEffect(monthTransactions) {

        Log.d(
            "SpendMend",
            "Transactions = ${monthTransactions.size}"
        )

    }

    if (showBudgetSheet) {

        BudgetBottomSheet(

            currentBudget = budgetState.budget,

            spent = budgetState.spent,

            onDismiss = {
                showBudgetSheet = false
            },

            onSave = { amount ->

                viewModel.saveCurrentMonthBudget(amount)

                showBudgetSheet = false

            }

        )

    }

    LazyColumn(

        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7FB))
            .statusBarsPadding(),

        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 16.dp,
            bottom = 120.dp
        ),

        verticalArrangement = Arrangement.spacedBy(18.dp)

    ) {

        item {

            GreetingSection()
            MonthSelector(
                month = selectedMonth,
                year = selectedYear,
                onPrevious = {
                    viewModel.previousMonth()
                },
                onNext = {
                    viewModel.nextMonth()
                }
            )

        }

        item {

            BalanceCard(
                savings = savings,
                income = income,
                expense = expense
            )

        }

        item {

            BudgetCard(
                budgetState = budgetState,
                onClick = {
                    showBudgetSheet = true
                }
            )
        }

        item {

            AIInsightCard(
                spent = expense,
                budget = budgetState.budget,
                categorySummary = categorySummary
            )

        }

        item {

            HighestCategoryCard(

                categorySummary = categorySummary

            )

        }

        item {

            QuickStatsCard(

                transactions = monthTransactions.size,

                totalExpense = expense,

                categories = categorySummary.size

            )

        }

        if (budgetState.status != BudgetStatus.SAFE) {

            item {

                BudgetWarningBanner(
                    spent = budgetState.spent,
                    budget = budgetState.budget
                )

            }

        }

        item {

            if (monthTransactions.isEmpty()) {

                EmptyMonthState()

            } else {

                RecentTransactions(
                    transactions = monthTransactions
                )

            }

        }

    }

}

@Composable
private fun GreetingSection() {

    Column {

        Text(
            text = "Good Evening 👋",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    "SpendMend",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Track every rupee smarter",
                    color = Color.Gray
                )

            }

            Card(
                shape = CircleShape
            ) {

                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp)
                )

            }

        }

    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MonthSelector(
    month: Int,
    year: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onMonthClick: () -> Unit = {}
) {

    val monthName = remember(month) {
        DateFormatSymbols().months[month]
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                FilledIconButton(
                    onClick = onPrevious,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color(0xFFEAF7EF)
                    )
                ) {

                    Icon(
                        imageVector = Icons.Rounded.ChevronLeft,
                        contentDescription = "Previous Month",
                        tint = Color(0xFF239947)
                    )

                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            onMonthClick()
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    AnimatedContent(
                        targetState = "$monthName $year",
                        label = "MonthAnimation"
                    ) { target ->

                        Text(
                            text = target,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )

                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.Gray
                    )

                }

                Spacer(modifier = Modifier.weight(1f))

                FilledIconButton(
                    onClick = onNext,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color(0xFFEAF7EF)
                    )
                ) {

                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = "Next Month",
                        tint = Color(0xFF239947)
                    )

                }

            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Monthly Overview",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

        }

    }

}

@Composable
private fun BalanceCard(

    savings: Double,

    income: Double,

    expense: Double

) {

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(30.dp),

        colors = CardDefaults.cardColors(

            containerColor = Color(0xFF239947)

        )

    ) {

        Column(

            modifier = Modifier.padding(24.dp)

        ) {

            Text(

                "This Month Savings",

                color = Color.White.copy(.75f)

            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(

                "₹ ${"%,.0f".format(savings)}",

                color = Color.White,

                style = MaterialTheme.typography.headlineLarge,

                fontWeight = FontWeight.Bold

            )

            Spacer(modifier = Modifier.height(28.dp))

            Row {

                BalanceItem(

                    title = "Income",

                    amount = income,

                    icon = Icons.Outlined.ArrowDownward,

                    color = Color.White,

                    modifier = Modifier.weight(1f)

                )

                BalanceItem(

                    title = "Expense",

                    amount = expense,

                    icon = Icons.Outlined.ArrowUpward,

                    color = Color.White,

                    modifier = Modifier.weight(1f)

                )

            }

        }

    }

}

@Composable
private fun BalanceItem(

    modifier: Modifier = Modifier,

    title: String,

    amount: Double,

    icon: ImageVector,

    color: Color

) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {

            Text(
                title,
                color = color.copy(.8f)
            )

            Text(
                "₹${"%,.0f".format(amount)}",
                color = color,
                fontWeight = FontWeight.Bold
            )

        }

    }
}


@Composable
private fun QuickStatsCard(

    transactions: Int,

    totalExpense: Double,

    categories: Int

) {

    Row(

        horizontalArrangement = Arrangement.spacedBy(12.dp)

    ) {

        SmallStatCard(

            title = "Expenses",

            value = "₹${totalExpense.toInt()}",

            modifier = Modifier.weight(1f)

        )

        SmallStatCard(

            title = "Transactions",

            value = transactions.toString(),

            modifier = Modifier.weight(1f)

        )

        SmallStatCard(

            title = "Categories",

            value = categories.toString(),

            modifier = Modifier.weight(1f)

        )

    }

}

@Composable
private fun SmallStatCard(

    modifier: Modifier = Modifier,

    title: String,

    value: String

) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                value,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                title,
                color = Color.Gray
            )

        }

    }

}

@Composable
private fun BudgetCard(
    budgetState: BudgetUiState,
    onClick: () -> Unit
) {

    val statusColor = when (budgetState.status) {

        BudgetStatus.SAFE ->
            Color(0xFF239947)

        BudgetStatus.WARNING ->
            Color(0xFFFF9800)

        BudgetStatus.EXCEEDED ->
            Color(0xFFE53935)

    }

    val statusText = when (budgetState.status) {

        BudgetStatus.SAFE ->
            "Healthy"

        BudgetStatus.WARNING ->
            "Warning"

        BudgetStatus.EXCEEDED ->
            "Exceeded"

    }

    Card(
        onClick = onClick,

        shape = RoundedCornerShape(28.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier.padding(22.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    "Monthly Budget",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.weight(1f))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = statusColor.copy(.15f)
                    )
                ) {

                    Text(
                        statusText,
                        color = statusColor,
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        )
                    )

                }

            }

            Spacer(modifier = Modifier.height(20.dp))

            LinearProgressIndicator(
                progress = { budgetState.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color = statusColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "${budgetState.percentage}% Used",
                color = statusColor,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text("Spent")

                    Text(
                        "₹${"%,.0f".format(budgetState.spent)}",
                        fontWeight = FontWeight.Bold
                    )

                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text("Remaining")

                    Text(
                        "₹${"%,.0f".format(budgetState.remaining)}",
                        fontWeight = FontWeight.Bold
                    )

                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text("Budget")

                    Text(
                        "₹${"%,.0f".format(budgetState.budget)}",
                        fontWeight = FontWeight.Bold
                    )

                }

            }

        }

    }
}

@Composable
private fun AIInsightCard(
    spent: Double,
    budget: Double,
    categorySummary: Map<Category, Double>
) {

    val topCategory =
        categorySummary.maxByOrNull { it.value }

    val progress =
        if (budget == 0.0)
            0.0
        else
            (spent / budget)

    val insight = when {

        spent == 0.0 ->
            "Start tracking expenses to receive AI-powered insights."

        progress >= 1 ->
            "You've exceeded your monthly budget. Try reducing discretionary spending."

        topCategory != null ->
            "${topCategory.key.name.replace("_", " ")} is your highest spending category this month."

        else ->
            "You're managing your spending well."

    }

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEAF7EF)
        )
    ) {

        Row(
            modifier = Modifier.padding(20.dp)
        ) {

            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {

                Icon(
                    Icons.Outlined.Lightbulb,
                    null,
                    tint = Color(0xFF239947),
                    modifier = Modifier.padding(10.dp)
                )

            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {

                Text(
                    "Smart Insight",
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    insight
                )

            }

        }

    }

}

@Composable
private fun TransactionCard(
    transaction: Transaction
) {

    val isExpense =
        transaction.transactionType == TransactionType.EXPENSE

    val formattedDate = remember(transaction.date) {
        SimpleDateFormat(
            "dd MMM • hh:mm a",
            Locale.getDefault()
        ).format(Date(transaction.date))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        color =
                            if (isExpense)
                                Color(0xFFFFF1F1)
                            else
                                Color(0xFFEAFBF0),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text =
                        if (isExpense)
                            "💸"
                        else
                            "💰",
                    style = MaterialTheme.typography.titleLarge
                )

            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = transaction.merchant,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = buildString {

                        append(
                            transaction.category.name
                                .replace("_", " ")
                                .lowercase()
                                .replaceFirstChar {
                                    it.titlecase()
                                }
                        )

                        append(" • ")

                        append(transaction.bankName.name)

                        append(" • ")

                        append(transaction.paymentMethod.name)

                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

            }

            Column(
                horizontalAlignment = Alignment.End
            ) {

                Text(
                    text =
                        if (isExpense)
                            "- ₹${"%,.2f".format(transaction.amount)}"
                        else
                            "+ ₹${"%,.2f".format(transaction.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color =
                        if (isExpense)
                            Color(0xFFE53935)
                        else
                            Color(0xFF239947)
                )

                if (transaction.isAutoDetected) {

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "SMS",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF239947)
                    )

                }

            }

        }

    }

}

@Composable
private fun BudgetWarningBanner(

    spent: Double,

    budget: Double

) {

    val exceeded = spent >= budget

    val color =
        if (exceeded)
            Color(0xFFE53935)
        else
            Color(0xFFFF9800)

    Card(

        colors = CardDefaults.cardColors(

            containerColor = color.copy(.10f)

        ),

        shape = RoundedCornerShape(24.dp)

    ) {

        Row(

            modifier = Modifier.padding(20.dp),

            verticalAlignment = Alignment.CenterVertically

        ) {

            Text(
                text = if (exceeded) "🚨" else "⚠"
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column {

                Text(

                    if (exceeded)
                        "Budget Exceeded"
                    else
                        "Budget Warning",

                    fontWeight = FontWeight.Bold,

                    color = color

                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(

                    if (exceeded)
                        "You have exceeded your monthly budget."
                    else
                        "You've already used 80% of your monthly budget."

                )

            }

        }

    }

}

@Composable
private fun HighestCategoryCard(

    categorySummary: Map<Category, Double>

) {

    val highest =
        categorySummary.maxByOrNull { it.value }

    Card(
        shape = RoundedCornerShape(28.dp)
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(

                "Top Spending",

                fontWeight = FontWeight.Bold

            )

            Spacer(modifier = Modifier.height(16.dp))

            if (highest == null) {

                Text("No data available")

            } else {

                Text(

                    highest.key.name
                        .replace("_", " "),

                    style = MaterialTheme.typography.titleLarge

                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(

                    "₹${highest.value.toInt()}",

                    color = Color.Gray

                )

            }

        }

    }

}

@Composable
private fun RecentTransactions(
    transactions: List<Transaction>
) {

    Column {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            TextButton(
                onClick = {
                    // TODO Navigate to all transactions
                }
            ) {

                Text("View All")

            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        if (transactions.isEmpty()) {

            EmptyTransactions()

        } else {

            transactions
                .sortedByDescending { it.date }
                .take(5)
                .forEachIndexed { index, transaction ->

                    TransactionCard(transaction)

                    if (index != 4 && index != transactions.lastIndex) {

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                    }

                }

        }

    }

}

@Composable
private fun EmptyTransactions() {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "📊",
                style = MaterialTheme.typography.displayMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No Transactions Yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your SMS expenses will automatically appear here.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

        }

    }


}

@Composable
private fun EmptyMonthState() {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "📅",
                style = MaterialTheme.typography.displayMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "No Transactions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "No transactions were found for this month.",
                color = Color.Gray
            )

        }

    }

}