package com.example.spendmend.screens

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.spendmend.core.SmsImporter
import com.example.spendmend.data.Transaction
import com.example.spendmend.data.model.Category
import com.example.spendmend.screens.components.BudgetBottomSheet
import com.example.spendmend.ui.state.BudgetUiState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols
import java.util.Calendar
import com.example.spendmend.data.model.TransactionType
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.snapshotFlow


private fun getMonthName(month: Int): String {
    return DateFormatSymbols().months[month]
}

@Composable
fun HomeScreen(
    navController: NavHostController,
    onBottomBarVisibilityChanged: (Boolean) -> Unit,
    viewModel: TransactionViewModel = viewModel()
) {

    // ---------------------------
    // State
    // ---------------------------

    val income by viewModel.currentMonthIncome.collectAsState()

    val expense by viewModel.currentMonthExpense.collectAsState()

    val savings by viewModel.currentMonthSavings.collectAsState()

    val monthTransactions by
    viewModel.currentMonthTransactions.collectAsState()

    val categorySummary by
    viewModel.categorySummary.collectAsState()

    val selectedMonth by
    viewModel.selectedMonth.collectAsState()

    val selectedYear by
    viewModel.selectedYear.collectAsState()

    val budgetState by
    viewModel.budgetUiState.collectAsState()

    var showBudgetSheet by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    // ---------------------------
    // SMS Permission
    // ---------------------------

    val smsPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
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

    // ---------------------------
    // Budget Bottom Sheet
    // ---------------------------

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

    // ---------------------------
    // Home Content
    // ---------------------------

    LazyColumn(

        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7FB))
            .statusBarsPadding(),

        verticalArrangement = Arrangement.spacedBy(18.dp),

        contentPadding = PaddingValues(

            start = 20.dp,

            end = 20.dp,

            top = 16.dp,

            bottom = 120.dp

        ),

        state = listState,

    ) {

        item {

            GreetingSection(navController)

            Spacer(modifier = Modifier.height(16.dp))

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
                categorySummary
            )

        }

        item {

            QuickStatsCard(

                transactions = monthTransactions.size,

                totalExpense = expense,

                categories = categorySummary.size

            )

        }

        item {

            RecentTransactions(

                transactions = monthTransactions,

                onViewAll = {

                    // navController.navigate("history")

                }

            )

        }

    }

}

private fun getGreeting(): String {

    return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {

        in 5..11 -> "Good Morning"

        in 12..16 -> "Good Afternoon"

        in 17..20 -> "Good Evening"

        else -> "Good Night"

    }

}

private fun getUserName(): String {

    val user = FirebaseAuth.getInstance().currentUser

    return when {

        !user?.displayName.isNullOrBlank() ->
            user.displayName!!

        !user?.email.isNullOrBlank() ->
            user.email!!.substringBefore("@")

        else ->
            "User"

    }

}

