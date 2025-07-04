package com.example.coffee_application.ui.screen.home

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.coffee_application.LanguageViewModel
import com.example.coffee_application.R
import com.example.coffee_application.data.CoffeeItem
import com.example.coffee_application.data.coffeeOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

val bestSellerItems = coffeeOptions.take(4)

val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_light, FontWeight.Light),
    Font(R.font.poppins_medium, FontWeight.Medium),
)

@Composable
fun BestSellerCarousel(
    items: List<CoffeeItem>,
    languageViewModel: LanguageViewModel,
    navController: NavController,
) {
    val language by languageViewModel.language.collectAsState()
    val itemWidth = 320.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val contentPadding = (screenWidth - itemWidth) / 2

    // Vòng lặp vô hạn để tạo hiệu ứng cuộn tròn
    val pagerState = rememberPagerState(
        initialPage = items.size * 500, // Bắt đầu ở giữa để cuộn được 2 chiều
        pageCount = { Int.MAX_VALUE }
    )
    val scope = rememberCoroutineScope()

    // Tự động cuộn sau mỗi 3 giây
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            scope.launch {
                pagerState.animateScrollToPage(
                    page = pagerState.currentPage + 1,
                    // Thêm dòng này để kiểm soát tốc độ
                    animationSpec = tween(durationMillis = 1000)
                )
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
    ) {
        Text(
            text = ("☕ Best Seller"),
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF2F3E46),
            modifier = Modifier.padding(start = 24.dp, bottom = 12.dp),
            fontFamily = Poppins,
            fontWeight = FontWeight.Medium,
        )

        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = contentPadding),
            pageSpacing = 16.dp,
        ) { page ->
            val item = items[page % items.size] // Lấy item tương ứng

            // Tính toán độ lệch so với trang trung tâm
            val pageOffset = (
                    (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                    ).absoluteValue

            // Dùng độ lệch để tính toán hiệu ứng phóng to/thu nhỏ
            val scale = lerp(
                start = 0.75f,
                stop = 1.1f,
                fraction = 1f - pageOffset.coerceIn(0f, 1f)
            )

            CarouselItem(
                item = item,
                language = language,
                scale = scale,
                onClick = { navController.navigate("details/${item.name}") }
            )
        }
    }
}

@Composable
fun CarouselItem(
    item: CoffeeItem,
    language: String,
    scale: Float,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFFFF3E0),
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onClick() })
            }
            .width(320.dp)
            .height(120.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = item.name,
                modifier = Modifier.padding(start = 26.dp, end = 16.dp).size(80.dp).clip(RoundedCornerShape(12.dp))
            )
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Text(
                    fontFamily = Poppins,
                    text = item.name,
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF324A59)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (language == "vi") {
                        val price = item.pricesVnd.first().toInt()
                        "${price}₫"
                    } else {
                        val price = item.pricesUsd.first()
                        "$${price}"
                    },
                    fontSize = 14.sp,
                    color = Color(0xFFB98068)
                )
            }
        }
    }
}

// Hàm nội suy tuyến tính để tính giá trị trung gian
fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return (1 - fraction) * start + fraction * stop
}