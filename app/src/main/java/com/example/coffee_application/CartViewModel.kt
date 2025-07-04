package com.example.coffee_application.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffee_application.model.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CartViewModel : ViewModel() {

    // StateFlow chứa danh sách các món hàng trong giỏ
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems = _cartItems.asStateFlow()

    // --- Các StateFlow tính toán tổng tiền ---
    val totalAmountUsd = cartItems.map { list ->
        list.sumOf { it.totalPriceUsd }
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), 0.0)

    val totalAmountVnd = cartItems.map { list ->
        list.sumOf { it.totalPriceVnd }
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), 0.0)

    /**
     * Thêm một sản phẩm vào giỏ hàng.
     * Nếu sản phẩm với các tùy chọn tương tự đã tồn tại, nó sẽ gộp số lượng lại.
     */
    fun addToCart(newItem: CartItem) {
        val currentList = _cartItems.value.toMutableList()
        val existingItem = currentList.find { it.hasSameOptions(newItem) }

        if (existingItem != null) {
            // Nếu đã có, cập nhật số lượng của món hàng cũ
            val updatedItem = existingItem.copy(quantity = existingItem.quantity + newItem.quantity)
            val index = currentList.indexOf(existingItem)
            currentList[index] = updatedItem
        } else {
            // Nếu chưa có, thêm món hàng mới vào danh sách
            currentList.add(newItem)
        }
        _cartItems.value = currentList
    }

    /**
     * Cập nhật số lượng của một món hàng trong giỏ.
     * Nếu số lượng <= 0, món hàng sẽ bị xóa.
     */
    fun updateQuantity(cartItemId: String, newQuantity: Int) {
        val currentList = _cartItems.value.toMutableList()
        val itemIndex = currentList.indexOfFirst { it.id == cartItemId }

        if (itemIndex != -1) {
            if (newQuantity > 0) {
                val updatedItem = currentList[itemIndex].copy(quantity = newQuantity)
                currentList[itemIndex] = updatedItem
            } else {
                // Xóa nếu số lượng là 0 hoặc ít hơn
                currentList.removeAt(itemIndex)
            }
            _cartItems.value = currentList
        }
    }

    /**
     * Xóa một món hàng khỏi giỏ.
     */
    fun removeFromCart(cartItemId: String) {
        _cartItems.value = _cartItems.value.filterNot { it.id == cartItemId }
    }

    /**
     * Xóa sạch giỏ hàng.
     * Hàm này sẽ được gọi khi người dùng đăng xuất.
     */
    fun clearCart() {
        _cartItems.value = emptyList()
    }
}