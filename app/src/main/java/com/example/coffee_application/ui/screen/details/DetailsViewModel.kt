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


    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity.asStateFlow()


    private val _selectedShotIndex = MutableStateFlow(0)
    val selectedShotIndex: StateFlow<Int> = _selectedShotIndex.asStateFlow()


    private val _isHotSelected = MutableStateFlow(true)
    val isHotSelected: StateFlow<Boolean> = _isHotSelected.asStateFlow()


    private val _selectedSizeIndex = MutableStateFlow(0)
    val selectedSizeIndex: StateFlow<Int> = _selectedSizeIndex.asStateFlow()


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

    fun setQuantity(q: Int) {
        if (q > 0) _quantity.value = q
    }

    fun setShot(index: Int) {
        _selectedShotIndex.value = index
    }

    fun setIsHot(isHot: Boolean) {
        _isHotSelected.value = isHot
        if (isHot) {
            _selectedIceLevel.value = 0
        }
    }

    fun setSize(index: Int) {
        _selectedSizeIndex.value = index
    }

    fun setIceLevel(level: Int) {
        if (!_isHotSelected.value) {
            _selectedIceLevel.value = level
        }
    }

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