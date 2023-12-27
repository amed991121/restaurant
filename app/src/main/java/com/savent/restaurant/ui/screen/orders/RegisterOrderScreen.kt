package com.savent.restaurant.ui.screen.orders

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.savent.restaurant.R
import com.savent.restaurant.ui.model.dish.DishItemEvent
import com.savent.restaurant.ui.model.dish.DishUnitsItem
import com.savent.restaurant.ui.model.payment_method.PaymentMethodItem
import com.savent.restaurant.ui.screen.ListScreenEvent
import com.savent.restaurant.ui.theme.DarkBlue
import com.savent.restaurant.ui.theme.LightGray
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun RegisterOrderScreen(
    state: OrdersState,
    onEvent: (OrdersEvent) -> Unit,
    navEvent: (OrderNavEvent) -> Unit,
) {

    var dialog by remember { mutableStateOf(OrdersDialog.AddOrderTag) }

    val loadingComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.loading)
    )
    val loadingProgress by animateLottieCompositionAsState(
        composition = loadingComposition,
        restartOnPlay = true,
        iterations = Int.MAX_VALUE
    )

    val sheetState = rememberBottomSheetState(
        initialValue = BottomSheetValue.Collapsed
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )
    val scope = rememberCoroutineScope()

    val lazyListState = rememberLazyListState()

    if (state.order.pendingKitchenDishes.isEmpty() && dialog == OrdersDialog.SendDishesToPrinter)
        LaunchedEffect(Unit) {
            scope.launch {
                sheetState.collapse()
            }
        }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.93f)
    ) {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = {
                when (dialog) {
                    OrdersDialog.AddOrderTag -> {
                        AddTagDialog(tag = state.order.tag, onEvent = { event ->
                            when (event) {
                                AddTagEvent.Close -> scope.launch { sheetState.collapse() }
                                is AddTagEvent.Save -> {
                                    onEvent(OrdersEvent.AddOrderTag(event.tag, true))
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
                                                isPersistent = true
                                            )
                                        )
                                        DishesListScreenEvent.Close -> scope.launch { sheetState.collapse() }
                                        is DishesListScreenEvent.RemoveUnit -> onEvent(
                                            OrdersEvent.RemoveDishUnit(
                                                dishId = event.id,
                                                isPersistent = true
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
                    OrdersDialog.Confirm -> {
                        ConfirmDialog(onEvent = { event ->
                            when (event) {
                                ConfirmEvent.Accept -> scope.launch {
                                    sheetState.collapse()
                                    onEvent(OrdersEvent.RegisterOrder)
                                }
                                ConfirmEvent.Cancel -> scope.launch { sheetState.collapse() }
                                ConfirmEvent.Close -> scope.launch { sheetState.collapse() }
                            }
                        })
                    }
                    OrdersDialog.PaymentMethod -> {
                        PaymentMethodDialog(
                            methods = state.paymentMethods,
                            onClose = {
                                scope.launch { sheetState.collapse() }
                            },
                            onClick = {
                                scope.launch {
                                    sheetState.collapse()
                                    onEvent(OrdersEvent.SelectPaymentMethod(it))
                                }

                            })
                    }
                    OrdersDialog.SendDishesToPrinter -> {
                        SendDishesToKitchenPrinterDialog(
                            dishes = state.order.pendingKitchenDishes,
                            onDismiss = { scope.launch { sheetState.collapse() } },
                            onSelectPrinterDevice = { onEvent(OrdersEvent.ScanPrinterDevices) },
                            onSendKitchenOrderToPrinter = {
                                onEvent(
                                    OrdersEvent.SendKitchenOrderToPrinter(
                                        it
                                    )
                                )
                            }
                        )
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
                    horizontalAlignment = Alignment.CenterHorizontally,
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
                            text = stringResource(id = R.string.open_order),
                            style = MaterialTheme.typography.h3.copy(fontFamily = FontFamily.Cursive),
                            modifier = Modifier
                                .padding(start = 5.dp)
                                .align(Alignment.Top)
                                .weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row() {
                            AnimatedVisibility(
                                visible = state.order.pendingKitchenDishes.isNotEmpty(),
                                enter = slideInHorizontally(initialOffsetX = { it }),
                                exit = slideOutHorizontally(targetOffsetX = { it })
                            ) {
                                Spacer(modifier = Modifier.width(20.dp))
                                BadgedBox(
                                    badge = {
                                        Box(
                                            modifier = Modifier
                                                .size(30.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    DarkBlue
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = state.order.pendingKitchenDishes.size.toString(),
                                                fontSize = 15.sp,
                                                color = Color.White
                                            )
                                        }
                                    },
                                    modifier = Modifier.padding(end = 10.dp)
                                ) {
                                    Icon(
                                        painterResource(id = R.drawable.ic_printer),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(CircleShape)
                                            .background(LightGray.copy(alpha = 1f))
                                            .clickable {
                                                dialog = OrdersDialog.SendDishesToPrinter
                                                scope.launch { sheetState.expand() }
                                            }
                                            .padding(15.dp),
                                        tint = Color.Black
                                    )
                                }
                            }

                        }

                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.LightGray.copy(alpha = 0.35f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_note),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp),
                                tint = Color.Gray.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = state.order.tableName,
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
                            .align(Alignment.CenterHorizontally)
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
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
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
                                                            isPersistent = true
                                                        )
                                                    )
                                                is DishItemEvent.Remove ->
                                                    onEvent(
                                                        OrdersEvent.RemoveDishUnit(
                                                            dishId = event.id,
                                                            isPersistent = true
                                                        )
                                                    )
                                            }

                                        })
                                }
                            })
                    }

                    var isCollapse by remember { mutableStateOf(true) }
                    Icon(
                        imageVector = if (isCollapse)
                            Icons.Rounded.KeyboardArrowUp
                        else Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier
                            .size(35.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { isCollapse = !isCollapse },
                        tint = Color.Gray
                    )
                    Column(
                        modifier = Modifier
                            .padding(bottom = 20.dp),
                    ) {
                        var discounts by remember { mutableStateOf(state.order.checkout.discounts) }
                        var collected by remember { mutableStateOf(state.order.checkout.collected) }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Thin),
                                text = stringResource(R.string.subtotal),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )

                            Text(
                                modifier = Modifier.padding(end = 8.dp),
                                style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Thin),
                                text = "$ ${state.order.checkout.subtotal}",
                                color = Color.DarkGray.copy(alpha = 0.6f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,

                                )

                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Thin),
                                text = stringResource(R.string.discounts),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )

                            val shape = RoundedCornerShape(10.dp)
                            BasicTextField(
                                value = discounts,
                                onValueChange = {
                                    discounts = it.ifEmpty { "0" }
                                    onEvent(
                                        OrdersEvent.UpdateCollectedValues(
                                            discounts = discounts.toFloat(),
                                            collected = collected.toFloat()
                                        )
                                    )
                                },
                                modifier = Modifier
                                    .requiredHeight(40.dp)
                                    .wrapContentWidth()
                                    .border(
                                        border = BorderStroke(
                                            2.dp,
                                            Color.Gray.copy(alpha = 0.0f)
                                        ),
                                        shape = shape
                                    )
                                    .clip(shape)
                                    .background(LightGray.copy(alpha = 1f)),
                                textStyle = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Thin)
                                    .copy(
                                        color = Color.DarkGray.copy(alpha = 1f),
                                        textAlign = TextAlign.End
                                    ),
                                singleLine = true,
                                decorationBox = { innerTextField ->
                                    Box(
                                        modifier = Modifier
                                            .width(100.dp)
                                            .padding(
                                                start = 8.dp,
                                                top = 6.dp,
                                                bottom = 6.dp,
                                                end = 8.dp
                                            )
                                    ) {
                                        innerTextField()
                                    }
                                },
                                /* visualTransformation = CurrencyAmountInputVisualTransformation(
                                     fixedCursorAtTheEnd = true
                                 ),*/
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal
                                )
                            )

                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(
                            modifier = Modifier
                                .height(1.dp)
                                .background(Color.LightGray.copy(alpha = 0.8f))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Black),
                                text = stringResource(R.string.total),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                modifier = Modifier.padding(end = 8.dp),
                                style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Black),
                                text = "$ ${state.order.checkout.total}",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )

                        }
                        AnimatedVisibility(visible = !isCollapse) {
                            Column {
                                Spacer(modifier = Modifier.height(5.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.subtitle1.copy(
                                            fontWeight = FontWeight.Black
                                        ),
                                        text = stringResource(R.string.collected),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )

                                    val shape = RoundedCornerShape(10.dp)
                                    BasicTextField(
                                        value = collected,
                                        onValueChange = {
                                            collected = it.ifEmpty { "0" }
                                            onEvent(
                                                OrdersEvent.UpdateCollectedValues(
                                                    discounts = discounts.toFloat(),
                                                    collected = collected.toFloat()
                                                )
                                            )
                                        },
                                        modifier = Modifier
                                            .requiredHeight(50.dp)
                                            .wrapContentWidth()
                                            .border(
                                                border = BorderStroke(
                                                    2.dp,
                                                    Color.Gray.copy(alpha = 0.0f)
                                                ),
                                                shape = shape
                                            )
                                            .clip(shape)
                                            .background(LightGray.copy(alpha = 1f)),
                                        textStyle = MaterialTheme.typography.subtitle1.copy(
                                            fontWeight = FontWeight.Thin
                                        )
                                            .copy(
                                                color = Color.DarkGray.copy(alpha = 1f),
                                                textAlign = TextAlign.End
                                            ),
                                        decorationBox = { innerTextField ->
                                            Box(
                                                modifier = Modifier
                                                    .width(125.dp)
                                                    .padding(
                                                        start = 8.dp,
                                                        top = 6.dp,
                                                        bottom = 6.dp,
                                                        end = 8.dp
                                                    )
                                            ) {
                                                innerTextField()
                                            }
                                        },
                                        singleLine = true,
                                        /*visualTransformation = CurrencyAmountInputVisualTransformation(
                                            fixedCursorAtTheEnd = true
                                        ),*/
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Decimal
                                        )
                                    )

                                }
                                Spacer(modifier = Modifier.height(5.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.subtitle1.copy(
                                            fontWeight = FontWeight.Black
                                        ),
                                        text = stringResource(R.string.change),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Text(
                                        modifier = Modifier.padding(end = 8.dp),
                                        style = MaterialTheme.typography.subtitle1.copy(
                                            fontWeight = FontWeight.Thin,
                                        ),
                                        text = "$ ${state.order.checkout.change}",
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )

                                }

                                Spacer(modifier = Modifier.height(15.dp))

                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    PaymentMethodItem(
                                        method = state.order.paymentMethod,
                                        onClick = {
                                            dialog = OrdersDialog.PaymentMethod
                                            scope.launch { sheetState.expand() }
                                        })
                                }


                            }

                        }

                        Spacer(modifier = Modifier.height(15.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .wrapContentSize(Alignment.Center)
                                    .fillMaxWidth(1f)
                                    .clip(RoundedCornerShape(28.dp))
                                    .background(DarkBlue.copy(alpha = 1f))
                                    .clickable {
                                        dialog = OrdersDialog.Confirm
                                        scope.launch { sheetState.expand() }
                                    }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(9.dp)
                                ) {

                                    Text(
                                        text = stringResource(id = R.string.register),
                                        modifier = Modifier
                                            .wrapContentHeight(),
                                        style = TextStyle(
                                            fontSize = 23.sp,
                                            fontWeight = FontWeight.Thin,
                                            fontStyle = FontStyle.Italic
                                        ),
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_check_fill),
                                        contentDescription = null,
                                        modifier = Modifier.size(21.dp),
                                        tint = Color.White
                                    )

                                }
                            }
                        }
                    }

                }

                AnimatedVisibility(
                    visible = state.isLoading,
                    enter = scaleIn(),
                    exit = scaleOut()
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        LottieAnimation(
                            composition = loadingComposition,
                            progress = loadingProgress,
                            modifier = Modifier
                                .size(500.dp)
                        )
                    }
                }

            }
        }
    }


}


