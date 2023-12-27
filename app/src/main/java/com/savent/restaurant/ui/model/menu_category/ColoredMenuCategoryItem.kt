package com.savent.restaurant.ui.model.menu_category

import android.graphics.Typeface
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Alignment.Companion.Top
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savent.restaurant.data.common.model.Dish
import com.savent.restaurant.ui.component.RoundedBox
import com.savent.restaurant.ui.model.StaggeredColoredItem

@Composable
fun ColoredMenuCategoryItem(
    menuCategory: MenuCategoryModel,
    sgcItem: StaggeredColoredItem,
    onSelect: (Dish.Category) -> Unit
) {
    RoundedBox(
        height = sgcItem.height,
        color = sgcItem.color,
        cornerSize = 20.dp,
        content = { Content(menuCategory = menuCategory, onSelect = onSelect) }
    )
}

@Composable
private fun Content(menuCategory: MenuCategoryModel, onSelect: (Dish.Category) -> Unit) {
    Column(
        modifier = Modifier
            .wrapContentWidth(Start)
            .wrapContentHeight(Top)
            .padding(top = 10.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) {
                onSelect(menuCategory.type)
            }
    ) {
        Text(
            text = menuCategory.categoryName,
            style = TextStyle(
                fontFamily = FontFamily(typeface = Typeface.DEFAULT_BOLD),
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
            ),
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(start = 15.dp, end = 20.dp),
            maxLines = 1
        )
        Image(
            painter = painterResource(id = menuCategory.resId),
            contentDescription = null,
            alignment = Center,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp)
        )

    }
}
