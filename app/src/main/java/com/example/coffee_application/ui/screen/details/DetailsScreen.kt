package com.example.coffee_application.ui.screen.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coffee_application.LanguageViewModel
import com.example.coffee_application.R
import com.example.coffee_application.model.CartItem
import com.example.coffee_application.viewmodel.CartViewModel
import com.example.coffee_application.viewmodel.DetailsViewModel
import kotlinx.coroutines.launch
import java.util.Locale

val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_light, FontWeight.Light),
    Font(R.font.poppins_medium, FontWeight.Medium),
)

val DmSans = FontFamily(
    Font(R.font.dmsan_medium, FontWeight.Medium),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    coffeeName: String,
    navController: NavController,
    languageViewModel: LanguageViewModel,
    cartViewModel: CartViewModel,
    detailsViewModel: DetailsViewModel = viewModel()
) {

    // === THÊM MÃ NÀY ===
    // State để điều khiển việc hiển thị BottomSheet
    val sheetState = rememberModalBottomSheetState()
    var showCartPreview by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    // Lấy danh sách các sản phẩm trong giỏ hàng
    val cartItemsList by cartViewModel.cartItems.collectAsState()
    // === KẾT THÚC THÊM MÃ ===

    val currentLang by languageViewModel.language.collectAsState()
    val coffeeItem by detailsViewModel.coffeeItem.collectAsState()



    LaunchedEffect(key1 = coffeeName) {
        detailsViewModel.loadCoffeeItem(coffeeName)
    }

    if (showCartPreview) {
        CartPreviewSheet(
            cartItems = cartItemsList,
            language = currentLang,
            sheetState = sheetState,
            onDismiss = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showCartPreview = false
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
                        if(currentLang == "vi") "Chi tiết" else "Details",
                        modifier = Modifier.fillMaxWidth(),
                        fontFamily = Poppins,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        fontSize = 24.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.size(40.dp).padding(start = 10.dp) // Thêm padding để căn chỉnh
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_back), // Tải ảnh từ drawable
                            contentDescription = "Back", // Mô tả cho mục đích hỗ trợ tiếp cận
                            modifier = Modifier
                                .size(50.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        showCartPreview = true
                    }) {
                        Icon(
                            Icons.Outlined.ShoppingCart,
                            contentDescription = "Cart",
                            // Áp dụng animation scale vào icon
                            modifier = Modifier
                                .size(30.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                //modifier = Modifier.padding(top = 50.dp)
            )
        },
        bottomBar = {
            BottomBar(
                detailsViewModel = detailsViewModel,
                languageViewModel = languageViewModel,
                onAddToCart = {
                    // Logic khi nhấn nút "Add to Cart"
                    val coffee = detailsViewModel.coffeeItem.value
                    val quantity = detailsViewModel.quantity.value
                    val sizeIndex = detailsViewModel.selectedSizeIndex.value

                    if (coffee != null && quantity > 0) {
                        val cartItem = CartItem(
                            coffee = coffee,
                            quantity = quantity,
                            selectedShotIndex = detailsViewModel.selectedShotIndex.value,
                            isHotSelected = detailsViewModel.isHotSelected.value,
                            selectedSizeIndex = sizeIndex,
                            selectedIceLevel = detailsViewModel.selectedIceLevel.value,
                            pricePerItemUsd = coffee.pricesUsd.getOrElse(sizeIndex) { 0.0 },
                            pricePerItemVnd = coffee.pricesVnd.getOrElse(sizeIndex) { 0.0 }
                        )
                        cartViewModel.addToCart(cartItem)
                    }
                    navController.navigate("cart")
                }
            )
        }
    ) { paddingValues ->
        coffeeItem?.let { item ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Image
                item {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xFFEDEFF5),
                        // Thêm viền ở đây
                        border = BorderStroke(1.dp, Color(0xFFF2F2F2)), // <-- THÊM VIỀN VÀ MÀU XÁM MỚI
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth()
                            .height(125.dp)
                    ) {
                        Image(
                            painter = painterResource(id = item.imageRes),
                            contentDescription = item.name,
                            modifier = Modifier.padding(16.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                // Name and Quantity
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.name,
                            fontFamily = DmSans,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        QuantitySelector(detailsViewModel)
                    }
                }

                item { Divider(modifier = Modifier.padding(horizontal = 24.dp)) }

                // Options
                item { OptionShot(detailsViewModel, currentLang) }
                item { Divider(modifier = Modifier.padding(horizontal = 24.dp)) }
                item { OptionSelect(detailsViewModel, currentLang) }
                item { Divider(modifier = Modifier.padding(horizontal = 24.dp)) }
                item { OptionSize(detailsViewModel, currentLang) }
                item { Divider(modifier = Modifier.padding(horizontal = 24.dp)) }
                item { OptionIce(detailsViewModel, currentLang) }
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

// --- Các Composable cho từng lựa chọn ---

@Composable
fun OptionRow(label: String, content: @Composable RowScope.() -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            content = content
        )
    }
}

@Composable
fun OptionShot(viewModel: DetailsViewModel, language: String) {
    val selectedIndex by viewModel.selectedShotIndex.collectAsState()
    val shotOptions = if (language == "vi") listOf("1 shot", "2 shots") else listOf("Single", "Double")

    // 1. Dùng Row làm layout chính cho cả hàng
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp), // Padding cho cả hàng
        verticalAlignment = Alignment.CenterVertically // Căn giữa các phần tử theo chiều dọc
    ) {
        // 2. "Shot" label nằm bên trái
        Text(
            text = "Shot",
            fontFamily = DmSans,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        // 3. Spacer chiếm hết không gian thừa để đẩy các nút sang phải
        Spacer(modifier = Modifier.weight(1f))

        // 4. Một Row khác để nhóm 2 nút bấm lại với nhau
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            shotOptions.forEachIndexed { index, text ->
                SelectableTextButton(
                    onClick = { viewModel.setShot(index) },
                    text = text,
                    isSelected = selectedIndex == index
                )
            }
        }
    }
}

