package com.savent.restaurant.ui.component

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RoundedBox(
    width: Dp? = null,
    height: Dp? = null,
    cornerSize: Dp = 10.dp,
    color: Color = Color.LightGray,
    content: @Composable (() -> Unit)? = null
) {
    val modifier = Modifier
        .run { if (width != null) requiredWidth(width) else fillMaxWidth() }
        .run { if (height != null) requiredHeight(height) else fillMaxHeight() }
        .clip(RoundedCornerShape(cornerSize))
        .background(color = color)

    Box(modifier = modifier) {
        content?.invoke()
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun RoundedColorBoxPreview() {
    RoundedBox(color = Color.Red, height = 170.dp, width = 200.dp)
}