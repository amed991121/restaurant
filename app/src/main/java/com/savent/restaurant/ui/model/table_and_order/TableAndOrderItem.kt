package com.savent.restaurant.ui.model.table_and_order

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savent.restaurant.R
import com.savent.restaurant.ui.component.RoundedBox


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TableAndOrderItem(
    model: TableAndOrderModel,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit
) {

    var isCollapse by rememberSaveable { mutableStateOf(true) }
    Column() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .combinedClickable(
                    onClick = {
                        if (model.orderNames.size == 1)
                            onClick(model.orderNames[0].id)
                        else
                            isCollapse = !isCollapse
                    },
                    onLongClick = {
                        if (model.orderNames.size == 1)
                            onLongClick(model.orderNames[0].id)
                    },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RoundedBox(
                width = 60.dp,
                height = 60.dp,
                cornerSize = 18.dp,
                Color.LightGray.copy(alpha = 0.3f),
                content = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_table),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxSize()
                    )
                }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 15.dp, end = 15.dp)
                    .align(Alignment.Top)
            ) {
                Text(
                    style = MaterialTheme.typography.body1,
                    text = model.tableName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.padding(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
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
            }
        }
        AnimatedVisibility(!isCollapse) {
            OrderNameList(items = model.orderNames, onClick = onClick, onLongClick = onLongClick)
        }
    }

}

@Composable
private fun OrderNameList(
    items: List<OrderNameModel>,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Spacer(modifier = Modifier.padding(3.dp))
        items.forEach { item ->
            OrderNameItem(model = item, onClick = onClick, onLongClick = onLongClick)
        }
    }

}
