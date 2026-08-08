package com.example.spendmend.screens

import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.LocalGasStation
import androidx.compose.material.icons.rounded.LocalGroceryStore
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Subscriptions
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spendmend.data.Transaction
import com.example.spendmend.data.model.BudgetStatus
import com.example.spendmend.data.model.Category
import com.example.spendmend.data.model.TransactionType
import com.example.spendmend.ui.theme.BrandGreen
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun TransactionScreen(
    viewModel: TransactionViewModel = viewModel(),
    onBottomBarVisibilityChanged: (Boolean) -> Unit,
) {

    val month by viewModel.selectedMonth.collectAsState()
    val year by viewModel.selectedYear.collectAsState()

    val transactions by viewModel.currentMonthTransactions.collectAsState()
    val income by viewModel.currentMonthIncome.collectAsState()
    val expense by viewModel.currentMonthExpense.collectAsState()
    val savings by viewModel.currentMonthSavings.collectAsState()
    val totalTransactions by viewModel.totalTransactions.collectAsState()
    val budgetState by viewModel.budgetUiState.collectAsState()

    var searchQuery by rememberSaveable {
        mutableStateOf("")
    }

    var selectedFilter by rememberSaveable {
        mutableStateOf("All")
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FB)),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 28.dp,
            bottom = 120.dp
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp),

        state = listState,
    ) {

        item {
            TransactionsHeader(
                selectedMonth = month,
                selectedYear = year,
                onPrevious = { viewModel.previousMonth() },
                onNext = { viewModel.nextMonth() }
            )
        }

        item {
            SearchAndFilterSection(
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )
        }

        item {
            SummarySection(
                income = income,
                expense = expense,
                savings = savings,
                totalTransactions = totalTransactions
            )
        }

        item {
            BudgetOverviewCard(
                budget = budgetState.budget,
                spent = budgetState.spent,
                remaining = budgetState.remaining,
                progress = budgetState.progress,
                percentage = budgetState.percentage,
                status = budgetState.status
            )
        }

        item {
            Text(
                text = "Recent Transactions",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        items(
            items = transactions,
            key = { transaction -> transaction.id }
        ) { transaction ->

            TransactionItem(
                transaction = transaction
            )

        }
    }
}

@Composable
private fun TransactionsHeader(

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

            Column {

                Text(

                    text = "Transactions",

                    fontSize = 32.sp,

                    fontWeight = FontWeight.ExtraBold,

                    color = BrandGreen

                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(

                    text = "Your complete spending history",

                    fontSize = 15.sp,

                    color = Color.Gray

                )

            }

            Surface(

                shape = CircleShape,

                color = Color.White,

                shadowElevation = 6.dp

            ) {

                IconButton(
                    onClick = {}
                ) {

                    Icon(

                        imageVector = Icons.Outlined.Notifications,

                        contentDescription = null,

                        tint = BrandGreen,

                        modifier = Modifier.size(22.dp)

                    )

                }

            }

        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(

            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(20.dp),

            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )

        ) {

            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 14.dp
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
                        text = monthName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = selectedYear.toString(),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchAndFilterSection(

    searchQuery: String,

    onSearchChange: (String) -> Unit,

    selectedFilter: String,

    onFilterSelected: (String) -> Unit

) {

    Column {

        OutlinedTextField(

            value = searchQuery,

            onValueChange = onSearchChange,

            modifier = Modifier.fillMaxWidth(),

            singleLine = true,

            placeholder = {

                Text("Search transactions...")

            },

            leadingIcon = {

                Icon(

                    imageVector = Icons.Default.Search,

                    contentDescription = null,

                    tint = Color.Gray

                )

            },

            shape = RoundedCornerShape(18.dp),

            colors = OutlinedTextFieldDefaults.colors(

                focusedContainerColor = Color.White,

                unfocusedContainerColor = Color.White,

                focusedBorderColor = BrandGreen,

                unfocusedBorderColor = Color(0xFFE5E7EB)

            )

        )

        Spacer(modifier = Modifier.height(18.dp))

        val filters = listOf(

            "All",
            "Expense",
            "Income"

        )

        LazyRow(

            horizontalArrangement = Arrangement.spacedBy(12.dp)

        ) {

            items(filters) { filter ->

                FilterChip(

                    selected = selectedFilter == filter,

                    onClick = {

                        onFilterSelected(filter)

                    },

                    label = {

                        Text(filter)

                    }

                )

            }

            item {

                AssistChip(

                    onClick = { },

                    label = {

                        Text("Category")

                    },

                    trailingIcon = {

                        Icon(

                            imageVector = Icons.Rounded.KeyboardArrowDown,

                            contentDescription = null

                        )

                    }

                )

            }

            item {

                AssistChip(

                    onClick = { },

                    label = {

                        Text("Bank")

                    },

                    trailingIcon = {

                        Icon(

                            imageVector = Icons.Rounded.KeyboardArrowDown,

                            contentDescription = null

                        )

                    }

                )

            }

        }

    }

}

@Composable
private fun SummarySection(

    income: Double,

    expense: Double,

    savings: Double,

    totalTransactions: Int

) {

    Column(

        verticalArrangement = Arrangement.spacedBy(16.dp)

    ) {

        Row(

            horizontalArrangement = Arrangement.spacedBy(16.dp)

        ) {

            SummaryCard(

                modifier = Modifier.weight(1f),

                title = "Income",

                value = "₹${income.toInt()}",

                valueColor = Color(0xFF2E7D32)

            )

            SummaryCard(

                modifier = Modifier.weight(1f),

                title = "Expense",

                value = "₹${expense.toInt()}",

                valueColor = Color(0xFFC62828)

            )

        }

        Row(

            horizontalArrangement = Arrangement.spacedBy(16.dp)

        ) {

            SummaryCard(

                modifier = Modifier.weight(1f),

                title = "Savings",

                value = "₹${savings.toInt()}",

                valueColor = BrandGreen

            )

            SummaryCard(

                modifier = Modifier.weight(1f),

                title = "Transactions",

                value = totalTransactions.toString(),

                valueColor = Color(0xFF1565C0)

            )

        }

    }

}

@Composable
private fun SummaryCard(

    modifier: Modifier = Modifier,

    title: String,

    value: String,

    valueColor: Color

) {

    Card(

        modifier = modifier,

        shape = RoundedCornerShape(18.dp),

        colors = CardDefaults.cardColors(

            containerColor = Color.White

        ),

        elevation = CardDefaults.cardElevation(

            defaultElevation = 3.dp

        )

    ) {

        Column(

            modifier = Modifier.padding(18.dp)

        ) {

            Text(

                text = title,

                fontSize = 14.sp,

                color = Color.Gray

            )

            Spacer(

                modifier = Modifier.height(10.dp)

            )

            Text(

                text = value,

                fontSize = 22.sp,

                fontWeight = FontWeight.Bold,

                color = valueColor

            )

        }

    }

}

@Composable
private fun BudgetOverviewCard(

    budget: Double,

    spent: Double,

    remaining: Double,

    progress: Float,

    percentage: Int,

    status: BudgetStatus

) {

    val statusColor = when (status) {

        BudgetStatus.SAFE -> Color(0xFF2E7D32)

        BudgetStatus.WARNING -> Color(0xFFF9A825)

        BudgetStatus.EXCEEDED -> Color(0xFFC62828)

    }

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(

            containerColor = Color.White

        ),

        elevation = CardDefaults.cardElevation(

            defaultElevation = 3.dp

        )

    ) {

        Column(

            modifier = Modifier.padding(20.dp)

        ) {

            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceBetween,

                verticalAlignment = Alignment.CenterVertically

            ) {

                Column {

                    Text(

                        text = "Monthly Budget",

                        fontSize = 18.sp,

                        fontWeight = FontWeight.Bold

                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(

                        text = "₹${budget.toInt()}",

                        color = Color.Gray

                    )

                }

                Surface(

                    shape = RoundedCornerShape(50),

                    color = statusColor.copy(alpha = 0.12f)

                ) {

                    Text(

                        text = status.name,

                        modifier = Modifier.padding(

                            horizontal = 14.dp,

                            vertical = 6.dp

                        ),

                        color = statusColor,

                        fontWeight = FontWeight.Bold

                    )

                }

            }

            Spacer(modifier = Modifier.height(20.dp))

            LinearProgressIndicator(

                progress = { progress },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),

                color = statusColor,

                trackColor = Color(0xFFEAEAEA)

            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceBetween

            ) {

                BudgetInfo(

                    "Spent",

                    "₹${spent.toInt()}"

                )

                BudgetInfo(

                    "Remaining",

                    "₹${remaining.toInt()}"

                )

                BudgetInfo(

                    "Used",

                    "$percentage%"

                )

            }

        }

    }

}

@Composable
private fun BudgetInfo(

    title: String,

    value: String

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

            text = value,

            fontSize = 18.sp,

            fontWeight = FontWeight.Bold

        )

    }

}

