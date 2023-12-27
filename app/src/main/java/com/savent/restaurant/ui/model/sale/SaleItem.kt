package com.savent.restaurant.ui.model.sale

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savent.restaurant.R
import com.savent.restaurant.ui.model.SharedReceipt
import com.savent.restaurant.ui.theme.DarkBlue
import com.savent.restaurant.ui.theme.LightGray

@Composable
fun SaleItem(sale: SaleModel, onShareReceipt: () -> Unit) {
    Column(
        modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 0.dp, bottom = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(color = Color.LightGray.copy(alpha = 0.25f))
                            .padding(5.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_menu),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp),
                            tint = Color.DarkGray
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = sale.tableName, fontSize = 26.sp)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_spoon_fork1),
                        contentDescription = null,
                        modifier = Modifier
                            .size(25.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "${sale.totalDishes} platos",
                        modifier = Modifier
                            .wrapContentSize(),
                        style = TextStyle(
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Thin,
                            fontStyle = FontStyle.Italic
                        ),
                        color = Color.DarkGray
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(sale.paymentMethod.resId),
                        contentDescription = null,
                        modifier = Modifier
                            .size(23.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = sale.paymentMethod.name,
                        modifier = Modifier
                            .wrapContentSize(),
                        style = TextStyle(
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Thin,
                            fontStyle = FontStyle.Italic
                        ),
                        color = Color.DarkGray
                    )
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(verticalArrangement = Arrangement.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_send),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(LightGray.copy(alpha = 0.8f))
                        .clickable { onShareReceipt() }
                        .padding(10.dp),
                    tint = DarkBlue.copy(alpha = 1f)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Divider(color = Color.LightGray)
        Spacer(modifier = Modifier.height(10.dp))
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
                style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Thin),
                text = "$ ${sale.checkout.subtotal}",
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

        }
        Spacer(modifier = Modifier.height(6.dp))
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

            Text(
                style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Thin),
                text = "$ ${sale.checkout.discounts}",
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Black),
                text = stringResource(R.string.total),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                style = MaterialTheme.typography.body2.copy(
                    fontWeight = FontWeight.Thin,
                    color = Color.Black
                ),
                text = "$ ${sale.checkout.total}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Black),
                text = stringResource(R.string.collected),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                style = MaterialTheme.typography.body2.copy(
                    fontWeight = FontWeight.Thin,
                    color = Color.Black
                ),
                text = "$ ${sale.checkout.collected}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Black),
                text = stringResource(R.string.change),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                style = MaterialTheme.typography.body2.copy(
                    fontWeight = FontWeight.Thin,
                    color = Color.Black
                ),
                text = "$ ${sale.checkout.change}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Text(text = sale.hour, fontSize = 15.sp, color = DarkBlue.copy(alpha = 0.7f))
        }
        Spacer(modifier = Modifier.height(5.dp))
        Divider(color = Color.Black)
    }

}
