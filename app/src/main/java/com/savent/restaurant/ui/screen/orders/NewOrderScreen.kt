package com.savent.restaurant.ui.screen.orders

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.Top
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savent.restaurant.R
import com.savent.restaurant.ui.model.dish.DishItemEvent
import com.savent.restaurant.ui.model.dish.DishUnitsItem
import com.savent.restaurant.ui.screen.ListScreenEvent
import com.savent.restaurant.ui.theme.DarkBlue
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun NewOrderScreen(
    state: OrdersState,
    onEvent: (OrdersEvent) -> Unit,
    navEvent: (OrderNavEvent) -> Unit
) {
    var dialog by remember { mutableStateOf(OrdersDialog.Tables) }

    val sheetState = rememberBottomSheetState(
        initialValue = BottomSheetValue.Collapsed
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )
    val scope = rememberCoroutineScope()

    val lazyListState = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.91f)
    ) {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = {
                when (dialog) {
                    OrdersDialog.Tables ->
                        Box(modifier = Modifier.fillMaxHeight(0.98f)) {
                            TablesListScreen(tables = state.tables, onEvent = { event ->
                                when (event) {
                                    ListScreenEvent.Close ->
                                        scope.launch { sheetState.collapse() }
                                    is ListScreenEvent.Search ->
                                        onEvent(OrdersEvent.SearchTables(event.query))
                                    is ListScreenEvent.Select ->
                                        onEvent(OrdersEvent.SelectTable(event.id))
                                }
                            })
                        }
                    OrdersDialog.AddOrderTag -> {
                        AddTagDialog(tag = state.order.tag, onEvent = { event ->
                            when (event) {
                                AddTagEvent.Close -> scope.launch { sheetState.collapse() }
                                is AddTagEvent.Save -> {
                                    onEvent(OrdersEvent.AddOrderTag(event.tag))
                                }
                                AddTagEvent.ShowDiners -> {
                                    scope.launch {
                                        sheetState.collapse()
                                        dialog = OrdersDialog.Diners
                                        sheetState.expand()
                                    }
                                }
                            }

                        })
                    }
                    OrdersDialog.Diners ->
                        Box(modifier = Modifier.fillMaxHeight(0.98f)) {
                            DinersListScreen(diners = state.diners, onEvent = { event ->
                                when (event) {
                                    ListScreenEvent.Close ->
                                        scope.launch { sheetState.collapse() }
                                    is ListScreenEvent.Search ->
                                        onEvent(OrdersEvent.SearchDiners(event.query))
                                    is ListScreenEvent.Select ->
                                        onEvent(OrdersEvent.SelectDiner(event.id))
                                }
                            })
                        }
                    OrdersDialog.Dishes ->
                        Box(modifier = Modifier.fillMaxHeight(0.98f)) {
                            DishesListScreen(
                                dishes = state.dishes,
                                categories = state.menuCategories,
                                onEvent = { event ->
                                    when (event) {
                                        is DishesListScreenEvent.AddUnit -> onEvent(
                                            OrdersEvent.AddDishUnit(
                                                dishId = event.id,
                                                isPersistent = false
                                            )
                                        )
                                        DishesListScreenEvent.Close -> scope.launch { sheetState.collapse() }
                                        is DishesListScreenEvent.RemoveUnit -> onEvent(
                                            OrdersEvent.RemoveDishUnit(
                                                dishId = event.id,
                                                isPersistent = false
                                            )
                                        )
                                        is DishesListScreenEvent.Search -> onEvent(
                                            OrdersEvent.SearchDishes(
                                                event.query,
                                                event.category
                                            )
                                        )
                                    }
                                })
                        }

                    else -> {}
                }
            },
            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            sheetBackgroundColor = Color.White,
            sheetPeekHeight = 0.dp
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 20.dp, end = 20.dp),
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .wrapContentSize()
                                .clip(RoundedCornerShape(15.dp))
                                .background(Color.LightGray.copy(alpha = 0.2f))
                                .clickable { navEvent(OrderNavEvent.Back) },

                            ) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(50.dp)
                                    .padding(10.dp),
                                tint = Color.Gray,
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(id = R.string.new_order),
                            style = MaterialTheme.typography.h3.copy(fontFamily = FontFamily.Cursive),
                            modifier = Modifier
                                .padding(start = 5.dp)
                                .align(Top)
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.LightGray.copy(alpha = 0.35f))
                            .clickable {
                                dialog = OrdersDialog.Tables
                                scope.launch { sheetState.expand() }
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.add_circle),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp),
                                tint = Color.Gray.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = state.order.tableName.ifEmpty { stringResource(id = R.string.add_table) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Thin,
                                    fontStyle = FontStyle.Italic
                                ),
                                color = Color.Gray
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.LightGray.copy(alpha = 0.35f))
                            .clickable {
                                dialog = OrdersDialog.AddOrderTag
                                scope.launch { sheetState.expand() }
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_user),
                                contentDescription = null,
                                modifier = Modifier.size(28.dp),
                                tint = Color.Gray.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = state.order.tag.ifEmpty { stringResource(id = R.string.in_name_of) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Thin,
                                    fontStyle = FontStyle.Italic
                                ),
                                color = Color.Gray
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                            .align(CenterHorizontally)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.LightGray.copy(alpha = 0.35f))
                            .clickable {
                                dialog = OrdersDialog.Dishes
                                scope.launch { sheetState.expand() }
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AddCircle,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(30.dp),
                                tint = DarkBlue
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(id = R.string.add_new_dish),
                                modifier = Modifier
                                    .wrapContentSize(),
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Thin,
                                    fontStyle = FontStyle.Italic
                                ),
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier
                            .wrapContentSize()
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(15.dp),
                    ) {
                        items(
                            count = state.order.dishes.size,
                            key = { state.order.dishes[it].id },
                            itemContent = {
                                Box(modifier = Modifier.animateItemPlacement(tween())) {
                                    DishUnitsItem(
                                        dish = state.order.dishes[it],
                                        onEvent = { event ->
                                            when (event) {
                                                is DishItemEvent.Add ->
                                                    onEvent(
                                                        OrdersEvent.AddDishUnit(
                                                            dishId = event.id,
                                                            isPersistent = false
                                                        )
                                                    )
                                                is DishItemEvent.Remove ->
                                                    onEvent(
                                                        OrdersEvent.RemoveDishUnit(
                                                            dishId = event.id,
                                                            isPersistent = false
                                                        )
                                                    )
                                            }

                                        })
                                }
                            })
                    }

                }
                AnimatedVisibility(
                    lazyListState.canScrollForward || state.order.dishes.isEmpty()
                            || (!lazyListState.canScrollForward && !lazyListState.canScrollBackward),
                    enter = scaleIn(),
                    exit = scaleOut(),
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.BottomEnd)
                        .padding(end = 20.dp, bottom = 33.dp)
                ) {
                    FloatingActionButton(
                        onClick = {
                            onEvent(OrdersEvent.CreateOrder)
                        },
                        backgroundColor = DarkBlue,
                        elevation =
                        FloatingActionButtonDefaults.elevation(defaultElevation = 12.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .padding(8.dp),
                            tint = Color.White
                        )
                    }
                }

            }
        }
    }

}

enum class OrdersDialog {
    Tables, AddOrderTag, Diners, Dishes, Action, Confirm, PaymentMethod, SendDishesToPrinter
}
