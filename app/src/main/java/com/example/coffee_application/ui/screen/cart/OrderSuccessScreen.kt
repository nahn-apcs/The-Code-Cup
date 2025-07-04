package com.example.coffee_application.ui.screen.success

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.coffee_application.LanguageViewModel
import com.example.coffee_application.Poppins
import com.example.coffee_application.R
import com.example.coffee_application.ui.component.BottomBarItem

@Composable
fun OrderSuccessScreen(
    navController: NavController,
    languageViewModel: LanguageViewModel
) {
    // Lấy trạng thái ngôn ngữ hiện tại từ ViewModel
    val currentLang by languageViewModel.language.collectAsState()

    // Định nghĩa các chuỗi văn bản dựa trên ngôn ngữ
    val title = if (currentLang == "vi") "Đặt hàng thành công" else "Order Success"
    val subtitle = if (currentLang == "vi") "Đơn hàng của bạn đã được đặt" else "Your order has been placed"
    val trackOrderButtonText = if (currentLang == "vi") "Theo dõi đơn hàng" else "Track My Order"
    val backToHomeButtonText = if (currentLang == "vi") "Về trang chủ" else "Back to Home"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon thành công
        Icon(
            painter = painterResource(id = R.drawable.take_away),
            contentDescription = "Success Icon",
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Tiêu đề
        Text(
            text = title,
            fontFamily = Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Phụ đề
        Text(
            text = subtitle,
            fontFamily = Poppins,
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Nút "Theo dõi đơn hàng"
        Button(
            onClick = {
                navController.navigate("main?startTab=${BottomBarItem.BILL.name}") {
                    popUpTo("main") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF324A59),     // màu nền nút
                contentColor = Color.White              // màu chữ/icon trong nút
            )
        ) {
            Text(
                trackOrderButtonText,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Poppins
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nút "Về trang chủ"
        OutlinedButton(
            onClick = {
                navController.navigate("main?startTab=${BottomBarItem.HOME.name}") {
                    popUpTo("main") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,     // màu nền nút
                contentColor = Color(0xFF324A59)             // màu chữ/icon trong nút
            )

        ) {
            Text(
                backToHomeButtonText,
                fontSize = 18.sp,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}