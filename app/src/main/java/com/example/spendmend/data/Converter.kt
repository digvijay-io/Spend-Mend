package com.example.spendmend.data

import androidx.room.TypeConverter
import com.example.spendmend.data.model.Bank
import com.example.spendmend.data.model.Category
import com.example.spendmend.data.model.PaymentMethod
import com.example.spendmend.data.model.TransactionType

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
}