@SuppressLint("DefaultLocale")
@Composable
private fun TransactionItem(

    transaction: Transaction,

    onClick: (() -> Unit)? = null

) {

    val isIncome =
        transaction.transactionType == TransactionType.INCOME

    val amountColor =
        if (isIncome)
            Color(0xFF2E7D32)
        else
            Color(0xFFD32F2F)

    val amountPrefix =
        if (isIncome) "+ ₹" else "- ₹"

    val iconBackground =
        if (isIncome)
            Color(0xFFE8F5E9)
        else
            Color(0xFFE8F5E9)

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(22.dp),

        colors = CardDefaults.cardColors(

            containerColor = Color.White

        ),

        elevation = CardDefaults.cardElevation(

            defaultElevation = 3.dp

        ),

        onClick = {

            onClick?.invoke()

        }

    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),

            verticalAlignment = Alignment.CenterVertically

        ) {

            Surface(

                modifier = Modifier.size(56.dp),

                shape = CircleShape,

                color = iconBackground

            ) {

                Box(

                    contentAlignment = Alignment.Center

                ) {

                    Icon(

                        imageVector = getCategoryIcon(
                            transaction.category
                        ),

                        contentDescription = null,

                        tint = BrandGreen,

                        modifier = Modifier.size(28.dp)

                    )

                }

            }

            Spacer(

                modifier = Modifier.width(16.dp)

            )

            Column(

                modifier = Modifier.weight(1f)

            ) {

                Text(

                    text = transaction.merchant,

                    fontSize = 17.sp,

                    fontWeight = FontWeight.Bold

                )

                Spacer(

                    modifier = Modifier.height(4.dp)

                )

                Text(

                    text = transaction.description,

                    fontSize = 13.sp,

                    color = Color.Gray,

                    maxLines = 1

                )

                Spacer(

                    modifier = Modifier.height(8.dp)

                )

                Row(

                    horizontalArrangement = Arrangement.spacedBy(8.dp)

                ) {

                    Surface(

                        shape = RoundedCornerShape(50),

                        color = BrandGreen.copy(alpha = .10f)

                    ) {

                        Text(

                            text = transaction.category.name,

                            modifier = Modifier.padding(

                                horizontal = 10.dp,

                                vertical = 4.dp

                            ),

                            fontSize = 11.sp,

                            color = BrandGreen

                        )

                    }

                    Surface(

                        shape = RoundedCornerShape(50),

                        color = Color(0xFFF3F4F6)

                    ) {

                        Text(

                            text = transaction.paymentMethod.name,

                            modifier = Modifier.padding(

                                horizontal = 10.dp,

                                vertical = 4.dp

                            ),

                            fontSize = 11.sp,

                            color = Color.DarkGray

                        )

                    }

                }

            }

            Spacer(

                modifier = Modifier.width(12.dp)

            )

            Column(

                horizontalAlignment = Alignment.End

            ) {

                Text(
                    text =
                        amountPrefix +
                                String.format(
                                    "%,.0f",
                                    transaction.amount
                                ),
                    color = amountColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(

                    modifier = Modifier.height(6.dp)

                )

                Text(

                    text = SimpleDateFormat(

                        "dd MMM, hh:mm a",

                        LocalLocale.current.platformLocale

                    ).format(

                        Date(

                            transaction.date

                        )

                    ),

                    color = Color.Gray,

                    fontSize = 11.sp

                )

            }

        }

    }

}

private fun getCategoryIcon(category: Category): ImageVector {
    return when (category) {
        Category.FOOD -> Icons.Rounded.Fastfood
        Category.SHOPPING -> Icons.Rounded.ShoppingBag
        Category.TRAVEL -> Icons.Rounded.Flight
        Category.ENTERTAINMENT -> Icons.Rounded.Movie
        Category.GROCERIES -> Icons.Rounded.LocalGroceryStore
        Category.HEALTH -> Icons.Rounded.LocalHospital
        Category.EDUCATION -> Icons.Rounded.School
        Category.BILLS -> Icons.Rounded.ReceiptLong
        Category.INVESTMENT -> Icons.Rounded.TrendingUp
        Category.SALARY -> Icons.Rounded.Payments
        Category.TRANSFER -> Icons.Rounded.SwapHoriz
        Category.SUBSCRIPTION -> Icons.Rounded.Subscriptions
        Category.RECHARGE -> Icons.Rounded.PhoneAndroid
        Category.FUEL -> Icons.Rounded.LocalGasStation
        Category.RENT -> Icons.Rounded.Home
        Category.MEDICAL -> Icons.Rounded.Medication
        Category.OTHER -> Icons.Rounded.AccountBalanceWallet
    }
}