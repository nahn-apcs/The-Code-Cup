package com.example.coffee_application.ui.screen.cart

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.coffee_application.LanguageViewModel
import com.example.coffee_application.Poppins
import com.example.coffee_application.R
import com.example.coffee_application.model.CartItem
import com.example.coffee_application.viewmodel.CartViewModel
import com.example.coffee_application.viewmodel.ProfileViewModel
import java.text.NumberFormat
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    cartViewModel: CartViewModel,
    languageViewModel: LanguageViewModel,
    profileViewModel: ProfileViewModel
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val totalAmountUsd by cartViewModel.totalAmountUsd.collectAsState()
    val totalAmountVnd by cartViewModel.totalAmountVnd.collectAsState()
    val currentLang by languageViewModel.language.collectAsState()

    val title = if (currentLang == "vi") "Giỏ hàng" else "My Cart"
    val totalPriceLabel = if (currentLang == "vi") "Tổng tiền" else "Total Price"
    val checkoutLabel = if (currentLang == "vi") "Thanh toán" else "Checkout"

    val totalPrice = if (currentLang == "vi") totalAmountVnd else totalAmountUsd


    val profile by profileViewModel.profile.collectAsState()


    var showAddressDialog by remember { mutableStateOf(false) }

    if (showAddressDialog) {
        AddressConfirmationDialog(
            language = currentLang,
            onDismissRequest = { showAddressDialog = false },
            onConfirm = { newAddress ->
                showAddressDialog = false


                val addressToUse = if (newAddress.isNotBlank()) {
                    newAddress
                } else {
                    profile?.address ?: ""
                }

                profileViewModel.checkout(cartItems, addressToUse) { success ->
                    if (success) {
                        cartViewModel.clearCart()
                        navController.navigate("orderSuccess")
                    } else {
                        // TODO: Xử lý khi checkout thất bại (ví dụ: hiển thị Snackbar)
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                        textAlign = TextAlign.Center,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.size(40.dp).padding(start = 10.dp, bottom = 10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back",
                            modifier = Modifier
                                .size(50.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}, enabled = false) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(45.dp),
                            tint = Color.Transparent
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                CartBottomBar(
                    totalPrice = totalPrice,
                    language = currentLang,
                    priceLabel = totalPriceLabel,
                    checkoutLabel = checkoutLabel,
                    onCheckout = {
                        showAddressDialog = true
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (cartItems.isEmpty()) {
                EmptyCartView(language = currentLang)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(cartItems, key = { it.id }) { item ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                if (it == SwipeToDismissBoxValue.EndToStart) {
                                    cartViewModel.removeFromCart(item.id)
                                    true
                                } else {
                                    false
                                }
                            },
                            positionalThreshold = { it * 0.25f }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            enableDismissFromEndToStart = true,
                            backgroundContent = {
                                val color by animateColorAsState(
                                    targetValue = when (dismissState.targetValue) {
                                        SwipeToDismissBoxValue.EndToStart -> Color(0xFFFF6B6B)
                                        else -> Color(0xFFFFF0F0)
                                    },
                                    label = "background color"
                                )
                                val scale by animateFloatAsState(
                                    targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) 1.2f else 1f,
                                    label = "icon scale"
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(color)
                                        .padding(end = 24.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .background(Color(0xFFEDEFF5), shape = CircleShape)
                                            .scale(scale),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color(0xFFC14444)
                                        )
                                    }
                                }
                            }
                        ) {
                            CartListItem(item = item, language = currentLang)
                        }

                    }
                }
            }
        }
    }
}


@Composable
fun CartListItem(item: CartItem, language: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 1f),
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = item.coffee.imageRes),
                contentDescription = item.coffee.name,
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.coffee.name,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = generateOptionsString(item, language),
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = Color.Gray,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "x${item.quantity}",
                    fontFamily = Poppins,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            // Giá tiền
            Text(
                text = if (language == "vi") {
                    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                    formatter.format(item.totalPriceVnd)
                } else {
                    String.format(Locale.US, "$%.2f", item.totalPriceUsd)
                },
                fontFamily = Poppins,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@Composable
fun EmptyCartView(language: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.empty_cart),
                contentDescription = "Empty Cart",
                modifier = Modifier.size(650.dp).padding(bottom = 100.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun CartBottomBar(
    totalPrice: Double,
    language: String,
    priceLabel: String,
    checkoutLabel: String,
    onCheckout: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = priceLabel,
                    fontFamily = Poppins,
                    color = Color.Gray,
                    fontSize = 16.sp
                )
                Text(
                    text = if (language == "vi") {
                        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                        formatter.format(totalPrice)
                    } else {
                        String.format(Locale.US, "$%.2f", totalPrice)
                    },
                    fontFamily = Poppins,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Button(
                onClick = onCheckout,
                shape = CircleShape,
                modifier = Modifier
                    .height(56.dp)
                    .width(180.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF324A59),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = checkoutLabel,
                    fontFamily = Poppins,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}


private fun generateOptionsString(cartItem: CartItem, language: String): String {
    val options = mutableListOf<String>()
    options.add(
        if (language == "vi") {
            if (cartItem.selectedShotIndex == 0) "1 shot" else "2 shots"
        } else {
            if (cartItem.selectedShotIndex == 0) "single" else "double"
        }
    )
    options.add(if (language == "vi") (if (cartItem.isHotSelected) "nóng" else "lạnh") else (if (cartItem.isHotSelected) "hot" else "iced"))

    options.add(
        if (language == "vi") {
            when (cartItem.selectedSizeIndex) {
                0 -> "nhỏ"
                1 -> "vừa"
                else -> "lớn"
            }
        } else {
            when (cartItem.selectedSizeIndex) {
                0 -> "small"
                1 -> "medium"
                else -> "large"
            }
        }
    )
    if (!cartItem.isHotSelected) {
        val iceLevelString = if (language == "vi") {
            when (cartItem.selectedIceLevel) {
                1 -> "ít đá"
                2 -> "đá vừa"
                3 -> "nhiều đá"
                else -> "không đá"
            }
        } else {
            when (cartItem.selectedIceLevel) { //
                1 -> "less ice"
                2 -> "medium ice"
                3 -> "full ice"
                else -> "no ice"
            }
        }
        if (iceLevelString.isNotEmpty()) {
            options.add(iceLevelString)
        }
    }
    return options.filter { it.isNotEmpty() }.joinToString(" | ")
}


@Composable
fun AddressConfirmationDialog(
    language: String,
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    val title = if (language == "vi") "Nhập địa chỉ bạn muốn nhận hàng:" else "Please type in the address you want to pick up:"
    val note = if (language == "vi") "(Nếu để trống, hệ thống sẽ sử dụng địa chỉ mặc định của bạn.)" else "(If you leave this empty, your default address will be used.)"
    val example = if (language == "vi") "(Vd: 223 Nguyễn Văn Cừ, P.Chợ Quán, Q.5, TP.HCM)" else "(Ex: 223 Nguyen Van Cu Street, Cho Quan Ward, Dist 5, HCMC)"
    val addressLabel = if (language == "vi") "Địa chỉ" else "Address"
    val cancelLabel = if (language == "vi") "HỦY" else "CANCEL"
    val confirmLabel = if (language == "vi") "XÁC NHẬN" else "CONFIRM"

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = title,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column {
                Text(
                    text = note,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text(addressLabel) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = example,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text) }) {
                Text(confirmLabel, fontFamily = Poppins, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(cancelLabel, fontFamily = Poppins, fontWeight = FontWeight.Bold, color = Color.Gray)
            }
        }
    )
}