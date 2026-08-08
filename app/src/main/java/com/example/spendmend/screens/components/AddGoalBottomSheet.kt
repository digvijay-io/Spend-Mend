package com.example.spendmend.screens.components

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddTask
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.spendmend.data.model.Goal
import com.example.spendmend.data.model.GoalCategory
import com.example.spendmend.data.model.GoalPriority
import com.example.spendmend.screens.GoalViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

private val SpendGreen = Color(0xFF239947)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalBottomSheet(
    viewModel: GoalViewModel,
    goal: Goal? = null,
    onDismiss: () -> Unit
) {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    var goalTitle by rememberSaveable(goal) {
        mutableStateOf(goal?.title ?: "")
    }

    var selectedCategory by rememberSaveable(goal) {
        mutableStateOf(goal?.category ?: GoalCategory.PHONE)
    }

    var targetAmount by rememberSaveable(goal) {
        mutableStateOf(
            goal?.targetAmount?.takeIf { it > 0 }?.toString() ?: ""
        )
    }

    var savedAmount by rememberSaveable(goal) {
        mutableStateOf(
            goal?.savedAmount?.takeIf { it > 0 }?.toString() ?: ""
        )
    }

    var targetDate by rememberSaveable(goal) {
        mutableLongStateOf(
            goal?.targetDate ?: System.currentTimeMillis()
        )
    }

    var selectedPriority by rememberSaveable(goal) {
        mutableStateOf(
            goal?.priority ?: GoalPriority.MEDIUM
        )
    }

    var notes by rememberSaveable(goal) {
        mutableStateOf(goal?.notes ?: "")
    }

    var showMoreCategories by rememberSaveable {
        mutableStateOf(false)
    }

    val quickCategories = remember {
        listOf(
            GoalCategory.PHONE,
            GoalCategory.HOME,
            GoalCategory.TRAVEL
        )
    }

    val context = LocalContext.current

    val calendar = remember {
        Calendar.getInstance()
    }

    val locale = LocalConfiguration.current.locales[0]

    val formattedDate = remember(targetDate) {
        SimpleDateFormat(
            "dd MMM yyyy",
            locale
        ).format(Date(targetDate))
    }

    val datePicker = remember {

        DatePickerDialog(
            context,
            { _, year, month, day ->

                calendar.set(year, month, day)
                targetDate = calendar.timeInMillis

            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

    }

    var animateContent by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        delay(80)
        animateContent = true
    }

    val formValid = remember(
        goalTitle,
        targetAmount
    ) {
        goalTitle.isNotBlank() &&
                targetAmount.toDoubleOrNull() != null
    }

    val buttonColor by animateColorAsState(
        if (formValid)
            SpendGreen
        else
            MaterialTheme.colorScheme.surfaceVariant,
        label = ""
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = {
            BottomSheetDefaults.DragHandle()
        },
        shape = RoundedCornerShape(
            topStart = 28.dp,
            topEnd = 28.dp
        ),
        containerColor = MaterialTheme.colorScheme.surface
    ) {

        AnimatedVisibility(
            visible = animateContent,
            enter = fadeIn() +
                    slideInVertically { it / 5 }
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(horizontal = 22.dp)
                    .padding(bottom = 26.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {

                Surface(
                    shape = CircleShape,
                    color = SpendGreen.copy(alpha = .10f)
                ) {

                    Icon(
                        imageVector =
                            if (goal == null)
                                Icons.Outlined.AddTask
                            else
                                Icons.Outlined.Edit,
                        contentDescription = null,
                        tint = SpendGreen,
                        modifier = Modifier
                            .padding(14.dp)
                            .size(24.dp)
                    )

                }

                Text(
                    text =
                        if (goal == null)
                            "Create Goal"
                        else
                            "Update Goal",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text =
                        if (goal == null)
                            "Start saving for something meaningful."
                        else
                            "Update your savings progress.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider()

                // ------------------------
                // Goal Name
                // ------------------------

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        "Goal Name",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedTextField(
                        value = goalTitle,
                        onValueChange = {
                            goalTitle = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        leadingIcon = {
                            Text("🎯")
                        },
                        placeholder = {
                            Text("MacBook Pro, Goa Trip...")
                        } ,
                        colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SpendGreen,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedLabelColor = SpendGreen,
                            cursorColor = SpendGreen,
                            focusedLeadingIconColor = SpendGreen
                        )
                    )

                }
                // ------------------------
                // Category
                // ------------------------

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = "Category",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        quickCategories.forEach { category ->

                            CategoryChip(
                                modifier = Modifier.weight(1f),
                                category = category,
                                selected = selectedCategory == category
                            ) {
                                selectedCategory = category
                            }

                        }

                        FilledTonalButton(
                            onClick = {
                                showMoreCategories = true
                            },
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(
                                horizontal = 14.dp,
                                vertical = 10.dp
                            )
                        ) {

                            Text("More")

                        }

                    }

                }

                // ------------------------
                // Savings
                // ------------------------

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = "Savings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        AmountCard(
                            modifier = Modifier.weight(1f),
                            title = "Target",
                            emoji = "🎯",
                            value = targetAmount,
                            placeholder = "50000",
                            onValueChange = {

                                targetAmount = it.filter { c ->
                                    c.isDigit() || c == '.'
                                }

                            }
                        )

                        AmountCard(
                            modifier = Modifier.weight(1f),
                            title = "Saved",
                            emoji = "💰",
                            value = savedAmount,
                            placeholder = "5000",
                            onValueChange = {

                                savedAmount = it.filter { c ->
                                    c.isDigit() || c == '.'
                                }

                            }
                        )

                    }

                }

                // ------------------------
                // Target Date
                // ------------------------

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = "Target Date",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                datePicker.show()
                            },
                        shape = RoundedCornerShape(18.dp)
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 18.dp,
                                    vertical = 16.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "📅",
                                style = MaterialTheme.typography.titleLarge
                            )

                            Spacer(
                                modifier = Modifier.width(12.dp)
                            )

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = formattedDate,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Text(
                                    text = "Tap to change",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                            }

                        }

                    }

                }
                // ------------------------
                // Priority
                // ------------------------

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = "Priority",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        GoalPriority.entries.forEach { priority ->

                            PriorityChip(
                                modifier = Modifier.weight(1f),
                                priority = priority,
                                selected = selectedPriority == priority
                            ) {

                                selectedPriority = priority

                            }

                        }

                    }

                }

                // ------------------------
                // Notes
                // ------------------------

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = "Notes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = {

                            if (it.length <= 200)
                                notes = it

                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 3,
                        shape = RoundedCornerShape(18.dp),
                        placeholder = {

                            Text(
                                "Optional notes..."
                            )

                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SpendGreen,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedLabelColor = SpendGreen,
                            cursorColor = SpendGreen,
                            focusedLeadingIconColor = SpendGreen
                        )
                    )

                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                // ------------------------
                // Create Button
                // ------------------------

                Button(
                    onClick = {

                        val target =
                            targetAmount.toDoubleOrNull() ?: return@Button

                        val saved =
                            savedAmount.toDoubleOrNull() ?: 0.0

                        val goalToSave = Goal(

                            id = goal?.id ?: 0,

                            title = goalTitle.trim(),

                            category = selectedCategory,

                            targetAmount = target,

                            savedAmount = saved,

                            targetDate = targetDate,

                            priority = selectedPriority,

                            notes = notes.trim(),

                            isCompleted = saved >= target,

                            createdAt = goal?.createdAt
                                ?: System.currentTimeMillis()

                        )

                        if (goal == null) {

                            viewModel.addGoal(goalToSave)

                        } else {

                            viewModel.updateGoal(goalToSave)

                        }

                        onDismiss()

                    },
                    enabled = formValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonColor
                    )
                ) {

                    Text(
                        text =
                            if (goal == null)
                                "Create Goal"
                            else
                                "Update Goal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                }

            }

        }

    }

    if (showMoreCategories) {

        ModalBottomSheet(
            onDismissRequest = {

                showMoreCategories = false

            },
            sheetState = rememberModalBottomSheetState()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = "More Categories",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                GoalCategory.entries
                    .filter {

                        it !in quickCategories

                    }
                    .forEach { category ->

                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {

                                    selectedCategory = category
                                    showMoreCategories = false

                                },
                            shape = RoundedCornerShape(16.dp)
                        ) {

                            Text(
                                text = category.displayName,
                                modifier = Modifier.padding(18.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )

                        }

                    }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

            }

        }

    }

}

