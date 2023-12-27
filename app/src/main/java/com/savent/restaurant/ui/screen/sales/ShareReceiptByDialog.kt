package com.savent.restaurant.ui.screen.sales

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.savent.restaurant.ui.model.SharedReceipt
import com.savent.restaurant.ui.model.share_by.ShareReceiptByModel
import com.savent.restaurant.ui.model.share_by.ShareBytItem

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ShareReceiptDialog(
    methods: List<ShareReceiptByModel>,
    onClose: () -> Unit,
    onClick: (SharedReceipt.Method) -> Unit,
    printerLongClick: ()-> Unit
) {

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
                    .clickable { onClose() }
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(
                    count = methods.size,
                    key = { idx -> methods[idx].method.ordinal },
                    itemContent = { idx ->
                        ShareBytItem(
                            shareBy = methods[idx],
                            onClick = onClick,
                            onLongClick = {
                                if (it == SharedReceipt.Method.Printer)
                                    printerLongClick()
                            })
                    })
            }
        }
    }
}
