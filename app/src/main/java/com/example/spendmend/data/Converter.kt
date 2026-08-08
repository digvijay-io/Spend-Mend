package com.example.spendmend.data

import androidx.room.TypeConverter
import com.example.spendmend.data.model.Bank
import com.example.spendmend.data.model.Category
import com.example.spendmend.data.model.PaymentMethod
import com.example.spendmend.data.model.TransactionType
import com.example.spendmend.data.model.GoalCategory
import com.example.spendmend.data.model.GoalPriority

class Converters {

    @TypeConverter
    fun fromTransactionType(value: TransactionType): String = value.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType =
        TransactionType.valueOf(value)

    @TypeConverter
    fun fromCategory(value: Category): String = value.name

    @TypeConverter
    fun toCategory(value: String): Category =
        Category.valueOf(value)

    @TypeConverter
    fun fromPaymentMethod(value: PaymentMethod): String = value.name

    @TypeConverter
    fun toPaymentMethod(value: String): PaymentMethod =
        PaymentMethod.valueOf(value)

    @TypeConverter
    fun fromBank(value: Bank): String = value.name

    @TypeConverter
    fun toBank(value: String): Bank =
        Bank.valueOf(value)

    @TypeConverter
    fun fromGoalCategory(category: GoalCategory): String {
        return category.name
    }

    @TypeConverter
    fun toGoalCategory(value: String): GoalCategory {
        return GoalCategory.valueOf(value)
    }

    @TypeConverter
    fun fromPriority(priority: GoalPriority): String {
        return priority.name
    }

    @TypeConverter
    fun toPriority(value: String): GoalPriority {
        return GoalPriority.valueOf(value)
    }
}