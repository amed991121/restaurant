package com.savent.restaurant.ui.model.table_and_order

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.Table
import com.savent.restaurant.ui.component.RoundedBox

@Composable
fun TableItem(table: Table, onClick: (Int) -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick(table.id) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoundedBox(
            width = 52.dp,
            height = 52.dp,
            cornerSize = 14.dp,
            Color.LightGray.copy(alpha = 0.3f),
            content = {
                Image(
                    painter = painterResource(id = R.drawable.ic_table),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(6.dp)
                        .fillMaxSize()
                )
            }
        )

        Column(
            verticalArrangement = Arrangement.Center, modifier = Modifier
                .weight(1f)
                .padding(start = 15.dp, end = 15.dp)
        ) {
            Text(
                style = MaterialTheme.typography.body1,
                text = table.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,

                )
        }

    }
}