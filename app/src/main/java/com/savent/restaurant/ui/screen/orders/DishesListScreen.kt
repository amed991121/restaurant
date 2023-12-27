package com.savent.restaurant.ui.screen.orders

import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.Dish
import com.savent.restaurant.ui.component.SearchBar
import com.savent.restaurant.ui.model.dish.DishItemEvent
import com.savent.restaurant.ui.model.dish.DishModel
import com.savent.restaurant.ui.model.dish.DishUnitsItem
import com.savent.restaurant.ui.model.menu_category.MenuCategoryModel
import com.savent.restaurant.ui.model.menu_category.SimpleMenuCategoryItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DishesListScreen(
    dishes: List<DishModel>,
    categories: List<MenuCategoryModel>,
    onEvent: (DishesListScreenEvent) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf(Dish.Category.ALL) }

    Box(modifier = Modifier
        .fillMaxWidth()
        //.heightIn(550.dp,750.dp)
    ) {
        Box(Modifier.align(Alignment.TopEnd).padding(top = 8.dp, end = 8.dp)) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.LightGray.copy(alpha = 0.2f))
                    .align(
                        Alignment.TopEnd
                    )
                    .clickable { onEvent(DishesListScreenEvent.Close) }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .padding(2.dp)
                        .align(Alignment.Center),
                    tint = Color.Gray.copy(alpha = 0.5f)
                )
            }
        }
        Column(modifier = Modifier.padding(15.dp)) {
            Text(
                text = stringResource(id = R.string.dishes),
                style = MaterialTheme.typography.h4,
                modifier = Modifier
                    .padding(start = 5.dp)
            )
            Spacer(modifier = Modifier.height(15.dp))
            SearchBar(
                hint = stringResource(id = R.string.search),
                textStyle = TextStyle(
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Thin,
                    fontStyle = FontStyle.Italic
                )
            ) {
                query = it
                onEvent(DishesListScreenEvent.Search(query,category))
            }
            Spacer(modifier = Modifier.height(20.dp))
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categories) { item ->
                    SimpleMenuCategoryItem(menuCategory = item, onSelect = {
                        category = it
                        onEvent(DishesListScreenEvent.Search(query,category))
                    })
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp),
            ) {
                items(count = dishes.size, key = { dishes[it].id }, itemContent = {
                    Box(modifier = Modifier.animateItemPlacement(tween())){
                        DishUnitsItem(dish = dishes[it], onEvent = { event ->
                            when (event) {
                                is DishItemEvent.Add ->
                                    onEvent(DishesListScreenEvent.AddUnit(event.id))
                                is DishItemEvent.Remove ->
                                    onEvent(DishesListScreenEvent.RemoveUnit(event.id))
                            }
                        })
                    }
                })
            }
        }
    }

}


sealed class DishesListScreenEvent {
    object Close : DishesListScreenEvent()
    class Search(val query: String, val category: Dish.Category) : DishesListScreenEvent()
    class AddUnit(val id: Int) : DishesListScreenEvent()
    class RemoveUnit(val id: Int) : DishesListScreenEvent()
}
