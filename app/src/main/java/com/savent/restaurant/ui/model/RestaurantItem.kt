package com.savent.restaurant.ui.model.restaurant

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savent.restaurant.ui.component.RoundedBox
import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.Restaurant

@Composable
fun RestaurantItem(restaurant: Restaurant, onClick: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick(restaurant.id) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoundedBox(
            width = 42.dp,
            height = 42.dp,
            cornerSize = 15.dp,
            Color.LightGray.copy(alpha = 0.2f),
            content = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_store),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(6.dp)
                        .fillMaxSize(),
                    tint = Color.Gray
                )
            }
        )

        Text(
            fontSize = 19.sp,
            text = restaurant.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(start = 15.dp, end = 15.dp)
                .align(Alignment.CenterVertically)
        )

    }
}