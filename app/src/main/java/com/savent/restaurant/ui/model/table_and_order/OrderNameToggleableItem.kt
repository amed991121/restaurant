package com.savent.restaurant.ui.model.table_and_order

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.savent.restaurant.R
import com.savent.restaurant.ui.component.CircularCheckBox
import com.savent.restaurant.ui.component.RoundedBox
import com.savent.restaurant.ui.theme.DarkBlue

@Composable
fun OrderNameToggleableItem(model: OrderNameModel, onEvent: (IsOrderChecked) -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 8.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onEvent(IsOrderChecked(model.id, !model.isSelected))
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoundedBox(
            width = 40.dp,
            height = 40.dp,
            cornerSize = 15.dp,
            Color.LightGray.copy(alpha = 0.2f),
            content = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_person),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(6.dp)
                        .fillMaxSize(),
                    tint = Color.Gray
                )
            }
        )

        Text(
            style = MaterialTheme.typography.body1,
            text = model.dinerName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(start = 15.dp, end = 15.dp)
                .align(Alignment.CenterVertically)
        )

        CircularCheckBox(
            checked = model.isSelected,
            onCheckedChange = { onEvent(IsOrderChecked(model.id, it)) },
            size = 23.dp,
            colors = CheckboxDefaults.colors(
                checkedColor = DarkBlue.copy(alpha = 0.8f)
            )
        )

    }
}

class IsOrderChecked(val id: Int, val isChecked: Boolean)