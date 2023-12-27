package com.savent.restaurant.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        textStyle = textStyle,
        decorationBox = { innerTextField ->
            Row(verticalAlignment = CenterVertically) {
                if (leadingIcon != null) {
                    Box {
                        leadingIcon()
                    }
                }
                if (placeholder != null) {
                    placeholder()
                } else {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 2.dp, top = 6.dp, bottom = 6.dp, end = 8.dp)
                    ) {
                        innerTextField()
                    }
                }
                if (trailingIcon != null) {
                    Box {
                        trailingIcon()
                    }
                }
            }
        },
        singleLine = singleLine,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions
    )
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun CustomTextFieldPreview() {
    CustomTextField(
        value = "text",
        onValueChange = { },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top)
            .clip(RoundedCornerShape(6.dp))
            .background(color = Color.LightGray)
            .padding(5.dp),
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                //painter = painterResource(id = R.drawable.round_search),
                contentDescription = null,
                modifier = Modifier.padding(start = 10.dp)
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Rounded.Clear,
                //painter = painterResource(id = R.drawable.round_clear),
                contentDescription = null,
                modifier = Modifier.padding(end = 4.dp)
            )
        },
        singleLine = true
    )

}
