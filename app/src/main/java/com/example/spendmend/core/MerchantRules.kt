package com.example.spendmend.core

import com.example.spendmend.data.model.Category

object MerchantRules {

    val categoryMap = mapOf(

        Category.FOOD to listOf(
            "zomato",
            "swiggy",
            "dominos",
            "pizza hut",
            "kfc",
            "mcdonald",
            "burger king",
            "starbucks",
            "subway"
        ),

        Category.SHOPPING to listOf(
            "amazon",
            "flipkart",
            "myntra",
            "ajio",
            "meesho",
            "nykaa"
        ),

        Category.TRAVEL to listOf(
            "uber",
            "ola",
            "rapido",
            "irctc",
            "ixigo",
            "makemytrip",
            "redbus"
        ),

        Category.SUBSCRIPTION to listOf(
            "spotify",
            "netflix",
            "youtube",
            "prime",
            "hotstar",
            "jiocinema",
            "sony liv"
        ),

        Category.RECHARGE to listOf(
            "jio",
            "airtel",
            "vi",
            "bsnl"
        ),

        Category.MEDICAL to listOf(
            "apollo",
            "pharmeasy",
            "1mg",
            "medplus"
        ),

        Category.FUEL to listOf(
            "indianoil",
            "hp",
            "bharat petroleum",
            "shell"
        ),

        Category.GROCERIES to listOf(
            "zepto",
            "blinkit",
            "bigbasket",
            "dmart",
            "reliance fresh"
        )

    )

}