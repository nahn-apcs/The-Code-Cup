package com.example.coffee_application.model

import com.example.coffee_application.data.CoffeeItem
import java.util.UUID

data class CartItem(
    // Thông tin cơ bản của sản phẩm cà phê
    val coffee: CoffeeItem,

    // Các tùy chọn người dùng đã chọn
    val quantity: Int,
    val selectedShotIndex: Int, // 0: Single, 1: Double
    val isHotSelected: Boolean, // true: Nóng, false: Lạnh
    val selectedSizeIndex: Int, // 0: Nhỏ, 1: Vừa, 2: Lớn
    val selectedIceLevel: Int,  // 0: Không, 1: Ít, 2: Vừa, 3: Nhiều

    // Giá tiền tại thời điểm thêm vào giỏ
    val pricePerItemUsd: Double,
    val pricePerItemVnd: Double,

    // ID duy nhất cho mỗi món hàng trong giỏ để dễ dàng cập nhật hoặc xóa
    val id: String = UUID.randomUUID().toString()
) {
    // Thuộc tính tính toán tổng giá tiền cho món hàng này
    val totalPriceUsd: Double
        get() = pricePerItemUsd * quantity

    val totalPriceVnd: Double
        get() = pricePerItemVnd * quantity

    // Hàm để kiểm tra xem một món hàng khác có cùng tùy chọn không (trừ số lượng)
    fun hasSameOptions(other: CartItem): Boolean {
        return this.coffee.name == other.coffee.name &&
                this.selectedShotIndex == other.selectedShotIndex &&
                this.isHotSelected == other.isHotSelected &&
                this.selectedSizeIndex == other.selectedSizeIndex &&
                this.selectedIceLevel == other.selectedIceLevel
    }
}
