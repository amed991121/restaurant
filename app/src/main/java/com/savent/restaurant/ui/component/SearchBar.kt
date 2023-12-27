package com.savent.restaurant.ui.component

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.savent.restaurant.R
import com.savent.restaurant.ui.theme.DarkBlue

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SearchBar(
    hint: String = "",
    textStyle: TextStyle = LocalTextStyle.current,
    onValueChange: (String) -> Unit,
) {

    var text by rememberSaveable { mutableStateOf(hint) }

    CustomTextField(
        value = text,
        onValueChange = {
            text = it.changeValueBehavior(hint, text)
            onValueChange(if (text != hint) text else "")
        },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top)
            .clip(RoundedCornerShape(20.dp))
            .background(color = Color.LightGray.copy(alpha = 0.25f))
            .padding(3.dp),
        textStyle = if (text == hint)
            textStyle.copy(color = DarkBlue.copy(alpha = 0.4f)) else textStyle,
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .padding(10.dp),
                tint = DarkBlue.copy(alpha = 0.4f)
            )
        },
        trailingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = null,
                modifier = Modifier
                    .size(38.dp)
                    .padding(10.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() })
                    {
                        onValueChange("")
                        text = hint
                    },
                tint = DarkBlue.copy(alpha = 0.4f)
            )

        },
        singleLine = true
    )

}

private fun String.changeValueBehavior(
    hint: String,
    text: String,
    maxLength: Long = Long.MAX_VALUE
): String {
    if (this.length > maxLength) return text
    if (this.isEmpty()) return hint
    if (text == hint) {
        var tempText = this
        hint.forEach { c ->
            tempText = tempText.replaceFirst(c.toString(), "")
        }
        return tempText
    }
    return this
}