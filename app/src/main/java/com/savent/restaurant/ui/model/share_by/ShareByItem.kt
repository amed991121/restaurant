package com.savent.restaurant.ui.model.share_by

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savent.restaurant.ui.model.SharedReceipt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShareBytItem(
    shareBy: ShareReceiptByModel,
    onClick: (SharedReceipt.Method) -> Unit,
    onLongClick: (SharedReceipt.Method)->Unit) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.LightGray.copy(alpha = 0.35f))
            .combinedClickable(
                onClick = { onClick(shareBy.method) },
                onLongClick = { onLongClick(shareBy.method) }
            )

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(bottom = 8.dp, top = 8.dp, start = 12.dp, end = 12.dp)
        ) {
            Icon(
                painter = painterResource(id = shareBy.resId),
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp),
                tint = Color.Gray.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = shareBy.name,
                modifier = Modifier
                    .wrapContentSize(),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Thin,
                    fontStyle = FontStyle.Italic
                ),
                color = Color.Gray
            )
            Spacer(modifier = Modifier.width(10.dp))
        }
    }
}