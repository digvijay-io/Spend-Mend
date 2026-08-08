package com.example.spendmend.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spendmend.data.model.Goal
import com.example.spendmend.screens.components.AddGoalBottomSheet
import com.example.spendmend.screens.components.GoalCard
import java.text.NumberFormat
import java.util.Locale


private val SpendMendGreen = Color(0xFF239947)

private val CardShape = RoundedCornerShape(28.dp)

private val ChipShape = RoundedCornerShape(18.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    viewModel: GoalViewModel = viewModel(),
    onGoalClick: (Goal) -> Unit,
    onBottomBarVisibilityChanged: (Boolean) -> Unit,
    onEditGoal: (Goal) -> Unit = {}
) {

    var showAddGoalSheet by remember {
        mutableStateOf(false)
    }

    var selectedTab by remember {
        mutableStateOf(0)
    }

    var searchQuery by remember {
        mutableStateOf("")
    }

    var addMoneyGoal by remember {
        mutableStateOf<Goal?>(null)
    }

    var deleteGoal by remember {
        mutableStateOf<Goal?>(null)
    }

    var enteredAmount by remember {
        mutableStateOf("")
    }

    val activeGoals by viewModel.activeGoals.collectAsState()

    val completedGoals by viewModel.completedGoals.collectAsState()

    val totalSaved by viewModel.totalSavedAmount.collectAsState()

    val totalTarget by viewModel.totalTargetAmount.collectAsState()

    val overallProgress by viewModel.overallProgress.collectAsState()

    val progress by animateFloatAsState(
        targetValue = overallProgress,
        label = "Progress"
    )

    val formatter = remember {
        NumberFormat.getNumberInstance(Locale("en", "IN"))
    }

    val goals = when (selectedTab) {
        1 -> activeGoals
        2 -> completedGoals
        else -> activeGoals + completedGoals
    }

    val filteredGoals = goals.filter {

        searchQuery.isBlank() ||
                it.title.contains(searchQuery, true) ||
                it.notes.contains(searchQuery, true) ||
                it.category.displayName.contains(searchQuery, true)

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

    Scaffold { padding ->

        LazyColumn(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding),

            contentPadding = PaddingValues(
                horizontal = 20.dp,
                vertical = 20.dp
            ),

            verticalArrangement = Arrangement.spacedBy(18.dp),

            state = listState

        ) {

            item {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column {

                        Text(

                            text = "Goals",

                            style = MaterialTheme.typography.headlineLarge,

                            fontWeight = FontWeight.Bold,

                            color = SpendMendGreen

                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(

                            text = "Turn dreams into reality,one saving at a time.",

                            style = MaterialTheme.typography.bodyLarge,

                            color = MaterialTheme.colorScheme.onSurfaceVariant

                        )

                    }

                }

            }

            // =====================================================
            // OVERALL PROGRESS CARD
            // =====================================================

            item {

                ElevatedCard(

                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = CardShape
                        )
                        .animateContentSize(),

                    shape = CardShape,

                    colors = CardDefaults.elevatedCardColors(
                        containerColor = Color.White
                    ),

                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 2.dp
                    )

                ) {

                    Column(

                        modifier = Modifier.padding(24.dp)

                    ) {

                        Row(

                            modifier = Modifier.fillMaxWidth(),

                            verticalAlignment = Alignment.CenterVertically

                        ) {

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(

                                    text = "Overall Progress",

                                    style = MaterialTheme.typography.titleMedium,

                                    color = MaterialTheme.colorScheme.onSurfaceVariant

                                )

                                Spacer(
                                    modifier = Modifier.height(6.dp)
                                )

                                Text(

                                    text = "₹${formatter.format(totalSaved)}",

                                    style = MaterialTheme.typography.headlineMedium,

                                    fontWeight = FontWeight.Bold,

                                    color = SpendMendGreen

                                )

                                Text(

                                    text = "of ₹${formatter.format(totalTarget)}",

                                    style = MaterialTheme.typography.bodyLarge,

                                    color = MaterialTheme.colorScheme.onSurfaceVariant

                                )

                            }

                            Surface(

                                modifier = Modifier.size(60.dp),

                                shape = CircleShape,

                                color = SpendMendGreen.copy(alpha = .12f)

                            ) {

                                Box(
                                    contentAlignment = Alignment.Center
                                ) {

                                    Icon(

                                        imageVector = Icons.Outlined.Flag,

                                        contentDescription = null,

                                        tint = SpendMendGreen,

                                        modifier = Modifier.size(30.dp)

                                    )

                                }

                            }

                        }

                        Spacer(modifier = Modifier.height(22.dp))

                        LinearProgressIndicator(

                            progress = { progress },

                            modifier = Modifier
                                .fillMaxWidth()
                                .height(9.dp),

                            color = SpendMendGreen,

                            trackColor = SpendMendGreen.copy(alpha = .15f)

                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(

                            modifier = Modifier.fillMaxWidth(),

                            horizontalArrangement = Arrangement.SpaceBetween

                        ) {

                            Column {

                                Text(

                                    text = "${(progress * 100).toInt()}%",

                                    style = MaterialTheme.typography.titleMedium,

                                    fontWeight = FontWeight.Bold,

                                    color = SpendMendGreen

                                )

                                Text(

                                    text = "Completed",

                                    style = MaterialTheme.typography.bodySmall,

                                    color = MaterialTheme.colorScheme.onSurfaceVariant

                                )

                            }

                            Column(
                                horizontalAlignment = Alignment.End
                            ) {

                                Text(

                                    text = activeGoals.size.toString(),

                                    style = MaterialTheme.typography.titleMedium,

                                    fontWeight = FontWeight.Bold,

                                    color = SpendMendGreen

                                )

                                Text(

                                    text = "Active Goals",

                                    style = MaterialTheme.typography.bodySmall,

                                    color = MaterialTheme.colorScheme.onSurfaceVariant

                                )

                            }

                        }

                    }

                }

            }

            // =====================================================
            // SEARCH
            // =====================================================

            item {

                OutlinedTextField(

                    value = searchQuery,

                    onValueChange = {

                        searchQuery = it

                    },

                    modifier = Modifier.fillMaxWidth(),

                    singleLine = true,

                    shape = RoundedCornerShape(18.dp),

                    leadingIcon = {

                        Icon(
                            Icons.Outlined.Search,
                            null,
                            tint = SpendMendGreen
                        )

                    },

                    placeholder = {

                        Text("Search goals")

                    },

                    colors = OutlinedTextFieldDefaults.colors(

                        focusedBorderColor = SpendMendGreen,

                        unfocusedBorderColor = SpendMendGreen.copy(alpha = .25f),

                        focusedLeadingIconColor = SpendMendGreen,

                        unfocusedLeadingIconColor = SpendMendGreen,

                        cursorColor = SpendMendGreen,

                        focusedContainerColor = Color.White,

                        unfocusedContainerColor = Color.White

                    )

                )

            }

            // =====================================================
            // FILTERS
            // =====================================================

            item {

                LazyRow(

                    horizontalArrangement = Arrangement.spacedBy(10.dp)

                ) {

                    item {

                        FilterChip(

                            selected = selectedTab == 0,

                            onClick = {

                                selectedTab = 0

                            },

                            label = {

                                Text("All")

                            },

                            shape = ChipShape,

                            colors = FilterChipDefaults.filterChipColors(

                                selectedContainerColor = SpendMendGreen,

                                selectedLabelColor = Color.White

                            )

                        )

                    }

                    item {

                        FilterChip(

                            selected = selectedTab == 1,

                            onClick = {

                                selectedTab = 1

                            },

                            label = {

                                Text("Active")

                            },

                            shape = ChipShape,

                            colors = FilterChipDefaults.filterChipColors(

                                selectedContainerColor = SpendMendGreen,

                                selectedLabelColor = Color.White

                            )

                        )

                    }

                    item {

                        FilterChip(

                            selected = selectedTab == 2,

                            onClick = {

                                selectedTab = 2

                            },

                            label = {

                                Text("Completed")

                            },

                            shape = ChipShape,

                            colors = FilterChipDefaults.filterChipColors(

                                selectedContainerColor = SpendMendGreen,

                                selectedLabelColor = Color.White

                            )

                        )

                    }

                    item {

                        FilterChip(

                            selected = false,

                            onClick = {
                                showAddGoalSheet = true
                            },

                            label = {
                                Text(
                                    "Add Goal",
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            },

                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Add,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            },

                            shape = ChipShape,

                            border = null,

                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = SpendMendGreen,
                                labelColor = Color.White,
                                iconColor = Color.White,
                                selectedContainerColor = SpendMendGreen,
                                selectedLabelColor = Color.White
                            )

                        )

                    }

                }

            }

            // =====================================================
            // GOALS SECTION
            // =====================================================

            item {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "${filteredGoals.size} Goals",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Surface(
                        color = SpendMendGreen.copy(alpha = .10f),
                        shape = RoundedCornerShape(50)
                    ) {

                        Text(
                            text = when (selectedTab) {
                                1 -> "Active"
                                2 -> "Completed"
                                else -> "All"
                            },
                            modifier = Modifier.padding(
                                horizontal = 14.dp,
                                vertical = 6.dp
                            ),
                            color = SpendMendGreen,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )

                    }

                }

            }



                item {

                    AnimatedVisibility(
                        visible = filteredGoals.isEmpty()
                    ) {

                    ElevatedCard(

                        modifier = Modifier.fillMaxWidth(),

                        shape = CardShape,

                        colors = CardDefaults.elevatedCardColors(
                            containerColor = Color.White
                        ),

                        elevation = CardDefaults.elevatedCardElevation(
                            defaultElevation = 2.dp
                        )

                    ) {

                        Column(

                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 28.dp,
                                    vertical = 42.dp
                                ),

                            horizontalAlignment = Alignment.CenterHorizontally

                        ) {

                            Surface(

                                modifier = Modifier.size(84.dp),

                                shape = CircleShape,

                                color = SpendMendGreen.copy(alpha = .12f)

                            ) {

                                Box(
                                    contentAlignment = Alignment.Center
                                ) {

                                    Icon(

                                        imageVector = Icons.Outlined.Flag,

                                        contentDescription = null,

                                        modifier = Modifier.size(40.dp),

                                        tint = SpendMendGreen

                                    )

                                }

                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(

                                text = "No Goals Yet",

                                style = MaterialTheme.typography.headlineSmall,

                                fontWeight = FontWeight.Bold

                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(

                                text = "Create your first financial goal\nand start saving today.",

                                style = MaterialTheme.typography.bodyLarge,

                                color = MaterialTheme.colorScheme.onSurfaceVariant

                            )

                            Spacer(modifier = Modifier.height(30.dp))

                            Button(

                                onClick = {

                                    showAddGoalSheet = true

                                },

                                shape = RoundedCornerShape(18.dp),

                                colors = ButtonDefaults.buttonColors(

                                    containerColor = SpendMendGreen

                                )

                            ) {

                                Icon(
                                    Icons.Outlined.Add,
                                    null
                                )

                                Spacer(
                                    modifier = Modifier.width(8.dp)
                                )

                                Text("Create Goal")

                            }

                        }

                    }

                }

            }

            if (filteredGoals.isNotEmpty()) {

                items(

                    items = filteredGoals,

                    key = { it.id }

                ) { goal ->

                    Box(

                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem()

                    ) {

                        GoalCard(

                            goal = goal,

                            onCardClick = {

                                onGoalClick(it)

                            },

                            onEdit = {

                                onEditGoal(it)

                            },

                            onDelete = {

                                deleteGoal = it

                            },

                            onComplete = {

                                viewModel.updateGoal(

                                    it.copy(

                                        savedAmount = it.targetAmount,

                                        isCompleted = true

                                    )

                                )

                            },

                            onAddMoney = {

                                addMoneyGoal = it

                                enteredAmount = ""

                            }

                        )

                    }

                }

            }

        }

        // =====================================================
        // ADD SAVINGS DIALOG
        // =====================================================

        addMoneyGoal?.let { goal ->

            AlertDialog(

                onDismissRequest = {
                    addMoneyGoal = null
                    enteredAmount = ""
                },

                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null,
                        tint = SpendMendGreen
                    )
                },

                title = {
                    Text("Add Savings")
                },

                text = {

                    Column {

                        Text(
                            text = "Add savings to ${goal.title}"
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        OutlinedTextField(

                            value = enteredAmount,

                            onValueChange = {
                                enteredAmount = it.filter { c ->
                                    c.isDigit() || c == '.'
                                }
                            },

                            modifier = Modifier.fillMaxWidth(),

                            singleLine = true,

                            shape = RoundedCornerShape(16.dp),

                            placeholder = {
                                Text("₹1000")
                            },

                            colors = OutlinedTextFieldDefaults.colors(

                                focusedBorderColor = SpendMendGreen,

                                unfocusedBorderColor = SpendMendGreen.copy(alpha = .30f),

                                cursorColor = SpendMendGreen,

                                focusedLabelColor = SpendMendGreen

                            )

                        )

                    }

                },

                confirmButton = {

                    Button(

                        onClick = {

                            val amount = enteredAmount.toDoubleOrNull()

                            if (amount != null && amount > 0) {

                                val newAmount = (goal.savedAmount + amount)
                                    .coerceAtMost(goal.targetAmount)

                                viewModel.updateGoal(

                                    goal.copy(

                                        savedAmount = newAmount,

                                        isCompleted = newAmount >= goal.targetAmount

                                    )

                                )

                            }

                            addMoneyGoal = null
                            enteredAmount = ""

                        },

                        colors = ButtonDefaults.buttonColors(
                            containerColor = SpendMendGreen
                        )

                    ) {

                        Text("Save")

                    }

                },

                dismissButton = {

                    OutlinedButton(

                        onClick = {

                            addMoneyGoal = null
                            enteredAmount = ""

                        }

                    ) {

                        Text(
                            "Cancel",
                            color = SpendMendGreen
                        )

                    }

                }

            )

        }

        // =====================================================
        // DELETE DIALOG
        // =====================================================

        deleteGoal?.let { goal ->

            AlertDialog(

                onDismissRequest = {
                    deleteGoal = null
                },

                icon = {

                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )

                },

                title = {

                    Text("Delete Goal")

                },

                text = {

                    Text(
                        "Delete \"${goal.title}\" permanently?"
                    )

                },

                confirmButton = {

                    Button(

                        onClick = {

                            viewModel.deleteGoal(goal)

                            deleteGoal = null

                        },

                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )

                    ) {

                        Text("Delete")

                    }

                },

                dismissButton = {

                    OutlinedButton(

                        onClick = {

                            deleteGoal = null

                        }

                    ) {

                        Text("Cancel")

                    }

                }

            )

        }

    }

    if (showAddGoalSheet) {

        AddGoalBottomSheet(

            viewModel = viewModel,

            onDismiss = {

                showAddGoalSheet = false

            }

        )

    }

}