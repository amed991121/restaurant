package com.savent.restaurant.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.savent.restaurant.R
import com.savent.restaurant.ui.theme.DarkBlue

@Composable
fun SplashScreen(animationEnd:(Boolean)->Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.anim2))
        val progress by animateLottieCompositionAsState(
            composition = composition,
            /*restartOnPlay = true,
            iterations = Int.MAX_VALUE*/
        )
        if(progress == 1.0f) animationEnd(true)
        Spacer(modifier = Modifier.height(40.dp))

        LottieAnimation(
            composition = composition,
            progress = progress,
            modifier = Modifier
                .requiredHeight(400.dp)
                .fillMaxWidth(0.95f),
        )
        Text(
            text = stringResource(id = R.string.savent),
            style = MaterialTheme.typography.h1.copy(color = DarkBlue),
        )
        Spacer(modifier = Modifier.height(60.dp))
        Image(
            painter = painterResource(id = R.drawable.savent_logo_128),
            contentDescription = null,
            modifier = Modifier
                .size(88.dp),
        )
    }
}