@Composable
private fun CategoryChip(
    modifier: Modifier = Modifier,
    category: GoalCategory,
    selected: Boolean,
    onClick: () -> Unit
) {

    val emoji = when (category) {
        GoalCategory.PHONE -> "📱"
        GoalCategory.HOME -> "🏠"
        GoalCategory.TRAVEL -> "✈️"
        GoalCategory.LAPTOP -> "💻"
        GoalCategory.VEHICLE -> "🚗"
        GoalCategory.EDUCATION -> "🎓"
        GoalCategory.INVESTMENT -> "📈"
        GoalCategory.EMERGENCY_FUND -> "🛡️"
        GoalCategory.WEDDING -> "💍"
        GoalCategory.BUSINESS -> "💼"
        GoalCategory.RETIREMENT -> "🌴"
        GoalCategory.CUSTOM -> "✨"
    }

    Surface(
        modifier = modifier
            .height(46.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (selected)
            SpendGreen
        else
            MaterialTheme.colorScheme.surfaceContainerLow
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = emoji,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = category.displayName,
                maxLines = 1,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (selected)
                    Color.White
                else
                    MaterialTheme.colorScheme.onSurface
            )

        }

    }

}

@Composable
private fun AmountCard(
    modifier: Modifier = Modifier,
    title: String,
    emoji: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {

    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = emoji,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

            }

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                prefix = {
                    Text("₹")
                },
                placeholder = {
                    Text(placeholder)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                textStyle = MaterialTheme.typography.titleMedium,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SpendGreen,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = SpendGreen,
                    cursorColor = SpendGreen,
                    focusedLeadingIconColor = SpendGreen
                )
            )

        }

    }

}

@Composable
private fun PriorityChip(
    modifier: Modifier = Modifier,
    priority: GoalPriority,
    selected: Boolean,
    onClick: () -> Unit
) {

    val (emoji, tint) = when (priority) {
        GoalPriority.LOW -> "🟢" to Color(0xFF2E7D32)
        GoalPriority.MEDIUM -> "🟡" to Color(0xFFF9A825)
        GoalPriority.HIGH -> "🔴" to Color(0xFFC62828)
    }

    Surface(
        modifier = modifier
            .height(46.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color =
            if (selected)
                tint.copy(alpha = 0.15f)
            else
                MaterialTheme.colorScheme.surfaceContainerLow
    ) {

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = emoji
            )

            Spacer(
                modifier = Modifier.width(6.dp)
            )

            Text(
                text = priority.name
                    .lowercase()
                    .replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color =
                    if (selected)
                        tint
                    else
                        MaterialTheme.colorScheme.onSurface
            )

        }

    }

}