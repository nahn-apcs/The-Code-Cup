package com.example.coffee_application.data

import com.example.coffee_application.R

data class CoffeeItem(
    val name: String,
    val imageRes: Int,
    val pricesUsd: List<Double>, // Thay đổi thành List<Double>
    val pricesVnd: List<Double>,  // Thay đổi thành List<Double>
    val point: Int = 0 // Điểm thưởng mặc định là 0
)

val coffeeOptions = listOf(
    CoffeeItem(
        name = "Americano",
        imageRes = R.drawable.americano,
        pricesUsd = listOf(2.49, 2.99, 3.49), // [Nhỏ, Vừa, Lớn]
        pricesVnd = listOf(60_000.0, 70_000.0, 80_000.0),
        point = 10 // Điểm thưởng cho Americano
    ),
    CoffeeItem(
        name = "Cappuccino",
        imageRes = R.drawable.cappuccino,
        pricesUsd = listOf(2.99, 3.49, 3.99),
        pricesVnd = listOf(75_000.0, 85_000.0, 95_000.0),
        point = 15 // Điểm thưởng cho Cappuccino
    ),
    CoffeeItem(
        name = "Mocha",
        imageRes = R.drawable.mocha,
        pricesUsd = listOf(3.29, 3.79, 4.29),
        pricesVnd = listOf(82_000.0, 92_000.0, 102_000.0),
        point = 20 // Điểm thưởng cho Mocha
    ),
    CoffeeItem(
        name = "Flat White",
        imageRes = R.drawable.flat_white,
        pricesUsd = listOf(2.69, 3.19, 3.69),
        pricesVnd = listOf(68_000.0, 78_000.0, 88_000.0),
        point = 12 // Điểm thưởng cho Flat White
    ),
    CoffeeItem(
        name = "Espresso",
        imageRes = R.drawable.espresso,
        pricesUsd = listOf(2.49, 2.99, 3.29), // [Single, Double, Triple]
        pricesVnd = listOf(60_000.0, 75_000.0, 85_000.0),
        point = 10 // Điểm thưởng cho Espresso
    ),
    CoffeeItem(
        name = "Macchiato",
        imageRes = R.drawable.macchiato,
        pricesUsd = listOf(3.09, 3.59, 4.09),
        pricesVnd = listOf(78_000.0, 88_000.0, 98_000.0),
        point = 18 // Điểm thưởng cho Macchiato
    )
)