@Composable
fun OptionSelect(viewModel: DetailsViewModel, language: String) {
    val isHot by viewModel.isHotSelected.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (language == "vi") "Chọn" else "Select",
            fontFamily = DmSans,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Cung cấp kích thước cố định cho các nút này
            SelectableIconOnlyButton(
                onClick = { viewModel.setIsHot(true) },
                iconRes = R.drawable.ic_hot_filled,
                isSelected = isHot,
                buttonSize = 50.dp,
                iconSize = 24.dp
            )
            SelectableIconOnlyButton(
                onClick = { viewModel.setIsHot(false) },
                iconRes = R.drawable.ic_cold_filled,
                isSelected = !isHot,
                buttonSize = 50.dp,
                iconSize = 30.dp
            )
        }
    }
}

@Composable
fun OptionSize(viewModel: DetailsViewModel, language: String) {
    val selectedIndex by viewModel.selectedSizeIndex.collectAsState()

    // Định nghĩa các icon và kích thước tăng dần tương ứng
    // Gồm: (Icon Resource, Kích thước nút, Kích thước icon)
    val sizeOptions = listOf(
        Triple(R.drawable.ic_size_filled, 50.dp, 20.dp),
        Triple(R.drawable.ic_size_filled, 50.dp, 28.dp),
        Triple(R.drawable.ic_size_filled, 50.dp, 34.dp)
    )

    // Áp dụng layout ngang
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (language == "vi") "Kích cỡ" else "Size",
            fontSize = 18.sp,
            fontFamily = DmSans,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically // Căn các nút có size khác nhau
        ) {
            sizeOptions.forEachIndexed { index, (iconRes, buttonSize, iconSize) ->
                SelectableIconOnlyButton(
                    onClick = { viewModel.setSize(index) },
                    iconRes = iconRes,
                    isSelected = selectedIndex == index,
                    // Truyền kích thước tăng dần vào nút
                    buttonSize = buttonSize,
                    iconSize = iconSize
                )
            }
        }
    }
}

@Composable
fun OptionIce(viewModel: DetailsViewModel, language: String) {
    val isHot by viewModel.isHotSelected.collectAsState()
    val selectedLevel by viewModel.selectedIceLevel.collectAsState()
    val isEnabled = !isHot

    // Định nghĩa các icon và kích thước tăng dần
    // Gồm: (Icon Resource, Kích thước nút, Kích thước icon)
    val iceOptions = listOf(
        Triple(R.drawable.ic_ice1_filled, 50.dp, 14.dp),
        Triple(R.drawable.ic_ice2_filled, 50.dp, 28.dp),
        Triple(R.drawable.ic_ice3_filled, 50.dp, 28.dp)
    )

    // Áp dụng layout ngang, và làm mờ nếu không được phép chọn
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .alpha(if (isEnabled) 1f else 0.4f), // Làm mờ nếu là đồ uống nóng
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (language == "vi") "Đá" else "Ice",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            iceOptions.forEachIndexed { index, (iconRes, buttonSize, iconSize) ->
                val level = index + 1 // Mức đá là 1, 2, 3
                SelectableIconOnlyButton(
                    onClick = {
                        if (isEnabled) {
                            // Nếu nhấn vào icon đang được chọn -> bỏ chọn (level = 0)
                            // Ngược lại -> chọn level mới
                            val newLevel = if (selectedLevel == level) 0 else level
                            viewModel.setIceLevel(newLevel)
                        }
                    },
                    iconRes = iconRes,
                    isSelected = selectedLevel == level,
                    buttonSize = buttonSize,
                    iconSize = iconSize
                )
            }
        }
    }
}

// --- Các Composable con dùng để xây dựng UI ---

@Composable
fun SelectableTextButton(onClick: () -> Unit, text: String, isSelected: Boolean) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF324A59) else Color.Transparent,
            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
        ),
        border = if (!isSelected) BorderStroke(1.dp, Color.LightGray) else null,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(
            text,
            fontFamily = DmSans,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
        )
    }
}

