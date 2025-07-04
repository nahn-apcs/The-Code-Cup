package com.example.coffee_application.ui.screen.order

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.coffee_application.LanguageViewModel
import com.example.coffee_application.Poppins
import com.example.coffee_application.R
import com.example.coffee_application.model.Order
import com.example.coffee_application.viewmodel.ProfileViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrderScreen( // 1. Đổi tên hàm và nhận paddingValues từ MainScreen
    paddingValues: PaddingValues,
    navController: NavController,
    profileViewModel: ProfileViewModel,
    languageViewModel: LanguageViewModel
) {
    val profile by profileViewModel.profile.collectAsState()
    val currentLang by languageViewModel.language.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    val tabs = if (currentLang == "vi") listOf("Đang thực hiện", "Lịch sử") else listOf("On going", "History")
    val title = if (currentLang == "vi") "Đơn hàng của tôi" else "My Order"

    // 2. Xóa bỏ Scaffold, chỉ giữ lại nội dung bên trong một Column
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = paddingValues.calculateBottomPadding()) // 4. Áp dụng padding từ MainScreen ở đây
    ) {
        // 3. Giữ lại TopAppBar vì nó là một phần của giao diện màn hình này
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = title,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    fontSize = 22.sp
                )
            },
            // Xóa bỏ hoàn toàn khối navigationIcon ở đây
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent
            )
        )
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    height = 3.dp,
                    color = Color(0xFF324A59) // Màu đậm
                )
            }
        ) {
            tabs.forEachIndexed { index, text ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = text,
                            fontFamily = Poppins,
                            fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (selectedTab == index) Color(0xFF324A59) else Color.Gray
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> {
                val ongoingOrders = profile?.onGoing?.orders ?: emptyList()
                if (ongoingOrders.isEmpty()) {
                    val message = if (currentLang == "vi") "Chưa có đơn hàng nào." else "No orders yet."
                    EmptyStateText(message = message)
                } else {
                    OngoingOrdersList(
                        orders = ongoingOrders,
                        language = currentLang,
                        onOrderComplete = { order ->
                            profileViewModel.moveOrderToHistory(order) { /* Handle result */ }
                        }
                    )
                }
            }
            1 -> {
                val historyOrders = profile?.history?.hist ?: emptyList()
                if (historyOrders.isEmpty()) {
                    val message = if (currentLang == "vi") "Lịch sử của bạn trống." else "Your history is empty."
                    EmptyStateText(message = message)
                } else {
                    HistoryOrdersList(
                        orders = historyOrders,
                        language = currentLang
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyStateText(
    modifier: Modifier = Modifier,
    message: String
) {
    // Box dùng để căn giữa nội dung
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        // Text để hiển thị thông báo
        Text(
            text = message,
            fontFamily = Poppins,
            fontSize = 18.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OngoingOrdersList(
    orders: List<Order>,
    language: String,
    onOrderComplete: (Order) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(orders, key = { it.id }) { order ->
            // BƯỚC 1: Đảm bảo chúng ta luôn có lambda onOrderComplete mới nhất
            val currentOnOrderComplete by rememberUpdatedState(onOrderComplete)

            // BƯỚC 2: Chỉ cho phép confirm, không gọi hàm ở đây nữa
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = {
                    // Chỉ trả về true nếu vuốt đúng hướng, không gọi hàm ở đây
                    it == SwipeToDismissBoxValue.EndToStart
                },
                positionalThreshold = { it * 0.25f }
            )

            // BƯỚC 3: DÙNG LaunchedEffect ĐỂ GỌI HÀM AN TOÀN
            LaunchedEffect(dismissState.currentValue) {
                if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                    currentOnOrderComplete(order)
                }
            }

            SwipeToDismissBox(
                state = dismissState,
                enableDismissFromStartToEnd = false,
                enableDismissFromEndToStart = true,
                backgroundContent = {
                    // ... (giữ nguyên phần backgroundContent)
                    val color by animateColorAsState(
                        targetValue = when (dismissState.targetValue) {
                            SwipeToDismissBoxValue.EndToStart -> Color(0xFFE8F5E9)
                            else -> Color(0xFFF5F5F5)
                        }, label = "background color"
                    )
                    val scale by animateFloatAsState(
                        targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) 1.2f else 1f,
                        label = "icon scale"
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                            .background(color)
                            .padding(end = 24.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color(0xFF4CAF50), shape = CircleShape)
                                .scale(scale),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Complete",
                                tint = Color.White
                            )
                        }
                    }
                }
            ) {
                OrderItemCard(order = order, language = language)
            }
        }
    }
}

@Composable
private fun HistoryOrdersList(orders: List<Order>, language: String) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(orders, key = { it.id }) { order ->
            OrderItemCard(order = order, language = language)
        }
    }
}

@Composable
private fun OrderItemCard(order: Order, language: String) {
    // Định dạng lại ngày tháng để dễ đọc hơn
    val displayDate = try {
        val parser = java.text.SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())
        val formatter = java.text.SimpleDateFormat("dd MMM | hh:mm a", Locale(if (language == "vi") "vi" else "en"))
        formatter.format(parser.parse(order.date)!!)
    } catch (e: Exception) {
        order.date // fallback
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 2.dp,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayDate,
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_size_filled),
                        contentDescription = "Coffee icon",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = order.name,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = "Map icon",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = order.deliveryAddress,
                        fontFamily = Poppins,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = if (language == "vi") {
                    NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(order.priceVnd)
                } else {
                    String.format(Locale.US, "$%.2f", order.priceUsd)
                },
                fontFamily = Poppins,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}