package com.savent.restaurant.ui.model.table_and_order

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savent.restaurant.R
import com.savent.restaurant.ui.component.CircularCheckBox
import com.savent.restaurant.ui.component.RoundedBox
import com.savent.restaurant.ui.theme.DarkBlue


@Composable
fun TableAndOrderToggleableItem(
    model: TableAndOrderModel,
    onEvent: (TableAndOrderItemEvent) -> Unit,
) {
    var isCollapse by remember { mutableStateOf(true) }

    Column() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    if (model.orderNames.size < 2){
                        onEvent(
                            TableAndOrderItemEvent.IsTableChecked(
                                model.tableId, !model.isSelected
                            )
                        )
                    }
                    else
                        isCollapse = !isCollapse
                },
            verticalAlignment = CenterVertically
        ) {
            RoundedBox(
                width = 65.dp,
                height = 65.dp,
                cornerSize = 18.dp,
                Color.LightGray.copy(alpha = 0.3f),
                content = {
                    Image(
                        painter = painterResource(id = R.drawable.table_raw1),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(6.dp)
                            .fillMaxSize()
                    )
                }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 15.dp, end = 15.dp)
                    .align(CenterVertically)
            ) {
                Text(
                    style = MaterialTheme.typography.body1,
                    text = model.tableName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.padding(2.dp))
                Row(
                    verticalAlignment = CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_note),
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp),
                        tint = Color.Gray
                    )
                    Text(
                        text = "${model.totalOrders}",
                        fontSize = 15.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }
            }
            if (model.totalOrders > 1) {
                RoundedBox(
                    width = 30.dp,
                    height = 30.dp,
                    cornerSize = 10.dp,
                    Color.LightGray.copy(alpha = 0.2f),
                    content = {
                        Icon(
                            imageVector = if (isCollapse)
                                Icons.Rounded.KeyboardArrowDown
                            else Icons.Rounded.KeyboardArrowUp,
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                                .clickable { isCollapse = !isCollapse },
                            tint = Color.Gray
                        )
                    }
                )
            } else
                CircularCheckBox(
                    checked = model.isSelected,
                    onCheckedChange = {
                        onEvent(TableAndOrderItemEvent.IsTableChecked(model.tableId, it))
                    },
                    size = 23.dp,
                    colors = CheckboxDefaults.colors(
                        checkedColor = DarkBlue.copy(alpha = 0.8f)
                    )
                )
        }
        AnimatedVisibility(!isCollapse) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Spacer(modifier = Modifier.padding(3.dp))
                model.orderNames.forEach { item ->
                    OrderNameToggleableItem(model = item, onEvent = { event ->
                        onEvent(
                            TableAndOrderItemEvent.IsOrderChecked(
                                event.id,
                                event.isChecked
                            )
                        )
                    })
                }
            }
        }
    }

}


sealed class TableAndOrderItemEvent {
    class IsTableChecked(val tableId: Int, val isChecked: Boolean) : TableAndOrderItemEvent()
    class IsOrderChecked(val orderId: Int, val isChecked: Boolean) : TableAndOrderItemEvent()
}