package com.example.coffee_application.model

import com.example.coffee_application.data.CoffeeItem
import java.util.UUID

data class CartItem(
    val coffee: CoffeeItem,

    val quantity: Int,
    val selectedShotIndex: Int, // 0: Single, 1: Double
    val isHotSelected: Boolean, // true: Nóng, false: Lạnh
    val selectedSizeIndex: Int, // 0: Nhỏ, 1: Vừa, 2: Lớn
    val selectedIceLevel: Int,  // 0: Không, 1: Ít, 2: Vừa, 3: Nhiều

    val pricePerItemUsd: Double,
    val pricePerItemVnd: Double,

    val id: String = UUID.randomUUID().toString()
) {
    val totalPriceUsd: Double
        get() = pricePerItemUsd * quantity

    val totalPriceVnd: Double
        get() = pricePerItemVnd * quantity


    fun hasSameOptions(other: CartItem): Boolean {
        return this.coffee.name == other.coffee.name &&
                this.selectedShotIndex == other.selectedShotIndex &&
                this.isHotSelected == other.isHotSelected &&
                this.selectedSizeIndex == other.selectedSizeIndex &&
                this.selectedIceLevel == other.selectedIceLevel
    }
}
