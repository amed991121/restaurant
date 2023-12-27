package com.savent.restaurant.ui.screen.food

import android.util.Log
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
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.savent.restaurant.R
import com.savent.restaurant.ui.component.SearchBar
import com.savent.restaurant.ui.model.table_and_order.TableAndOrderItemEvent
import com.savent.restaurant.ui.model.table_and_order.TableAndOrderModel
import com.savent.restaurant.ui.model.table_and_order.TableAndOrderToggleableItem
import com.savent.restaurant.ui.theme.DarkBlue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TableAndOrdersScreen(
    tablesAndOrders: List<TableAndOrderModel>,
    onEvent: (TableAndOrdersScreenEvent) -> Unit,
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.98f)
    ) {
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.LightGray.copy(alpha = 0.2f))
                    .align(
                        Alignment.TopEnd
                    )
                    .clickable { onEvent(TableAndOrdersScreenEvent.Close) }
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
            Spacer(modifier = Modifier.padding(top = 10.dp))
            Text(
                text = stringResource(id = R.string.add_to),
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(start = 5.dp)
            )
            Spacer(modifier = Modifier.padding(top = 15.dp))
            SearchBar(
                hint = stringResource(id = R.string.search),
                textStyle = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Thin,
                    fontStyle = FontStyle.Italic
                ),
                onValueChange = { onEvent(TableAndOrdersScreenEvent.Search(it)) }
            )
            Spacer(modifier = Modifier.padding(top = 20.dp))
            Column {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(
                        count = tablesAndOrders.size,
                        key = { tablesAndOrders[it].tableId },
                        itemContent = {
                            Box(modifier = Modifier.animateItemPlacement(tween())) {
                                TableAndOrderToggleableItem(
                                    model = tablesAndOrders[it],
                                    onEvent = { event ->
                                        when (event) {
                                            is TableAndOrderItemEvent.IsOrderChecked -> {
                                                onEvent(
                                                    TableAndOrdersScreenEvent.DishToOrder(
                                                        event.orderId,
                                                        event.isChecked
                                                    )
                                                )
                                            }
                                            is TableAndOrderItemEvent.IsTableChecked -> onEvent(
                                                TableAndOrdersScreenEvent.DishToTable(
                                                    event.tableId,
                                                    event.isChecked
                                                )
                                            )
                                        }
                                    },
                                )
                            }

                        })
                }

                Button(
                    onClick = {
                        onEvent(TableAndOrdersScreenEvent.SaveDishOrder)
                        onEvent(TableAndOrdersScreenEvent.Close)
                    },
                    modifier = Modifier
                        .wrapContentHeight(Alignment.Bottom)
                        .fillMaxWidth()
                        .padding(start = 30.dp, end = 30.dp, top = 10.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = DarkBlue)
                ) {
                    Text(
                        text = stringResource(id = R.string.accept),
                        fontSize = 19.sp,
                        color = Color.White,
                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                    )
                }
            }
        }
    }

}


sealed class TableAndOrdersScreenEvent {
    class Search(val query: String) : TableAndOrdersScreenEvent()
    class DishToOrder(val orderId: Int, val isAdded: Boolean) : TableAndOrdersScreenEvent()
    class DishToTable(val tableId: Int, val isAdded: Boolean) : TableAndOrdersScreenEvent()
    object SaveDishOrder : TableAndOrdersScreenEvent()
    object Close : TableAndOrdersScreenEvent()
}
