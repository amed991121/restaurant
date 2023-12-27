package com.savent.restaurant.ui.model.dish

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.ImagePainter.State.Empty.painter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.savent.restaurant.R
import com.savent.restaurant.shimmerEffect
import com.savent.restaurant.ui.component.RoundedBox
import com.savent.restaurant.ui.theme.DarkBlue
import com.savent.restaurant.ui.theme.LightGray

@OptIn(ExperimentalCoilApi::class)
@Composable
fun DishItem(
    dish: DishModel,
    onClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() })
            {
                onClick(dish.id)
            }
    ) {
        val dim = 170.dp
        val painter = rememberImagePainter(
            data = dish.url,
            builder = {
                //placeholder(R.drawable.ic_dish_large)
                error(R.drawable.ic_dish_large)
                crossfade(1000)
            }
        )
        val painterState = painter.state
        val modifier = Modifier
            .size(dim)
            .clip(RoundedCornerShape(20.dp))
            .then(
                if (painterState is ImagePainter.State.Success)
                    Modifier.border(
                        border = BorderStroke(
                            1.5.dp,
                            Color.Black
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) else Modifier
            )
            .background(LightGray.copy(alpha = 0.7f))
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = if (dish.url == null)
                    painterResource(id = R.drawable.ic_dish_large)
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
                            Modifier.padding(10.dp) else Modifier
                    ),
                contentScale = if (painterState is ImagePainter.State.Success)
                    ContentScale.Crop else ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(5.dp))
        Column(
            modifier = Modifier
                .padding(start = 5.dp)
        ) {
            Text(
                style = MaterialTheme.typography.body1,
                text = dish.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.padding(1.dp))
            Text(
                text = "$${dish.price}",
                fontSize = 15.sp,
                color = Color.Gray,
            )
        }
    }

}