@Composable
fun SelectableIconOnlyButton(
    onClick: () -> Unit,
    iconRes: Int,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    // Thêm 2 tham số để tùy chỉnh kích thước
    buttonSize: Dp = 50.dp,
    iconSize: Dp = 24.dp
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) Color(0xFF324A59) else Color.LightGray.copy(alpha = 0.3f),
        // Sử dụng kích thước từ tham số
        modifier = modifier.size(buttonSize),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                // Sử dụng kích thước từ tham số
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@Composable
fun QuantitySelector(viewModel: DetailsViewModel) {
    val quantity by viewModel.quantity.collectAsState()
    Surface(
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, Color.LightGray),
        color = Color.Transparent
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            // Giảm khoảng cách giữa các phần tử
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            // Giảm padding bên trong
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
        ) {
            // Thu nhỏ nút bấm
            IconButton(
                onClick = { viewModel.setQuantity(quantity - 1) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.Remove,
                    contentDescription = "Decrease",
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = quantity.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            // Thu nhỏ nút bấm
            IconButton(
                onClick = { viewModel.setQuantity(quantity + 1) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Increase",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun BottomBar(
    detailsViewModel: DetailsViewModel,
    languageViewModel: LanguageViewModel,
    onAddToCart: () -> Unit
) {
    val totalAmountUsd by detailsViewModel.totalAmountUsd.collectAsState()
    val totalAmountVnd by detailsViewModel.totalAmountVnd.collectAsState()
    val currentLang by languageViewModel.language.collectAsState()

    val (priceText, totalLabel, buttonText) = if (currentLang == "vi") {
        val formatter = java.text.NumberFormat.getIntegerInstance(Locale("vi", "VN"))
        Triple(formatter.format(totalAmountVnd) + "₫", "Tổng tiền", "Thêm vào giỏ hàng")
    } else {
        Triple(String.format(Locale.US, "$%.2f", totalAmountUsd), "Total Amount", "Add to cart")
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        //color = Color.White // Đảm bảo nền là màu trắng
    ) {
        // Dùng Column để xếp 2 hàng chồng lên nhau
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp) // Khoảng cách giữa 2 hàng
        ) {
            // Hàng 1: Chứa Total Amount và Giá tiền
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    fontFamily = Poppins,
                    text = totalLabel,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    fontFamily = Poppins,
                    text = priceText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Hàng 2: Chứa nút Add to cart
            Button(
                onClick = onAddToCart,
                shape = CircleShape,
                modifier = Modifier
                    .fillMaxWidth() // Nút chiếm hết chiều ngang
                    .padding(top = 10.dp, bottom = 8.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF324A59),
                    contentColor = Color.White
                )
            ) {
                Text(
                    fontFamily = Poppins,
                    text = buttonText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartPreviewSheet(
    cartItems: List<CartItem>,
    language: String,
    sheetState: androidx.compose.material3.SheetState,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (language == "vi") "Giỏ Hàng" else "My Cart",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Poppins
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (cartItems.isEmpty()) {
                Text(
                    text = if (language == "vi") "Giỏ hàng của bạn trống" else "Your cart is empty",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 32.dp)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    items(cartItems, key = { it.id }) { item ->
                        CartPreviewItem(item = item, language = language)
                    }
                }
            }
        }
    }
}

@Composable
fun CartPreviewItem(item: CartItem, language: String) {
    // Hàm helper để tạo chuỗi mô tả các lựa chọn
    fun generateOptionsString(cartItem: CartItem): String {
        val options = mutableListOf<String>()
        // Dịch "single/double"
        options.add(
            if (language == "vi") {
                if (cartItem.selectedShotIndex == 0) "1 shot" else "2 shots"
            } else {
                if (cartItem.selectedShotIndex == 0) "single" else "double"
            }
        )
        // Dịch "hot/iced"
        options.add(if (language == "vi") (if (cartItem.isHotSelected) "nóng" else "lạnh") else (if (cartItem.isHotSelected) "hot" else "iced"))

        // Dịch "small/medium/large"
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
        // Dịch mức đá cho đồ uống lạnh
        if (!cartItem.isHotSelected) {
            val iceLevelString = if (language == "vi") {
                when (cartItem.selectedIceLevel) {
                    0 -> "không đá"
                    1 -> "ít đá"
                    2 -> "đá vừa"
                    3 -> "nhiều đá"
                    else -> ""
                }
            } else {
                when (cartItem.selectedIceLevel) {
                    0 -> "no ice"
                    1 -> "less ice"
                    2 -> "medium ice"
                    3 -> "full ice"
                    else -> ""
                }
            }
            if (iceLevelString.isNotEmpty()) {
                options.add(iceLevelString)
            }
        }
        return options.filter { it.isNotEmpty() }.joinToString(" | ")
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF7F7F7),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = item.coffee.imageRes),
                contentDescription = item.coffee.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.coffee.name,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    fontFamily = Poppins
                )
                Text(
                    text = generateOptionsString(item),
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontFamily = Poppins
                )
                Text(
                    text = "x${item.quantity}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = Poppins
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (language == "vi") {
                    val formatter = java.text.NumberFormat.getIntegerInstance(Locale("vi", "VN"))
                    "${formatter.format(item.totalPriceVnd)}₫"
                } else {
                    String.format(Locale.US, "$%.2f", item.totalPriceUsd)
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins
            )
        }
    }
}