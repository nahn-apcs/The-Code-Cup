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

    // Dùng StateFlow để UI có thể lắng nghe sự thay đổi dữ liệu profile
    // Bắt đầu với giá trị null (chưa đăng nhập)
    private val _profile = MutableStateFlow<Profile?>(null)
    val profile: StateFlow<Profile?> = _profile.asStateFlow()

    // State cho việc đang tải dữ liệu
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Hàm này được gọi khi đăng nhập thành công.
     * Nó sẽ lấy dữ liệu từ Firebase và cập nhật vào StateFlow.
     */
    fun loadProfile(phoneNumber: String) {
        viewModelScope.launch {
            _isLoading.value = true
            profileManager.getProfileByPhone(phoneNumber) { fetchedProfile ->
                _profile.value = fetchedProfile
                _isLoading.value = false
            }
        }
    }

    /**
     * Hàm này được gọi khi người dùng lưu thông tin đã chỉnh sửa.
     * Nó sẽ cập nhật lên Firebase VÀ cập nhật lại StateFlow.
     */
    fun updateProfile(
        fullName: String,
        email: String,
        address: String,
        onResult: (Boolean) -> Unit
    ) {
        val currentProfile = _profile.value ?: run {
            onResult(false) // Trả về thất bại nếu không có profile
            return
        }

        // Tạo một đối tượng profile mới với thông tin đã cập nhật
        val updatedProfile = currentProfile.copy(
            fullName = fullName,
            email = email,
            address = address
        )

        viewModelScope.launch {
            _isLoading.value = true
            profileManager.saveProfile(updatedProfile) { success ->
                if (success) {
                    // Nếu lưu lên Firebase thành công, cập nhật lại state trong ViewModel
                    _profile.value = updatedProfile
                }
                // Bạn có thể thêm logic xử lý lỗi ở đây
                _isLoading.value = false
                onResult(success) // << GỌI CALLBACK ĐỂ BÁO KẾT QUẢ
            }
        }
    }

    /**
     * Hàm này được gọi khi người dùng đăng xuất.
     * Nó sẽ xoá sạch dữ liệu trong ViewModel.
     */
    fun onSignOut() {
        _profile.value = null
    }


    /**
     * Hàm xử lý thanh toán.
     * Chuyển đổi CartItems thành Orders, cập nhật Profile và lưu lên Firebase.
     */
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

            // 1. Chuyển đổi CartItem thành Order
            val newOrders = cartItems.map { cartItem ->
                Order(
                    name = cartItem.coffee.name,
                    priceVnd = cartItem.totalPriceVnd.toInt(),
                    priceUsd = cartItem.totalPriceUsd,
                    qty = cartItem.quantity,
                    rewardPoints = cartItem.coffee.point * cartItem.quantity, // Tổng điểm thưởng
                    loyaltyPts = cartItem.quantity, // 1 điểm cho mỗi ly
                    date = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    deliveryAddress = deliveryAddress
                )
            }


            // 2. Tạo profile đã cập nhật
            val updatedProfile = currentProfile.copy(
                // Thêm các đơn hàng mới vào danh sách onGoing hiện tại
                onGoing = currentProfile.onGoing.copy(
                    orders = (currentProfile.onGoing.orders + newOrders).toMutableList()
                )
            )

            // 3. Lưu profile mới lên Firebase
            profileManager.saveProfile(updatedProfile) { success ->
                if (success) {
                    // Nếu thành công, cập nhật state trong ViewModel
                    _profile.value = updatedProfile
                }
                _isLoading.value = false
                onResult(success)
            }
        }
    }

    /**
     * Di chuyển một đơn hàng từ OnGoing sang History VÀ CỘNG ĐIỂM
     */
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

            // --- BẮT ĐẦU THAY ĐỔI ---
            // Tính toán điểm mới bằng cách cộng dồn điểm từ đơn hàng đã hoàn thành
            val newTotalPoints = currentProfile.points + order.rewardPoints
            val newTotalLoyaltyPts = currentProfile.loyaltyPts + order.loyaltyPts
            // --- KẾT THÚC THAY ĐỔI ---


            // Tạo profile đã cập nhật với điểm mới
            val updatedProfile = currentProfile.copy(
                onGoing = currentProfile.onGoing.copy(orders = updatedOngoingOrders),
                history = currentProfile.history.copy(hist = updatedHistoryOrders),
                points = newTotalPoints, // Cập nhật tổng điểm thưởng
                loyaltyPts = newTotalLoyaltyPts // Cập nhật tổng điểm tích lũy
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

            // 1. Tạo một đơn hàng mới cho món đồ đã đổi
            val redeemedOrder = Order(
                name = "${coffeeItem.name} (Redeemed)", // Thêm ghi chú đã đổi
                priceVnd = 0, // Giá là 0 vì đổi bằng điểm
                priceUsd = 0.0,
                qty = 1,
                rewardPoints = 0, // Không có điểm thưởng khi đổi quà
                loyaltyPts = 0,   // Không có điểm tích lũy khi đổi quà
                date = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(Date()),
                deliveryAddress = "Redeemed in-app"
            )

            // 2. Tính điểm mới và cập nhật profile
            val updatedProfile = currentProfile.copy(
                points = currentProfile.points - redemptionCost, // Trừ điểm
                // Thêm đơn hàng đã đổi vào danh sách đang thực hiện
                onGoing = currentProfile.onGoing.copy(
                    orders = (currentProfile.onGoing.orders + redeemedOrder).toMutableList()
                )
            )

            // 3. Lưu profile mới lên Firebase
            profileManager.saveProfile(updatedProfile) { success ->
                if (success) {
                    // Nếu thành công, cập nhật state trong ViewModel
                    _profile.value = updatedProfile
                }
                _isLoading.value = false
                onResult(success)
            }
        }
    }


}