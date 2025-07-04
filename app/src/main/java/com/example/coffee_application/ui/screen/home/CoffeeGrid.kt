package com.example.coffee_application.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.coffee_application.data.coffeeOptions


@Composable
fun CoffeeGridSection(
    language: String,
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val title = if (language == "vi") "Khám phá cà phê" else "Choose your coffee"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF324A59), shape = RoundedCornerShape(24.dp))
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontFamily = Poppins,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .drawBehind {
                    val strokeWidth = 3.dp.toPx()
                    drawLine(
                        color = Color(0xFFFFF8E1),
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = strokeWidth
                    )
                }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column {
            coffeeOptions.chunked(2).forEach { rowItems ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rowItems.forEach { item ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFFFFBF5),
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 8.dp)
                                .clickable {
                                    navController.navigate("details/${item.name}")
                                }
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = item.imageRes),
                                    contentDescription = item.name,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .padding(bottom = 8.dp)
                                )
                                Text(
                                    text = item.name,
                                    color = Color(0xFF3E2723),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                    if (rowItems.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}