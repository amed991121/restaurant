package com.savent.restaurant.ui.screen.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.White

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savent.restaurant.R
import com.savent.restaurant.ui.model.dish.DishModel
import com.savent.restaurant.ui.theme.DarkBlue


@Composable
fun SendDishesToKitchenPrinterDialog(
    dishes: List<DishModel>,
    onDismiss: () -> Unit,
    onSelectPrinterDevice: () -> Unit,
    onSendKitchenOrderToPrinter: (HashMap<Int, Int>) -> Unit
) {
    val priorities: HashMap<Int, Int> by remember {
        val pairs = dishes.map { Pair(it.id,1) }.toTypedArray()
        mutableStateOf(hashMapOf(pairs = pairs))
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
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
                    .clickable { onDismiss() }
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = stringResource(id = R.string.send_order_to_kitchen),
                modifier = Modifier
                    .wrapContentSize(),
                style = TextStyle(
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Thin,
                    fontStyle = FontStyle.Italic
                ),
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(30.dp))
            Row(modifier = Modifier.padding(start = 5.dp, end = 5.dp)) {
                Text(
                    modifier = Modifier.weight(1f),
                    fontSize = 20.sp,
                    //fontWeight = FontWeight.Black,
                    text = stringResource(R.string.amount),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Black
                )
                Spacer(modifier = Modifier.width(15.dp))
                Text(
                    modifier = Modifier.weight(4f),
                    fontSize = 20.sp,
                    //fontWeight = FontWeight.Black,
                    text = stringResource(R.string.product),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Black
                )
                Spacer(modifier = Modifier.width(15.dp))
                Text(
                    modifier = Modifier.weight(1f),
                    fontSize = 20.sp,
                    //fontWeight = FontWeight.Black,
                    text = stringResource(R.string.priority),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Black
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Divider(modifier = Modifier.height(1.dp).padding(start = 5.dp, end = 5.dp))
            Spacer(modifier = Modifier.height(5.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(
                    count = dishes.size,
                    key = { dishes[it].id },
                    itemContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                modifier = Modifier.weight(1f),
                                fontSize = 19.sp,
                                text = "${dishes[it].units}   x",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Spacer(modifier = Modifier.width(15.dp))
                            Text(
                                modifier = Modifier.weight(4f),
                                fontSize = 19.sp,
                                text = dishes[it].name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Spacer(modifier = Modifier.width(15.dp))
                            var priorityTextValue by remember { mutableStateOf("1") }
                            BasicTextField(
                                value = priorityTextValue,
                                onValueChange = { str ->
                                    priorityTextValue = if (str == "0") "1" else str
                                    priorities[dishes[it].id] =
                                        priorityTextValue.ifEmpty { "1" }.toInt()
                                },
                                modifier = Modifier
                                    .requiredHeight(42.dp)
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(LightGray.copy(alpha = 0.4f)),
                                textStyle = TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Black,
                                    textAlign = TextAlign.Center
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
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                )
                            )

                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(25.dp))
                    .background(LightGray.copy(alpha = 0.4f))
                    .clickable { onSelectPrinterDevice() }
                    .padding(10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_bluetooth),
                    contentDescription = null,
                    modifier = Modifier.size(35.dp),
                    tint = DarkGray.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(id = R.string.select_print_device),
                    style = TextStyle(
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Italic
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = DarkGray.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Spacer(modifier = Modifier.height(30.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(DarkBlue)
                    .clickable {
                        onSendKitchenOrderToPrinter(priorities)
                    }
                    .padding(12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(id = R.string.send_order),
                    style = TextStyle(
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Italic
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = White
                )
                Spacer(modifier = Modifier.width(15.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_send_horizontally),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = White
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}