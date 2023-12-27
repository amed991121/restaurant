package com.savent.restaurant.ui.model.menu_category

import android.graphics.Typeface
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.Dish
import com.savent.restaurant.ui.component.RoundedBox
import com.savent.restaurant.ui.theme.DarkBlue

@Composable
fun SimpleMenuCategoryItem(menuCategory: MenuCategoryModel, onSelect: (Dish.Category) -> Unit) {
    val dim = 65.dp
    val pad = 9.dp
    Column(
        modifier = Modifier
            .requiredWidth(dim+6.dp)
            .wrapContentHeight()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() })
            { onSelect(menuCategory.type) }
    ) {
        RoundedBox(
            width = dim,
            height = dim,
            color = if (menuCategory.isSelected) DarkBlue else Color.LightGray.copy(alpha = 0.3f),
            cornerSize = 18.dp,
            content = {
                Icon(
                    painter = painterResource(id = menuCategory.resId),
                    contentDescription = null,
                    modifier = Modifier
                        .align(CenterHorizontally)
                        .padding(pad)
                        .fillMaxSize(),
                    tint = if (menuCategory.isSelected) Color.White else Color.Black
                )
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = menuCategory.categoryName,
            style = TextStyle(
                fontFamily = FontFamily(typeface = Typeface.DEFAULT_BOLD),
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
            ),
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .align(CenterHorizontally)
                .padding(start = 0.dp, end = 0.dp),
            maxLines = 1
        )
    }

}
