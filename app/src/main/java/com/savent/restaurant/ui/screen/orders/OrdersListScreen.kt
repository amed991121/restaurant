package com.savent.restaurant.ui.screen.orders

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savent.restaurant.ui.component.SearchBar
import com.savent.restaurant.ui.model.table_and_order.TableAndOrderItem
import com.savent.restaurant.R
import com.savent.restaurant.ui.theme.DarkBlue
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalAnimationApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun OrdersListScreen(
    state: OrdersState,
    onEvent: (OrdersEvent) -> Unit,
    navEvent: (OrderNavEvent) -> Unit,
    pullRefreshState: PullRefreshState
) {
    var dialog by remember { mutableStateOf(OrdersDialog.Action) }

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
            .pullRefresh(pullRefreshState)
    ) {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = {
                when (dialog) {
                    OrdersDialog.Action -> OrderActionDialog(onEvent = { event ->
                        when (event) {
                            OrderActionEvent.Close -> scope.launch { sheetState.collapse() }
                            OrderActionEvent.Delete -> {
                                scope.launch {
                                    sheetState.collapse()
                                    dialog = OrdersDialog.Confirm
                                    sheetState.expand()
                                }
                            }
                            OrderActionEvent.Edit -> {
                                navEvent(OrderNavEvent.ToRegisterOrder)
                                scope.launch { sheetState.collapse() }
                            }
                        }
                    })
                    OrdersDialog.Confirm -> {
                        ConfirmDialog(onEvent = { event ->
                            when (event) {
                                ConfirmEvent.Accept -> scope.launch {
                                    onEvent(OrdersEvent.DeleteOrder)
                                    sheetState.collapse()
                                }
                                ConfirmEvent.Cancel -> scope.launch { sheetState.collapse() }
                                ConfirmEvent.Close -> scope.launch { sheetState.collapse() }
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(modifier = Modifier.width(5.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_order1),
                            contentDescription = null,
                            modifier = Modifier.size(50.dp),
                            tint = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = stringResource(id = R.string.orders),
                            style = MaterialTheme.typography.h3.copy(fontFamily = FontFamily.Cursive),
                            modifier = Modifier.padding(start = 5.dp)
                        )
                    }

                    Spacer(modifier = Modifier.padding(top = 15.dp))
                    SearchBar(
                        hint = stringResource(id = R.string.search),
                        textStyle = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Thin,
                            fontStyle = FontStyle.Italic
                        )
                    ) {
                        onEvent(OrdersEvent.SearchOrders(it))
                    }
                    Spacer(modifier = Modifier.padding(top = 20.dp))
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(
                            count = state.orders.size,
                            key = { state.orders[it].tableId },
                            itemContent = {
                                Box(modifier = Modifier.animateItemPlacement(tween())) {
                                    TableAndOrderItem(model = state.orders[it],
                                        onClick = { id ->
                                            onEvent(OrdersEvent.SelectOrder(id))
                                            navEvent(OrderNavEvent.ToRegisterOrder)
                                        }, onLongClick = { id ->
                                            onEvent(OrdersEvent.SelectOrder(id))
                                            scope.launch {
                                                sheetState.collapse()
                                                dialog = OrdersDialog.Action
                                                sheetState.expand()
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
                            onEvent(OrdersEvent.ResetOrder)
                            navEvent(OrderNavEvent.ToNewOrder)
                        },
                        backgroundColor = DarkBlue,
                        elevation =
                        FloatingActionButtonDefaults.elevation(defaultElevation = 12.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
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

        PullRefreshIndicator(
            refreshing = state.isLoading,
            state = pullRefreshState,
            modifier = Modifier.align(
                Alignment.TopCenter
            )
        )
    }

}

