package com.savent.restaurant.ui.screen.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
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
import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.Diner
import com.savent.restaurant.ui.component.SearchBar
import com.savent.restaurant.ui.model.DinerItem
import com.savent.restaurant.ui.screen.ListScreenEvent


@Composable
fun DinersListScreen(diners: List<Diner>, onEvent: (ListScreenEvent) -> Unit){
    Box(modifier = Modifier
        .fillMaxWidth()
        //.heightIn(550.dp, 750.dp)
    ) {
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 8.dp)) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.LightGray.copy(alpha = 0.2f))
                    .align(
                        Alignment.TopEnd
                    )
                    .clickable { onEvent(ListScreenEvent.Close) }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .padding(2.dp)
                        .align(Alignment.Center)
                        ,
                    tint = Color.Gray.copy(alpha = 0.5f)
                )
            }
        }
        Column(modifier = Modifier
            .padding(15.dp)
            .wrapContentHeight()) {
            Text(
                text = stringResource(id = R.string.diners),
                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(start = 5.dp)
            )
            Spacer(modifier = Modifier.height(15.dp))
            SearchBar(
                hint = stringResource(id = R.string.search),
                onValueChange = { onEvent(ListScreenEvent.Search(it)) },
                textStyle = TextStyle(
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Thin,
                    fontStyle = FontStyle.Italic
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(
                    count = diners.size,
                    key = { idx -> diners[idx].id },
                    itemContent = { idx ->
                        DinerItem(diner = diners[idx], onClick = {
                            onEvent(ListScreenEvent.Select(it))
                            onEvent(ListScreenEvent.Close)
                        })
                    })
            }
        }
    }
}