package com.savent.restaurant.ui.screen.food

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import com.google.gson.Gson
import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.Dish
import com.savent.restaurant.ui.component.SearchBar
import com.savent.restaurant.ui.model.StaggeredColoredItem
import com.savent.restaurant.ui.model.dish.DishItem
import com.savent.restaurant.ui.model.dish.DishModel
import com.savent.restaurant.ui.model.menu_category.ColoredMenuCategoryItem
import com.savent.restaurant.ui.model.menu_category.MenuCategoryModel
import com.savent.restaurant.ui.model.menu_category.SimpleMenuCategoryItem
import com.savent.restaurant.ui.model.sale.SaleItem
import com.savent.restaurant.ui.screen.orders.ConfirmDialog
import com.savent.restaurant.ui.screen.orders.ConfirmEvent
import com.savent.restaurant.ui.screen.orders.OrdersDialog
import com.savent.restaurant.ui.theme.DarkBlue
import com.savent.restaurant.ui.theme.SaventRestaurantTheme
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun FoodScreen(
    state: FoodState,
    sgcList: List<StaggeredColoredItem>,
    onEvent: (FoodEvent) -> Unit,
    pullRefreshState: PullRefreshState,
    onClearSession: () -> Unit
) {

    var dishQuery by rememberSaveable { mutableStateOf("") }
    var dishCategory by rememberSaveable { mutableStateOf(Dish.Category.ALL) }

    val sheetState = rememberBottomSheetState(
        initialValue = BottomSheetValue.Collapsed
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )

    var dialog by remember { mutableStateOf(FoodMenuDialog.TablesAndOrders) }

    val scope = rememberCoroutineScope()
    Box(modifier = Modifier.pullRefresh(pullRefreshState).fillMaxHeight(0.9f)) {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = {
                when (dialog) {
                    FoodMenuDialog.TablesAndOrders -> TableAndOrdersScreen(
                        tablesAndOrders = state.tableAndOrders,
                        onEvent = { event ->
                            when (event) {
                                is TableAndOrdersScreenEvent.DishToOrder -> onEvent(
                                    FoodEvent.DishToCurrentOrder(
                                        event.orderId,
                                        event.isAdded
                                    )
                                )
                                is TableAndOrdersScreenEvent.DishToTable -> onEvent(
                                    FoodEvent.DishToTable(
                                        event.tableId,
                                        event.isAdded
                                    )
                                )
                                TableAndOrdersScreenEvent.Close -> scope.launch {
                                    sheetState.collapse()

                                }
                                TableAndOrdersScreenEvent.SaveDishOrder -> onEvent(FoodEvent.SaveDishOrder)
                                is TableAndOrdersScreenEvent.Search -> onEvent(
                                    FoodEvent.SearchTablesAndOrders(
                                        event.query
                                    )
                                )
                            }
                        },
                    )
                    FoodMenuDialog.ConfirmExit -> ConfirmDialog(onEvent = { event ->
                        when (event) {
                            ConfirmEvent.Accept -> onClearSession()
                            ConfirmEvent.Cancel -> scope.launch { sheetState.collapse() }
                            ConfirmEvent.Close -> scope.launch { sheetState.collapse() }
                        }

                    })
                }

            },
            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            sheetBackgroundColor = Color.White,
            sheetPeekHeight = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 20.dp, start = 20.dp, end = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_user),
                        contentDescription = null,
                        modifier = Modifier
                            .size(35.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray.copy(alpha = 0.2f))
                            .border(BorderStroke(3.dp, DarkBlue), CircleShape),
                        tint = DarkBlue
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        modifier = Modifier.weight(1f).padding(bottom = 3.dp),
                        text = state.employeeName,
                        fontSize = 34.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(
                        painter = painterResource(id = R.drawable.ic_exit),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .clickable {
                                dialog = FoodMenuDialog.ConfirmExit
                                scope.launch { sheetState.expand() }
                            }
                            .background(Color.LightGray.copy(alpha = 0.3f))
                            .padding(10.dp),
                        tint = Color.DarkGray
                    )

                }
                Spacer(modifier = Modifier.height(10.dp))
                SearchBar(
                    hint = stringResource(id = R.string.search), textStyle = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Thin,
                        fontStyle = FontStyle.Italic
                    ),
                    onValueChange = {
                        dishQuery = it
                        onEvent(FoodEvent.SearchDishes(query = dishQuery, category = dishCategory))
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
                FilterMenuCategoryList(items = state.categoriesListWithIcon, onSelect = {
                    dishCategory = it
                    onEvent(FoodEvent.SearchDishes(query = dishQuery, category = dishCategory))
                })
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = stringResource(id = R.string.menu),
                    style = MaterialTheme.typography.h3.copy(fontFamily = FontFamily.Cursive),
                )
                Spacer(modifier = Modifier.height(10.dp))
                AnimatedVisibility(
                    visible = dishCategory != Dish.Category.ALL || dishQuery.isNotEmpty(),
                    enter = scaleIn(),
                    exit = ExitTransition.None
                ) {
                    GridDishesList(dishes = state.dishes, onSelect = {
                        onEvent(FoodEvent.SelectDish(it))
                        scope.launch {
                            sheetState.expand()
                        }
                    })
                }
                AnimatedVisibility(
                    visible = dishCategory == Dish.Category.ALL && dishQuery.isEmpty(),
                    enter = fadeIn(),
                    exit = ExitTransition.None
                ) {
                    GridMenuCategoryList(items = state.categoriesListWithImage, onSelect = {
                        dishCategory = it
                        onEvent(FoodEvent.SearchDishes(query = dishQuery, category = dishCategory))
                    }, sgcList = sgcList)
                }

            }
        }

        PullRefreshIndicator(
            refreshing = state.isLoading,
            state = pullRefreshState,
            modifier = Modifier.align(
                Alignment.TopCenter
            )
        )
    }


}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GridMenuCategoryList(
    items: List<MenuCategoryModel>,
    onSelect: (Dish.Category) -> Unit,
    sgcList: List<StaggeredColoredItem>
) {

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(0.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalItemSpacing = 16.dp,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {

        items(
            count = items.size,
            key = { items[it].type },
            itemContent = {
                ColoredMenuCategoryItem(
                    menuCategory = items[it],
                    sgcItem = sgcList[it],
                    onSelect = { category ->  onSelect(category) })
            })
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GridDishesList(dishes: List<DishModel>, onSelect: (Int) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(0.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        items(
            count = dishes.size,
            key = { idx -> dishes[idx].id },
            itemContent = { idx ->
                Box(modifier = Modifier.animateItemPlacement(tween())) {
                    DishItem(
                        dish = dishes[idx],
                        onClick = {
                            onSelect(it)
                        })
                }
            }
        )


    }
}

@Composable
private fun FilterMenuCategoryList(
    items: List<MenuCategoryModel>,
    onSelect: (Dish.Category) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items) { item ->
            SimpleMenuCategoryItem(menuCategory = item, onSelect = { onSelect(it) })
        }
    }
}

enum class FoodMenuDialog {
    TablesAndOrders, ConfirmExit
}


