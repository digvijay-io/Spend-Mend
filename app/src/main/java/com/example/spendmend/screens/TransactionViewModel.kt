package com.example.spendmend.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendmend.data.AppDatabase
import com.example.spendmend.data.Budget
import com.example.spendmend.data.BudgetRepository
import com.example.spendmend.data.Transaction
import com.example.spendmend.data.model.Bank
import com.example.spendmend.data.model.BudgetStatus
import com.example.spendmend.data.model.Category
import com.example.spendmend.data.model.PaymentMethod
import com.example.spendmend.data.model.TransactionType
import com.example.spendmend.ui.state.BudgetUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class TransactionViewModel(
    application: Application
) : AndroidViewModel(application) {

    // ---------------------------------------------------------
    // Database
    // ---------------------------------------------------------

    private val database =
        AppDatabase.getDatabase(application)

    private val dao =
        database.transactionDao()

    private val budgetRepository =
        BudgetRepository(database.budgetDao())

    // ---------------------------------------------------------
    // Current Month
    // ---------------------------------------------------------

    private val calendar = Calendar.getInstance()

    private val _selectedMonth =
        MutableStateFlow(
            calendar.get(Calendar.MONTH)
        )

    private val _selectedYear =
        MutableStateFlow(
            calendar.get(Calendar.YEAR)
        )

    val selectedMonth: StateFlow<Int> =
        _selectedMonth

    val selectedYear: StateFlow<Int> =
        _selectedYear

    init {

        viewModelScope.launch {

            budgetRepository.ensureCurrentMonthBudget()

        }

    }

    // ---------------------------------------------------------
    // Budget
    // ---------------------------------------------------------

    val currentBudget: StateFlow<Budget?> =

        combine(

            selectedMonth,
            selectedYear

        ) { month, year ->

            Pair(month + 1, year)

        }

            .flatMapLatest { (month, year) ->

                budgetRepository.getBudgetFlow(
                    month,
                    year
                )

            }

            .stateIn(

                viewModelScope,

                SharingStarted.WhileSubscribed(5000),

                null

            )

    // ---------------------------------------------------------
    // Transactions
    // ---------------------------------------------------------

    val transactions: StateFlow<List<Transaction>> =

        dao.getAll()

            .stateIn(

                viewModelScope,

                SharingStarted.WhileSubscribed(5000),

                emptyList()

            )

    // ---------------------------------------------------------
    // Lifetime Statistics
    // ---------------------------------------------------------

    val totalIncome: StateFlow<Double> =

        transactions

            .map { list ->

                list.filter {

                    it.transactionType ==
                            TransactionType.INCOME

                }.sumOf {

                    it.amount

                }

            }

            .stateIn(

                viewModelScope,

                SharingStarted.WhileSubscribed(5000),

                0.0

            )

    val totalExpense: StateFlow<Double> =

        transactions

            .map { list ->

                list.filter {

                    it.transactionType ==
                            TransactionType.EXPENSE

                }.sumOf {

                    it.amount

                }

            }

            .stateIn(

                viewModelScope,

                SharingStarted.WhileSubscribed(5000),

                0.0

            )

    val savings: StateFlow<Double> =

        combine(

            totalIncome,

            totalExpense

        ) { income, expense ->

            income - expense

        }

            .stateIn(

                viewModelScope,

                SharingStarted.WhileSubscribed(5000),

                0.0

            )

    // ---------------------------------------------------------
    // Database Operations
    // ---------------------------------------------------------

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            dao.insert(transaction)
        }
    }

    fun insertDummyTransactions() {

        viewModelScope.launch {

            val now = System.currentTimeMillis()

            val dummyTransactions = listOf(

                Transaction(
                    amount = 25000.0,
                    merchant = "Company XYZ",
                    description = "Monthly Salary",
                    category = Category.SALARY,
                    transactionType = TransactionType.INCOME,
                    paymentMethod = PaymentMethod.NET_BANKING,
                    bankName = Bank.HDFC,
                    accountNumber = "XXXX1234",
                    date = now
                ),

                Transaction(
                    amount = 550.0,
                    merchant = "Zomato",
                    description = "Food Order",
                    category = Category.FOOD,
                    transactionType = TransactionType.EXPENSE,
                    paymentMethod = PaymentMethod.UPI,
                    bankName = Bank.HDFC,
                    accountNumber = "XXXX1234",
                    date = now,
                    isAutoDetected = false
                ),

                Transaction(
                    amount = 1299.0,
                    merchant = "Amazon",
                    description = "Shopping",
                    category = Category.SHOPPING,
                    transactionType = TransactionType.EXPENSE,
                    paymentMethod = PaymentMethod.CREDIT_CARD,
                    bankName = Bank.ICICI,
                    accountNumber = "XXXX5555",
                    date = now
                ),

                Transaction(
                    amount = 2499.0,
                    merchant = "IRCTC",
                    description = "Train Tickets",
                    category = Category.TRAVEL,
                    transactionType = TransactionType.EXPENSE,
                    paymentMethod = PaymentMethod.UPI,
                    bankName = Bank.SBI,
                    accountNumber = "XXXX1111",
                    date = now
                )

            )

            dummyTransactions.forEach {
                dao.insert(it)
            }
        }
    }

    // ---------------------------------------------------------
    // Month Navigation
    // ---------------------------------------------------------

    fun nextMonth() {

        if (_selectedMonth.value == 11) {

            _selectedMonth.value = 0
            _selectedYear.value++

        } else {

            _selectedMonth.value++

        }

    }

    fun previousMonth() {

        if (_selectedMonth.value == 0) {

            _selectedMonth.value = 11
            _selectedYear.value--

        } else {

            _selectedMonth.value--

        }

    }

    // ---------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------

    private fun isSameMonth(
        time: Long,
        month: Int,
        year: Int
    ): Boolean {

        val cal = Calendar.getInstance()

        cal.timeInMillis = time

        return cal.get(Calendar.MONTH) == month &&
                cal.get(Calendar.YEAR) == year

    }

    // ---------------------------------------------------------
    // Current Month Transactions
    // ---------------------------------------------------------

    val currentMonthTransactions: StateFlow<List<Transaction>> =

        combine(

            transactions,
            selectedMonth,
            selectedYear

        ) { list, month, year ->

            list.filter {

                isSameMonth(
                    it.date,
                    month,
                    year
                )

            }

        }

            .stateIn(

                viewModelScope,

                SharingStarted.WhileSubscribed(5000),

                emptyList()

            )

    // ---------------------------------------------------------
    // Monthly Income
    // ---------------------------------------------------------

    val currentMonthIncome: StateFlow<Double> =

        currentMonthTransactions

            .map { list ->

                list.filter {

                    it.transactionType ==
                            TransactionType.INCOME

                }.sumOf {

                    it.amount

                }

            }

            .stateIn(

                viewModelScope,

                SharingStarted.WhileSubscribed(5000),

                0.0

            )

    // ---------------------------------------------------------
    // Monthly Expense
    // ---------------------------------------------------------

    val currentMonthExpense: StateFlow<Double> =

        currentMonthTransactions

            .map { list ->

                list.filter {

                    it.transactionType ==
                            TransactionType.EXPENSE

                }.sumOf {

                    it.amount

                }

            }

            .stateIn(

                viewModelScope,

                SharingStarted.WhileSubscribed(5000),

                0.0

            )

    // ---------------------------------------------------------
    // Monthly Savings
    // ---------------------------------------------------------

    val currentMonthSavings: StateFlow<Double> =

        combine(

            currentMonthIncome,

            currentMonthExpense

        ) { income, expense ->

            income - expense

        }

            .stateIn(

                viewModelScope,

                SharingStarted.WhileSubscribed(5000),

                0.0

            )

    // ---------------------------------------------------------
    // Budget UI State
    // ---------------------------------------------------------

    val budgetUiState: StateFlow<BudgetUiState> =

        combine(

            currentBudget,
            currentMonthExpense

        ) { budget, expense ->

            val budgetAmount = budget?.amount ?: 0.0

            val remaining = budgetAmount - expense

            val progress =

                if (budgetAmount > 0)
                    (expense / budgetAmount).toFloat()
                else
                    0f

            val percentage =

                if (budgetAmount > 0)
                    ((expense / budgetAmount) * 100).toInt()
                else
                    0

            val status = when {

                percentage >= 100 ->
                    BudgetStatus.EXCEEDED

                percentage >= 80 ->
                    BudgetStatus.WARNING

                else ->
                    BudgetStatus.SAFE

            }

            BudgetUiState(

                budget = budgetAmount,

                spent = expense,

                remaining = remaining,

                progress = progress.coerceIn(0f, 1f),

                percentage = percentage,

                status = status

            )

        }

            .stateIn(

                viewModelScope,

                SharingStarted.WhileSubscribed(5000),

                BudgetUiState()

            )

    // ---------------------------------------------------------
    // Category Summary
    // ---------------------------------------------------------

    val categorySummary: StateFlow<Map<Category, Double>> =

        currentMonthTransactions

            .map { list ->

                list.filter {

                    it.transactionType ==
                            TransactionType.EXPENSE

                }

                    .groupBy {

                        it.category

                    }

                    .mapValues {

                        it.value.sumOf { tx -> tx.amount }

                    }

            }

            .stateIn(

                viewModelScope,

                SharingStarted.WhileSubscribed(5000),

                emptyMap()

            )

    // ---------------------------------------------------------
    // Previous Month Transactions
    // ---------------------------------------------------------

    val previousMonthTransactions: StateFlow<List<Transaction>> =

        combine(
            transactions,
            selectedMonth,
            selectedYear
        ) { list, month, year ->

            val previousMonth =
                if (month == 0) 11 else month - 1

            val previousYear =
                if (month == 0) year - 1 else year

            list.filter {

                isSameMonth(
                    it.date,
                    previousMonth,
                    previousYear
                )

            }

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    // ---------------------------------------------------------
    // Previous Month Expense
    // ---------------------------------------------------------

    val previousMonthExpense: StateFlow<Double> =

        previousMonthTransactions

            .map { list ->

                list.filter {

                    it.transactionType ==
                            TransactionType.EXPENSE

                }.sumOf {

                    it.amount

                }

            }

            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                0.0
            )

    // ---------------------------------------------------------
    // Expense Trend (Daily)
    // ---------------------------------------------------------

    val expenseTrend: StateFlow<List<Float>> =

        currentMonthTransactions

            .map { list ->

                val calendar = Calendar.getInstance()

                val dailyExpense =
                    mutableMapOf<Int, Double>()

                list.filter {

                    it.transactionType ==
                            TransactionType.EXPENSE

                }.forEach {

                    calendar.timeInMillis = it.date

                    val day =
                        calendar.get(Calendar.DAY_OF_MONTH)

                    dailyExpense[day] =
                        (dailyExpense[day] ?: 0.0) + it.amount

                }

                val days =
                    calendar.getActualMaximum(
                        Calendar.DAY_OF_MONTH
                    )

                (1..days).map {

                    (dailyExpense[it] ?: 0.0).toFloat()

                }

            }

            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    // ---------------------------------------------------------
    // Top Merchants
    // ---------------------------------------------------------

    val topMerchants: StateFlow<List<Pair<String, Double>>> =

        currentMonthTransactions

            .map { list ->

                list.filter {

                    it.transactionType ==
                            TransactionType.EXPENSE

                }

                    .groupBy {

                        it.merchant

                    }

                    .mapValues {

                        it.value.sumOf { tx -> tx.amount }

                    }

                    .toList()

                    .sortedByDescending {

                        it.second

                    }

                    .take(5)

            }

            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    // ---------------------------------------------------------
    // Payment Methods
    // ---------------------------------------------------------

    val paymentMethodSummary:
            StateFlow<Map<PaymentMethod, Double>> =

        currentMonthTransactions

            .map { list ->

                list.filter {

                    it.transactionType ==
                            TransactionType.EXPENSE

                }

                    .groupBy {

                        it.paymentMethod

                    }

                    .mapValues {

                        it.value.sumOf { tx -> tx.amount }

                    }

            }

            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyMap()
            )

    // ---------------------------------------------------------
    // Total Transactions
    // ---------------------------------------------------------

    val totalTransactions: StateFlow<Int> =

        currentMonthTransactions

            .map {

                it.size

            }

            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                0
            )

    // ---------------------------------------------------------
    // Biggest Expense
    // ---------------------------------------------------------

    val biggestExpense: StateFlow<Transaction?> =

        currentMonthTransactions

            .map { list ->

                list.filter {

                    it.transactionType ==
                            TransactionType.EXPENSE

                }

                    .maxByOrNull {

                        it.amount

                    }

            }

            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                null
            )

    // ---------------------------------------------------------
    // Highest Category
    // ---------------------------------------------------------

    val highestCategory:
            StateFlow<Map.Entry<Category, Double>?> =

        categorySummary

            .map {

                it.maxByOrNull { entry ->

                    entry.value

                }

            }

            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                null
            )

    // ---------------------------------------------------------
    // Average Daily Expense
    // ---------------------------------------------------------

    val averageDailyExpense: StateFlow<Double> =

        currentMonthExpense

            .map { expense ->

                val calendar = Calendar.getInstance()

                calendar.set(
                    Calendar.MONTH,
                    selectedMonth.value
                )

                calendar.set(
                    Calendar.YEAR,
                    selectedYear.value
                )

                val days =
                    calendar.getActualMaximum(
                        Calendar.DAY_OF_MONTH
                    )

                if (days == 0)
                    0.0
                else
                    expense / days

            }

            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                0.0
            )

    // ---------------------------------------------------------
    // Average Transaction Amount
    // ---------------------------------------------------------

    val averageTransaction: StateFlow<Double> =

        combine(

            currentMonthExpense,

            totalTransactions

        ) { expense, total ->

            if (total == 0)
                0.0
            else
                expense / total

        }

            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                0.0
            )

    // ---------------------------------------------------------
    // Saving Rate
    // ---------------------------------------------------------

    val savingRate: StateFlow<Int> =

        combine(

            currentMonthIncome,

            currentMonthSavings

        ) { income, savings ->

            if (income == 0.0)
                0
            else
                ((savings / income) * 100).toInt()

        }

            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                0
            )

    // ---------------------------------------------------------
    // Financial Health Score
    // ---------------------------------------------------------

    val financialHealthScore: StateFlow<Int> =

        combine(

            currentMonthIncome,

            currentMonthExpense,

            currentMonthSavings,

            budgetUiState

        ) { income, expense, savings, budget ->

            if (income <= 0.0) {

                0

            } else {

                var score = 100

                val savingRatio =
                    (savings / income)
                        .coerceIn(0.0, 1.0)

                score -=
                    ((1 - savingRatio) * 40).toInt()

                score -=
                    (budget.progress * 40).toInt()

                if (expense > income)
                    score -= 20

                score.coerceIn(0, 100)

            }

        }

            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                0
            )

    // ---------------------------------------------------------
    // AI Insight
    // ---------------------------------------------------------

    val aiInsight: StateFlow<String> =

        combine(

            highestCategory,

            savingRate,

            budgetUiState

        ) { category, savingRate, budget ->

            when {

                category == null ->
                    "Start tracking your expenses to unlock AI insights."

                budget.status == BudgetStatus.EXCEEDED ->
                    "You've exceeded your monthly budget. Review your recent spending."

                savingRate >= 40 ->
                    "Excellent! You're saving over 40% of your income."

                category.key == Category.FOOD ->
                    "Food is your largest expense. Cooking more meals could reduce spending."

                category.key == Category.SHOPPING ->
                    "Shopping is your biggest category. Waiting 24 hours before buying can reduce impulse purchases."

                else ->
                    "Your spending pattern looks balanced this month."

            }

        }

            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                ""
            )

    // ---------------------------------------------------------
    // Save Budget
    // ---------------------------------------------------------

    fun saveCurrentMonthBudget(
        amount: Double
    ) {

        viewModelScope.launch {

            budgetRepository.saveBudget(

                month = selectedMonth.value + 1,

                year = selectedYear.value,

                amount = amount

            )

        }

    }

}