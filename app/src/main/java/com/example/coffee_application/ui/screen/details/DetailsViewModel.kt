package com.example.coffee_application.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffee_application.data.CoffeeItem
import com.example.coffee_application.data.coffeeOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class DetailsViewModel : ViewModel() {

    private val _coffeeItem = MutableStateFlow<CoffeeItem?>(null)
    val coffeeItem: StateFlow<CoffeeItem?> = _coffeeItem.asStateFlow()

    // Trạng thái các lựa chọn của người dùng
    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity.asStateFlow()

    // 0 = Single, 1 = Double
    private val _selectedShotIndex = MutableStateFlow(0)
    val selectedShotIndex: StateFlow<Int> = _selectedShotIndex.asStateFlow()

    // true = Nóng, false = Lạnh
    private val _isHotSelected = MutableStateFlow(true)
    val isHotSelected: StateFlow<Boolean> = _isHotSelected.asStateFlow()

    // 0 = Nhỏ, 1 = Vừa, 2 = Lớn
    private val _selectedSizeIndex = MutableStateFlow(0)
    val selectedSizeIndex: StateFlow<Int> = _selectedSizeIndex.asStateFlow()

    // 0 = Không đá, 1 = Ít đá, 2 = Vừa, 3 = Nhiều đá
    private val _selectedIceLevel = MutableStateFlow(0)
    val selectedIceLevel: StateFlow<Int> = _selectedIceLevel.asStateFlow()

    fun loadCoffeeItem(name: String) {
        _coffeeItem.value = coffeeOptions.find { it.name == name }
        resetSelections()
    }

    private fun resetSelections() {
        _quantity.value = 1
        _selectedShotIndex.value = 0
        _isHotSelected.value = true
        _selectedSizeIndex.value = 0
        _selectedIceLevel.value = 0
    }

    // --- Các hàm để UI gọi để thay đổi trạng thái ---
    fun setQuantity(q: Int) {
        if (q > 0) _quantity.value = q
    }

    fun setShot(index: Int) {
        _selectedShotIndex.value = index
    }

    fun setIsHot(isHot: Boolean) {
        _isHotSelected.value = isHot
        if (isHot) { // Nếu chọn nóng thì không có đá
            _selectedIceLevel.value = 0
        }
    }

    fun setSize(index: Int) {
        _selectedSizeIndex.value = index
    }

    fun setIceLevel(level: Int) {
        // Chỉ cho phép chọn đá nếu là đồ lạnh
        if (!_isHotSelected.value) {
            _selectedIceLevel.value = level
        }
    }

    // --- Tính toán tổng tiền ---
    val totalAmountUsd: StateFlow<Double> = combine(
        _coffeeItem, _quantity, _selectedSizeIndex
    ) { coffee, qty, sizeIndex ->
        coffee?.let { it.pricesUsd.getOrElse(sizeIndex) { 0.0 } * qty } ?: 0.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalAmountVnd: StateFlow<Double> = combine(
        _coffeeItem, _quantity, _selectedSizeIndex
    ) { coffee, qty, sizeIndex ->
        coffee?.let { it.pricesVnd.getOrElse(sizeIndex) { 0.0 } * qty } ?: 0.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
}