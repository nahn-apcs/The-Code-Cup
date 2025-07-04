package com.example.coffee_application.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffee_application.model.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CartViewModel : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems = _cartItems.asStateFlow()

    val totalAmountUsd = cartItems.map { list ->
        list.sumOf { it.totalPriceUsd }
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), 0.0)

    val totalAmountVnd = cartItems.map { list ->
        list.sumOf { it.totalPriceVnd }
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), 0.0)

    fun addToCart(newItem: CartItem) {
        val currentList = _cartItems.value.toMutableList()
        val existingItem = currentList.find { it.hasSameOptions(newItem) }

        if (existingItem != null) {
            val updatedItem = existingItem.copy(quantity = existingItem.quantity + newItem.quantity)
            val index = currentList.indexOf(existingItem)
            currentList[index] = updatedItem
        } else {
            currentList.add(newItem)
        }
        _cartItems.value = currentList
    }

    fun updateQuantity(cartItemId: String, newQuantity: Int) {
        val currentList = _cartItems.value.toMutableList()
        val itemIndex = currentList.indexOfFirst { it.id == cartItemId }

        if (itemIndex != -1) {
            if (newQuantity > 0) {
                val updatedItem = currentList[itemIndex].copy(quantity = newQuantity)
                currentList[itemIndex] = updatedItem
            } else {
                currentList.removeAt(itemIndex)
            }
            _cartItems.value = currentList
        }
    }

    fun removeFromCart(cartItemId: String) {
        _cartItems.value = _cartItems.value.filterNot { it.id == cartItemId }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }
}