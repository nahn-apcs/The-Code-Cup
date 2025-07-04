package com.example.coffee_application.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.coffee_application.R

enum class BottomBarItem(val label: String, val filledRes: Int, val emptyRes: Int) {
    HOME("Home", R.drawable.ic_home_filled, R.drawable.ic_home_empty),
    GIFT("Gift", R.drawable.ic_gift_filled, R.drawable.ic_gift_empty),
    BILL("Bill", R.drawable.ic_receipt_filled, R.drawable.ic_receipt_empty)
}

@Composable
fun BottomNavigationBar(
    selectedItem: BottomBarItem,
    onItemSelected: (BottomBarItem) -> Unit
) {
    Surface(
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        border = BorderStroke(2.dp, Color.Gray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),

            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomBarItem.values().forEach { item ->
                val icon = if (item == selectedItem) item.filledRes else item.emptyRes

                Image(
                    painter = painterResource(id = icon),
                    contentDescription = item.label,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .size(28.dp)
                        .clickable { onItemSelected(item) }
                )
            }
        }
    }
}

