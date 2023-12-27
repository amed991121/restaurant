package com.savent.restaurant.ui.model.menu_category

import android.graphics.Typeface
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savent.restaurant.R


@Composable
fun MenuCategoryWithStrokeItem(model: MenuCategoryModel, selected: Boolean = false) {
    var isSelected by rememberSaveable { mutableStateOf(selected) }
    val dim = 60.dp
    val pad = 9.dp
    Column(
        modifier = Modifier
            .requiredWidth(dim)
            .wrapContentHeight()
            .clickable { isSelected = !isSelected }
    ) {
        Box(
            modifier = Modifier
                .requiredHeight(dim)
                .border(
                    border = BorderStroke(
                        width = 2.dp,
                        color = Color.LightGray.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(18.dp)
                )
                .clip(
                    if (isSelected) RoundedCornerShape(18.dp)
                    else RoundedCornerShape(0.dp)
                )
                .background(
                    color = if (isSelected) Color.LightGray.copy(alpha = 0.4f)
                    else Color.White
                )
        ) {
            Image(
                painter = painterResource(id = model.resId),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(pad)
                    .fillMaxSize(),
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = model.categoryName,
            style = TextStyle(
                fontFamily = FontFamily(typeface = Typeface.DEFAULT_BOLD),
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
            ),
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(start = 0.dp, end = 0.dp),
            maxLines = 1
        )
    }

}
