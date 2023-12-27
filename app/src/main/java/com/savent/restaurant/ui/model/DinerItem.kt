package com.savent.restaurant.ui.model

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.Diner
import com.savent.restaurant.ui.component.RoundedBox

@Composable
fun DinerItem(diner: Diner, onClick: (Int) -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick(diner.id) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoundedBox(
            width = 45.dp,
            height = 45.dp,
            cornerSize = 10.dp,
            Color.LightGray.copy(alpha = 0.3f),
            content = {
                Image(
                    painter = painterResource(id = R.drawable.ic_user),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxSize()
                )
            }
        )

        Text(
            style = MaterialTheme.typography.body1,
            text = diner.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(start = 15.dp, end = 15.dp)
        )

    }
}