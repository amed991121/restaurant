package com.savent.restaurant.ui.screen.orders

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savent.restaurant.R
import com.savent.restaurant.ui.theme.DarkBlue

@Composable
fun ConfirmDialog(onEvent: (ConfirmEvent) -> Unit) {
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
                    .clickable { onEvent(ConfirmEvent.Close) }
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
        Column() {
            Spacer(modifier = Modifier.height(45.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            onEvent(ConfirmEvent.Accept)
                        }
                        .background(Color.LightGray.copy(alpha = 0.35f))
                        .padding(start = 12.dp, end = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(8.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.accept),
                            modifier = Modifier
                                .wrapContentSize(),
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Thin,
                                fontStyle = FontStyle.Italic
                            ),
                            color = DarkBlue
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_check_fill),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp),
                            tint = DarkBlue
                        )

                    }
                }

                Spacer(modifier = Modifier.width(25.dp))

                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            onEvent(ConfirmEvent.Cancel)
                        }
                        .background(Color.LightGray.copy(alpha = 0.35f))
                        .padding(start = 12.dp, end = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(8.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.cancel),
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Thin,
                                fontStyle = FontStyle.Italic
                            ),

                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_cancel_fill),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp),
                            tint = DarkBlue
                        )

                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

    }
}

sealed class ConfirmEvent {
    object Close : ConfirmEvent()
    object Accept : ConfirmEvent()
    object Cancel : ConfirmEvent()
}