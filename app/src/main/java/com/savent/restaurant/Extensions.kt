package com.savent.restaurant

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat.startActivity
import com.savent.restaurant.utils.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.Normalizer
import java.text.SimpleDateFormat

fun Context.toast(message: Message) {
    when (message) {
        is Message.DynamicString ->
            Toast.makeText(this, message.value, Toast.LENGTH_LONG).show()
        is Message.StringResource ->
            Toast.makeText(this, message.resId, Toast.LENGTH_LONG).show()
    }

}

fun DateTimeObj.toLong() =
    SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse("${this.date} ${this.time}")?.time
        ?: System.currentTimeMillis()


fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition()
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        )
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFB8B5B5).copy(alpha = 0.5f),
                Color(0xD39E9999).copy(alpha = 0.5f),
                Color(0xFFB8B5B5).copy(alpha = 0.5f),
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    )
        .onGloballyPositioned {
            size = it.size
        }
}

 fun Activity.isNeededRequestAndroid12BLEPermission(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (!CheckPermissions.check(this, AppConstants.ANDROID_12_BLE_PERMISSIONS)) {
            requestPermissions(
                AppConstants.ANDROID_12_BLE_PERMISSIONS,
                AppConstants.REQUEST_12_BLE_CODE
            )
            return true
        }

    }
    return false

}

fun DateTimeObj.isToday(): Boolean{
    val today = DateFormat.format(System.currentTimeMillis(), "yyyy-MM-dd")
    val currentDate = DateFormat.format(this.toLong(), "yyyy-MM-dd")
    return currentDate == today
}

fun CharSequence.withOutAccent(): String {
    val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
    return "\\p{InCombiningDiacriticalMarks}+".toRegex().replace(temp, "")
}

