package com.savent.restaurant.ui.screen.orders

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savent.restaurant.R
import com.savent.restaurant.ui.component.CustomTextField
import com.savent.restaurant.ui.theme.DarkBlue
import com.savent.restaurant.ui.theme.LightGray
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddTagDialog(tag: String, onEvent: (AddTagEvent) -> Unit) {
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
                    .clickable { onEvent(AddTagEvent.Close) }
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
            Text(
                text = stringResource(id = R.string.add_tag),
                modifier = Modifier
                    .wrapContentSize(),
                style = TextStyle(
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Thin,
                    fontStyle = FontStyle.Italic
                ),
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(15.dp))

            var strTag by remember { mutableStateOf(tag) }

            CustomTextField(
                value = strTag,
                onValueChange = {
                    strTag = it
                },
                modifier = Modifier
                    .requiredHeight(50.dp)
                    .fillMaxWidth(0.8f)
                    .clip(RoundedCornerShape(25.dp))
                    .background(LightGray.copy(alpha = 1f)),
                textStyle = LocalTextStyle.current,
                leadingIcon = {
                    Row() {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_tag),
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .padding(top = 8.dp, bottom = 8.dp),
                            tint = DarkBlue.copy(alpha = 0.5f)
                        )
                    }

                },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = strTag.isNotEmpty(),
                        enter = scaleIn(),
                        exit = scaleOut()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = null,
                            modifier = Modifier
                                .size(45.dp)
                                .padding(10.dp, end = 15.dp)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }) {
                                    strTag = ""
                                },
                            tint = DarkBlue.copy(alpha = 0.5f)
                        )
                    }
                },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(15.dp))
            Box(
                modifier = Modifier
                    .requiredHeight(50.dp)
                    .fillMaxWidth(0.8f)
                    .clip(RoundedCornerShape(25.dp))
                    .background(DarkBlue)
                    .clickable {
                        onEvent(AddTagEvent.ShowDiners)
                    }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 5.dp, bottom = 5.dp)
                ) {

                    Text(
                        text = stringResource(id = R.string.diners),
                        style = MaterialTheme.typography.body2.copy(
                            color = Color.White,
                            fontSize = 22.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier//.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_user),
                        contentDescription = null,
                        modifier = Modifier
                            //.weight(0.5f)
                            .size(25.dp),
                        tint = Color.White
                    )
                }

            }
            Spacer(modifier = Modifier.height(15.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .clip(RoundedCornerShape(25.dp))
                    .background(Color.LightGray.copy(alpha = 0.35f))
                    .clickable {
                        onEvent(AddTagEvent.Save(strTag.trim()))
                        onEvent(AddTagEvent.Close)
                    }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {

                    Text(
                        text = stringResource(id = R.string.accept),
                        modifier = Modifier
                            .wrapContentHeight(),
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Thin,
                            fontStyle = FontStyle.Italic
                        ),
                        color = DarkBlue.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check_outline),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp),
                        tint = DarkBlue.copy(alpha = 0.5f)
                    )

                }
            }
            Spacer(modifier = Modifier.height(25.dp))
        }
    }
}

sealed class AddTagEvent() {
    class Save(val tag: String) : AddTagEvent()
    object ShowDiners : AddTagEvent()
    object Close : AddTagEvent()
}