package com.example.spendmend.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendmend.data.AppDatabase
import com.example.spendmend.data.Transaction
import com.example.spendmend.data.model.Bank
import com.example.spendmend.data.model.Category
import com.example.spendmend.data.model.PaymentMethod
import com.example.spendmend.data.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import com.example.spendmend.data.BudgetRepository
import com.example.spendmend.data.Budget
import com.example.spendmend.data.model.BudgetStatus
import com.example.spendmend.ui.state.BudgetUiState
import kotlinx.coroutines.flow.flatMapLatest


class TransactionViewModel(application: Application) : AndroidViewModel(application) {


    private val database = AppDatabase.getDatabase(application)

    private val dao = database.transactionDao()

    private val budgetRepository = BudgetRepository(
        database.budgetDao()
    )

    init {
        viewModelScope.launch {
            budgetRepository.ensureCurrentMonthBudget()
        }
    }

    private val calendar = Calendar.getInstance()

    private val _selectedMonth =
        MutableStateFlow(calendar.get(Calendar.MONTH))

    private val _selectedYear =
        MutableStateFlow(calendar.get(Calendar.YEAR))
    val selectedMonth = _selectedMonth

    val selectedYear = _selectedYear

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

    val transactions: StateFlow<List<Transaction>> =
        dao.getAll()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // --------------------------------------------------------------------
    // Lifetime Statistics
    // --------------------------------------------------------------------

    val totalIncome: StateFlow<Double> =
        transactions
            .map { list ->
                list.filter {
                    it.transactionType == TransactionType.INCOME
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
                    it.transactionType == TransactionType.EXPENSE
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

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0.0
        )


    // --------------------------------------------------------------------
    // Database
    // --------------------------------------------------------------------

    fun addTransaction(transaction: Transaction) {

        viewModelScope.launch {

            dao.insert(transaction)

        }

    }

    fun insertDummyTransactions() {

        viewModelScope.launch {

            val now = System.currentTimeMillis()

            val dummyList = listOf(

                Transaction(
                    amount = 499.0,
                    merchant = "Zomato",
                    description = "Zomato Order",
                    category = Category.FOOD,
                    transactionType = TransactionType.EXPENSE,
                    paymentMethod = PaymentMethod.UPI,
                    bankName = Bank.HDFC,
                    accountNumber = "XX1234",
                    date = now,
                    isAutoDetected = false
                ),

                Transaction(
                    amount = 299.0,
                    merchant = "Swiggy",
                    description = "Swiggy Order",
                    category = Category.FOOD,
                    transactionType = TransactionType.EXPENSE,
                    paymentMethod = PaymentMethod.UPI,
                    bankName = Bank.SBI,
                    accountNumber = "XX4321",
                    date = now
                ),

                Transaction(
                    amount = 6981.0,
                    merchant = "Ixigo",
                    description = "Flight Booking",
                    category = Category.TRAVEL,
                    transactionType = TransactionType.EXPENSE,
                    paymentMethod = PaymentMethod.UPI,
                    bankName = Bank.ICICI,
                    accountNumber = "XX2222",
                    date = now
                ),

                Transaction(
                    amount = 120.0,
                    merchant = "Jio",
                    description = "Recharge",
                    category = Category.RECHARGE,
                    transactionType = TransactionType.EXPENSE,
                    paymentMethod = PaymentMethod.UPI,
                    bankName = Bank.AXIS,
                    accountNumber = "XX9999",
                    date = now
                ),

                Transaction(
                    amount = 2049.0,
                    merchant = "Amazon",
                    description = "Shopping",
                    category = Category.SHOPPING,
                    transactionType = TransactionType.EXPENSE,
                    paymentMethod = PaymentMethod.CREDIT_CARD,
                    bankName = Bank.HDFC,
                    accountNumber = "XX7654",
                    date = now
                ),

                Transaction(
                    amount = 25000.0,
                    merchant = "Company XYZ",
                    description = "Salary",
                    category = Category.SALARY,
                    transactionType = TransactionType.INCOME,
                    paymentMethod = PaymentMethod.NET_BANKING,
                    bankName = Bank.HDFC,
                    accountNumber = "XX1234",
                    date = now
                )

            )

            dummyList.forEach {

                dao.insert(it)

            }

        }

    }

    // --------------------------------------------------------------------
    // Month Navigation
    // --------------------------------------------------------------------

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

    // --------------------------------------------------------------------
    // Helpers
    // --------------------------------------------------------------------

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

    // --------------------------------------------------------------------
    // Monthly Transactions
    // --------------------------------------------------------------------

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

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    // --------------------------------------------------------------------
    // Monthly Income
    // --------------------------------------------------------------------

    val currentMonthIncome: StateFlow<Double> =
        currentMonthTransactions
            .map { list ->

                list.filter {

                    it.transactionType == TransactionType.INCOME

                }.sumOf {

                    it.amount

                }

            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                0.0
            )

    // --------------------------------------------------------------------
    // Monthly Expense
    // --------------------------------------------------------------------

    val currentMonthExpense: StateFlow<Double> =
        currentMonthTransactions
            .map { list ->

                list.filter {

                    it.transactionType == TransactionType.EXPENSE

                }.sumOf {

                    it.amount

                }

            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                0.0
            )

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

            val status =
                when {
                    percentage >= 100 -> BudgetStatus.EXCEEDED
                    percentage >= 80 -> BudgetStatus.WARNING
                    else -> BudgetStatus.SAFE
                }

            BudgetUiState(
                budget = budgetAmount,
                spent = expense,
                remaining = remaining,
                progress = progress.coerceIn(0f, 1f),
                percentage = percentage,
                status = status
            )

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            BudgetUiState()
        )

    // --------------------------------------------------------------------
    // Monthly Savings
    // --------------------------------------------------------------------

    val currentMonthSavings: StateFlow<Double> =
        combine(
            currentMonthIncome,
            currentMonthExpense
        ) { income, expense ->

            income - expense

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0.0
        )

    // --------------------------------------------------------------------
    // Monthly Category Summary
    // --------------------------------------------------------------------

    val categorySummary: StateFlow<Map<Category, Double>> =
        currentMonthTransactions
            .map { list ->

                list.filter {
                    it.transactionType == TransactionType.EXPENSE
                }
                    .groupBy {
                        it.category
                    }
                    .mapValues { (_, transactions) ->
                        transactions.sumOf {
                            it.amount
                        }
                    }

            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyMap()
            )

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