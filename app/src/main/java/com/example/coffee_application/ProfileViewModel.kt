package com.example.coffee_application.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffee_application.data.CoffeeItem
import com.example.coffee_application.manager.PhoneProfileManager
import com.example.coffee_application.model.CartItem
import com.example.coffee_application.model.Order
import com.example.coffee_application.model.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileViewModel : ViewModel() {

    private val profileManager = PhoneProfileManager()

    private val _profile = MutableStateFlow<Profile?>(null)
    val profile: StateFlow<Profile?> = _profile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadProfile(phoneNumber: String) {
        viewModelScope.launch {
            _isLoading.value = true
            profileManager.getProfileByPhone(phoneNumber) { fetchedProfile ->
                _profile.value = fetchedProfile
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(
        fullName: String,
        email: String,
        address: String,
        onResult: (Boolean) -> Unit
    ) {
        val currentProfile = _profile.value ?: run {
            onResult(false)
            return
        }

        val updatedProfile = currentProfile.copy(
            fullName = fullName,
            email = email,
            address = address
        )

        viewModelScope.launch {
            _isLoading.value = true
            profileManager.saveProfile(updatedProfile) { success ->
                if (success) {
                    _profile.value = updatedProfile
                }
                _isLoading.value = false
                onResult(success)
            }
        }
    }

    fun onSignOut() {
        _profile.value = null
    }


    fun checkout(
        cartItems: List<CartItem>,
        deliveryAddress: String,
        onResult: (Boolean) -> Unit
    ) {
        val currentProfile = _profile.value
        if (currentProfile == null) {
            onResult(false)
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            val newOrders = cartItems.map { cartItem ->
                Order(
                    name = cartItem.coffee.name,
                    priceVnd = cartItem.totalPriceVnd.toInt(),
                    priceUsd = cartItem.totalPriceUsd,
                    qty = cartItem.quantity,
                    rewardPoints = cartItem.coffee.point * cartItem.quantity,
                    loyaltyPts = cartItem.quantity,
                    date = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    deliveryAddress = deliveryAddress
                )
            }


            val updatedProfile = currentProfile.copy(
                onGoing = currentProfile.onGoing.copy(
                    orders = (currentProfile.onGoing.orders + newOrders).toMutableList()
                )
            )

            profileManager.saveProfile(updatedProfile) { success ->
                if (success) {
                    _profile.value = updatedProfile
                }
                _isLoading.value = false
                onResult(success)
            }
        }
    }

    fun moveOrderToHistory(order: Order, onResult: (Boolean) -> Unit) {
        val currentProfile = _profile.value
        if (currentProfile == null) {
            onResult(false)
            return
        }

        viewModelScope.launch {
            val updatedOngoingOrders = currentProfile.onGoing.orders.toMutableList().apply {
                remove(order)
            }

            val updatedHistoryOrders = currentProfile.history.hist.toMutableList().apply {
                add(0, order)
            }

            val newTotalPoints = currentProfile.points + order.rewardPoints
            val newTotalLoyaltyPts = currentProfile.loyaltyPts + order.loyaltyPts


            val updatedProfile = currentProfile.copy(
                onGoing = currentProfile.onGoing.copy(orders = updatedOngoingOrders),
                history = currentProfile.history.copy(hist = updatedHistoryOrders),
                points = newTotalPoints,
                loyaltyPts = newTotalLoyaltyPts
            )

            profileManager.saveProfile(updatedProfile) { success ->
                if (success) {
                    _profile.value = updatedProfile
                }
                onResult(success)
            }
        }
    }


    fun redeemDrink(
        coffeeItem: CoffeeItem,
        redemptionCost: Int,
        onResult: (Boolean) -> Unit
    ) {
        val currentProfile = _profile.value
        if (currentProfile == null || currentProfile.points < redemptionCost) {
            onResult(false)
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            val redeemedOrder = Order(
                name = "${coffeeItem.name} (Redeemed)",
                priceVnd = 0,
                priceUsd = 0.0,
                qty = 1,
                rewardPoints = 0,
                loyaltyPts = 0,
                date = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(Date()),
                deliveryAddress = "Redeemed in-app"
            )


            val updatedProfile = currentProfile.copy(
                points = currentProfile.points - redemptionCost,
                onGoing = currentProfile.onGoing.copy(
                    orders = (currentProfile.onGoing.orders + redeemedOrder).toMutableList()
                )
            )

            profileManager.saveProfile(updatedProfile) { success ->
                if (success) {
                    _profile.value = updatedProfile
                }
                _isLoading.value = false
                onResult(success)
            }
        }
    }
}