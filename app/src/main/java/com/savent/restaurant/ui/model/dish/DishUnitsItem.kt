package com.savent.restaurant.ui.model.dish

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.savent.restaurant.R
import com.savent.restaurant.shimmerEffect
import com.savent.restaurant.ui.component.RoundedBox
import com.savent.restaurant.ui.theme.DarkBlue
import com.savent.restaurant.ui.theme.LightGray

@OptIn(ExperimentalCoilApi::class)
@Composable
fun DishUnitsItem(dish: DishModel, onEvent: (DishItemEvent) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val dim = 60.dp
        val painter = rememberImagePainter(
            data = dish.url,
            builder = {
                //placeholder(R.drawable.ic_dish_large)
                error(R.drawable.ic_dish)
                crossfade(1000)
            }
        )
        val painterState = painter.state
        val modifier = Modifier
            .size(dim)
            .clip(RoundedCornerShape(15.dp))
            .then(
                if (painterState is ImagePainter.State.Success)
                    Modifier.border(
                        border = BorderStroke(
                            1.5.dp,
                            Color.Black
                        ),
                        shape = RoundedCornerShape(15.dp)
                    ) else Modifier
            )
            .background(LightGray.copy(alpha = 0.7f))
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = if (dish.url == null)
                    painterResource(id = R.drawable.ic_dish)
                else painter,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (painterState is ImagePainter.State.Loading)
                            Modifier.shimmerEffect() else Modifier
                    )
                    .then(
                        if (dish.url == null || painterState is ImagePainter.State.Error)
                            Modifier.padding(6.dp) else Modifier
                    ),
                contentScale = if (painterState is ImagePainter.State.Success)
                    ContentScale.Crop else ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.width(10.dp))
        Column(
            modifier = Modifier
                .weight(1f)
            //.align(Alignment.Top)
        ) {
            Text(
                style = MaterialTheme.typography.body2,
                text = dish.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.padding(0.dp))
            Text(
                text = "$${dish.price}",
                fontSize = 17.sp,
                color = Color.Gray,
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Row(verticalAlignment = CenterVertically) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.2f))
                    .clickable { onEvent(DishItemEvent.Remove(dish.id)) }

            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_remove),
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .padding(1.dp),
                    tint = DarkBlue.copy(alpha = 0.4f),
                )
            }
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                text = "${dish.units}",
                fontSize = 17.sp,
                color = Color.Gray,
            )
            Spacer(modifier = Modifier.width(7.dp))
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.2f))
                    .clickable { onEvent(DishItemEvent.Add(dish.id)) }

            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .padding(1.dp),
                    tint = DarkBlue.copy(alpha = 0.4f),
                )
            }
        }
    }

}

sealed class DishItemEvent {
    class Add(val id: Int): DishItemEvent()
    class Remove(val id: Int): DishItemEvent()
}