@Composable
private fun GreetingSection(
    navController: NavHostController
) {

    Row(

        modifier = Modifier.fillMaxWidth(),

        verticalAlignment = Alignment.CenterVertically

    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(

                text = "${getGreeting()} 👋",

                style = MaterialTheme.typography.bodyLarge,

                color = Color.Gray

            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(

                text = getUserName(),

                style = MaterialTheme.typography.headlineLarge,

                fontWeight = FontWeight.Bold

            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(

                text = "Track every rupee smarter",

                style = MaterialTheme.typography.bodyMedium,

                color = Color.Gray

            )

        }

        FilledIconButton(

            onClick = {

                // navController.navigate("notifications")

            },

            colors = IconButtonDefaults.filledIconButtonColors(

                containerColor = Color.White

            )

        ) {

            Icon(

                Icons.Outlined.Notifications,

                null,

                tint = Color(0xFF239947)

            )

        }

        Spacer(modifier = Modifier.width(10.dp))

        Card(

            modifier = Modifier.size(52.dp),

            shape = CircleShape,

            colors = CardDefaults.cardColors(

                containerColor = Color.White

            )

        ) {

            Box(

                modifier = Modifier
                    .fillMaxSize()
                    .clickable {

                        // navController.navigate("profile")

                    },

                contentAlignment = Alignment.Center

            ) {

                Icon(

                    Icons.Outlined.AccountCircle,

                    null,

                    modifier = Modifier.fillMaxSize(),

                    tint = Color(0xFF239947)

                )

            }

        }

    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun MonthSelector(

    month: Int,

    year: Int,

    onPrevious: () -> Unit,

    onNext: () -> Unit,

    onMonthClick: () -> Unit = {}

) {

    val monthName = remember(month) {

        getMonthName(month)

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

            modifier = Modifier.padding(
                horizontal = 20.dp,
                vertical = 18.dp
            )

        ) {

            Text(

                text = "Monthly Overview",

                style = MaterialTheme.typography.labelLarge,

                color = Color.Gray

            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                FilledIconButton(

                    onClick = onPrevious,

                    colors = IconButtonDefaults.filledIconButtonColors(

                        containerColor = Color(0xFFEAF7EF)

                    )

                ) {

                    Icon(

                        Icons.Rounded.ChevronLeft,

                        null,

                        tint = Color(0xFF239947)

                    )

                }

                Spacer(modifier = Modifier.weight(1f))

                Row(

                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .clickable {

                            onMonthClick()

                        }
                        .padding(
                            horizontal = 14.dp,
                            vertical = 8.dp
                        ),

                    verticalAlignment = Alignment.CenterVertically

                ) {

                    AnimatedContent(

                        targetState = "$monthName $year",

                        label = ""

                    ) {

                        Text(

                            text = it,

                            style = MaterialTheme.typography.titleLarge,

                            fontWeight = FontWeight.SemiBold

                        )

                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(

                        Icons.Rounded.KeyboardArrowDown,

                        null,

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

                        Icons.Rounded.ChevronRight,

                        null,

                        tint = Color(0xFF239947)

                    )

                }

            }

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
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Column(
            modifier = Modifier.padding(24.dp)
        ) {

            Text(
                text = "This Month Savings",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "₹ ${"%,.0f".format(savings)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                BalanceItem(
                    modifier = Modifier.weight(1f),
                    title = "Income",
                    amount = income,
                    icon = Icons.Outlined.ArrowDownward,
                    color = Color.White
                )

                BalanceItem(
                    modifier = Modifier.weight(1f),
                    title = "Expense",
                    amount = expense,
                    icon = Icons.Outlined.ArrowUpward,
                    color = Color.White
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

        Card(
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.15f)
            )
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.padding(10.dp)
            )

        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = color.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "₹ ${"%,.0f".format(amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
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
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        SmallStatCard(
            modifier = Modifier.weight(1f),
            title = "Expense",
            value = "₹${"%,.0f".format(totalExpense)}"
        )

        SmallStatCard(
            modifier = Modifier.weight(1f),
            title = "Transactions",
            value = transactions.toString()
        )

        SmallStatCard(
            modifier = Modifier.weight(1f),
            title = "Categories",
            value = categories.toString()
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
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
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

    val progress =
        if (budgetState.budget <= 0)
            0f
        else
            (budgetState.spent / budgetState.budget)
                .coerceIn(0.0, 1.0)
                .toFloat()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {

        Column(
            modifier = Modifier.padding(22.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Monthly Budget",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                TextButton(
                    onClick = onClick
                ) {
                    Text("Edit")
                }

            }

            Spacer(modifier = Modifier.height(18.dp))

            BudgetInfoItem(
                title = "Budget",
                value = "₹${"%,.0f".format(budgetState.budget)}"
            )

            Spacer(modifier = Modifier.height(12.dp))

            BudgetInfoItem(
                title = "Spent",
                value = "₹${"%,.0f".format(budgetState.spent)}"
            )

            Spacer(modifier = Modifier.height(12.dp))

            BudgetInfoItem(
                title = "Remaining",
                value = "₹${"%,.0f".format(budgetState.remaining)}"
            )

            Spacer(modifier = Modifier.height(20.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {

                budgetState.budget <= 0 -> {

                    BudgetWarningBanner(
                        "No budget set for this month."
                    )

                }

                budgetState.remaining <= 0 -> {

                    BudgetWarningBanner(
                        "Budget exceeded!"
                    )

                }

                progress >= 0.90f -> {

                    BudgetWarningBanner(
                        "You've used more than 90% of your budget."
                    )

                }

            }

        }

    }

}

@Composable
private fun BudgetInfoItem(
    title: String,
    value: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = title,
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = value,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyLarge
        )

    }

}

@Composable
private fun BudgetWarningBanner(
    message: String
) {

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF3CD)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {

        Text(
            text = message,
            modifier = Modifier.padding(14.dp),
            color = Color(0xFF8A6D3B),
            style = MaterialTheme.typography.bodyMedium
        )

    }

}

@Composable
private fun AIInsightCard(
    spent: Double,
    budget: Double,
    categorySummary: Map<Category, Double>
){

    val topCategory = categorySummary.maxByOrNull { it.value }

    val insight = remember(spent, budget, categorySummary) {

        when {

            budget <= 0 ->
                "Set a monthly budget to unlock personalized spending insights."

            spent == 0.0 ->
                "No expenses recorded this month. Great time to start tracking!"

            spent > budget ->
                "You've exceeded your monthly budget. Consider reducing discretionary spending."

            spent >= budget * 0.9 ->
                "You're close to your budget limit. Spend carefully for the rest of the month."

            topCategory != null ->
                "Most of your spending is on ${topCategory.key}. Review if you can optimize this category."

            else ->
                "You're managing your finances well. Keep tracking your expenses."
        }

    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F2937)
        )
    ) {

        Column(
            modifier = Modifier.padding(22.dp)
        ) {

            Text(
                text = "AI Spending Insight",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = insight,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.9f)
            )

        }

    }

}

@Composable
private fun HighestCategoryCard(
    categorySummary: Map<Category, Double>
) {

    val topCategory = categorySummary.maxByOrNull { it.value }

    if (topCategory == null) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {

            Text(
                text = "No spending categories available.",
                modifier = Modifier.padding(22.dp),
                style = MaterialTheme.typography.bodyLarge
            )

        }

        return

    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {

        Column(
            modifier = Modifier.padding(22.dp)
        ) {

            Text(
                text = "Highest Spending Category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = topCategory.key.name.replace("_", " "),
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF239947),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "₹${"%,.0f".format(topCategory.value)}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(18.dp))

            LinearProgressIndicator(
                progress = {
                    val total = categorySummary.values.sum()

                    if (total == 0.0)
                        0f
                    else
                        (topCategory.value / total).toFloat()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "${((topCategory.value / categorySummary.values.sum()) * 100).toInt()}% of this month's spending",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

        }

    }

}

@Composable
private fun RecentTransactions(
    transactions: List<Transaction>,
    onViewAll: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                TextButton(
                    onClick = onViewAll
                ) {

                    Text("View All")

                }

            }

            Spacer(modifier = Modifier.height(12.dp))

            if (transactions.isEmpty()) {

                EmptyTransactions()

            } else {

                transactions
                    .take(5)
                    .forEach {

                        TransactionCard(it)

                    }

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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Card(
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor =
                    if (isExpense)
                        Color(0xFFFFEBEE)
                    else
                        Color(0xFFE8F5E9)
            )
        ) {

            Icon(
                imageVector =
                    if (isExpense)
                        Icons.Outlined.ArrowUpward
                    else
                        Icons.Outlined.ArrowDownward,
                contentDescription = null,
                tint =
                    if (isExpense)
                        Color.Red
                    else
                        Color(0xFF239947),
                modifier = Modifier.padding(12.dp)
            )

        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = transaction.merchant,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = transaction.category.name.replace("_", " "),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

        }

        Column(
            horizontalAlignment = Alignment.End
        ) {

            Text(
                text = "₹${"%,.0f".format(transaction.amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color =
                    if (isExpense)
                        Color.Red
                    else
                        Color(0xFF239947)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = formatDate(transaction.date),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

        }

    }

}

private fun formatDate(time: Long): String {

    val formatter = java.text.SimpleDateFormat(
        "dd MMM",
        java.util.Locale.getDefault()
    )

    return formatter.format(java.util.Date(time))
}

@Composable
private fun EmptyTransactions() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = Icons.Outlined.ReceiptLong,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(60.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "No transactions yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Your recent transactions will appear here.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

    }

}