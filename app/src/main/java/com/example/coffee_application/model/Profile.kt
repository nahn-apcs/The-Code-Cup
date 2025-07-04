package com.example.coffee_application.model

import androidx.annotation.Keep
import com.google.firebase.firestore.Exclude
import java.util.UUID

@Keep
data class Profile(
    val phoneNumber: String = "",        //Dùng làm ID chính
    val fullName: String = "",
    val email: String = "",
    val address: String = "",
    val history: History = History(),
    val onGoing: OnGoing = OnGoing(),
    val points: Int = 0,
    val loyaltyPts: Int = 0
)

@Keep
data class History(
    val hist: MutableList<Order> = mutableListOf()
) {
    @Exclude
    fun addObject(order: Order) {
        hist.add(order)
    }
    @Exclude
    fun clear() {
        hist.clear()
    }
//    @Exclude
//    fun getList(): List<Order> {
//        return hist
//    }
}

@Keep
data class OnGoing(
    val orders: MutableList<Order> = mutableListOf()
) {
    @Exclude
    fun addOrder(order: Order) {
        orders.add(order)
    }

    @Exclude
    fun removeOrder(order: Order) {
        orders.remove(order)
    }

//    @Exclude
//    fun getOrders(): List<Order> {
//        return orders
//    }

    @Exclude
    fun clear() {
        orders.clear()
    }
}

@Keep
data class Order(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val priceVnd: Int = 0,
    val priceUsd: Double = 0.0,
    val qty: Int = 0,
    val rewardPoints: Int = 0,
    val loyaltyPts: Int = 0,
    val date: String = "",
    val deliveryAddress: String = "",
)
