package com.savent.restaurant.ui.screen.login

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.savent.restaurant.R
import com.savent.restaurant.ui.component.CustomTextField
import com.savent.restaurant.ui.screen.ListScreenEvent
import com.savent.restaurant.ui.theme.DarkBlue
import com.savent.restaurant.ui.theme.LightGray
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun LoginScreen(state: LoginState, onEvent: (LoginEvent) -> Unit) {
    var animationVisibility by rememberSaveable {
        mutableStateOf(false)
    }
    val loadingComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.loading)
    )
    val loadingProgress by animateLottieCompositionAsState(
        composition = loadingComposition,
        restartOnPlay = true,
        iterations = Int.MAX_VALUE
    )
    val foodComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.food1)
    )
    val foodProgress by animateLottieCompositionAsState(
        composition = foodComposition,
        restartOnPlay = true,
        iterations = Int.MAX_VALUE
    )
    val sheetState = rememberBottomSheetState(
        initialValue = BottomSheetValue.Collapsed,
        //animationSpec = TweenSpec
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )
    val scope = rememberCoroutineScope()
    var dialog by rememberSaveable { mutableStateOf(Dialog.Companies) }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetBackgroundColor = Color.White,
        drawerBackgroundColor = Color.White,
        sheetContent = {
            if (dialog == Dialog.Companies) {
                CompanyListScreen(
                    companies = state.companies, onEvent = { event ->
                        when (event) {
                            ListScreenEvent.Close ->
                                scope.launch {
                                    sheetState.collapse()
                                }
                            is ListScreenEvent.Search -> {
                                onEvent(LoginEvent.SearchCompany(event.query))
                            }
                            is ListScreenEvent.Select -> {
                                onEvent(LoginEvent.SelectCompany(event.id))
                            }
                        }
                    }
                )
                return@BottomSheetScaffold
            }
            RestaurantsListScreen(
                restaurants = state.restaurants, onEvent = { event ->
                    when (event) {
                        ListScreenEvent.Close ->
                            scope.launch {
                                sheetState.collapse()
                            }
                        is ListScreenEvent.Search -> {
                            onEvent(LoginEvent.SearchRestaurant(event.query))
                        }
                        is ListScreenEvent.Select -> {
                            onEvent(LoginEvent.SelectRestaurant(event.id))
                        }
                    }
                }
            )
        },
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetPeekHeight = 0.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Column(modifier = Modifier
                    .requiredHeight(420.dp)
                    .fillMaxWidth(0.8f))
                {
                    AnimatedVisibility(visible = animationVisibility, enter = scaleIn()) {
                        LottieAnimation(
                            composition = foodComposition,
                            progress = foodProgress,
                            modifier = Modifier
                                .fillMaxSize()

                        )
                    }

                }

                animationVisibility = true

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.wrapContentSize()
                ) {
                    val shape = RoundedCornerShape(25.dp)
                    val fieldModifier = Modifier
                        .requiredHeight(50.dp)
                        .fillMaxWidth(0.8f)
                        .border(
                            border = BorderStroke(2.dp, Color.Gray.copy(alpha = 0.0f)),
                            shape = shape
                        )
                        .clip(shape)
                        .background(LightGray.copy(alpha = 1f))

                    val rfcHint = stringResource(id = R.string.rfc_hint)
                    var rfc by rememberSaveable { mutableStateOf(rfcHint) }
                    CustomTextField(
                        value = rfc,
                        onValueChange = {
                            rfc = it.changeValueBehavior(hint = rfcHint, text = rfc, maxLength = 13)
                        },
                        modifier = fieldModifier,
                        textStyle = if (rfc == rfcHint) LocalTextStyle.current.copy(
                            color = DarkBlue.copy(alpha = 0.4f)
                        ) else LocalTextStyle.current,
                        leadingIcon = {
                            Row() {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_user),
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
                                visible = rfc != rfcHint,
                                enter = scaleIn(),
                                exit = scaleOut()
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_close),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(10.dp, end = 15.dp)
                                        .clickable(
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() }) {
                                            rfc = rfcHint
                                        },
                                    tint = DarkBlue.copy(alpha = 0.5f)
                                )
                            }
                        },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    val pinHint = stringResource(id = R.string.pin_hint)
                    var pin by rememberSaveable { mutableStateOf(pinHint) }
                    CustomTextField(
                        value = pin,
                        onValueChange = {
                            pin = it.changeValueBehavior(hint = pinHint, text = pin)
                        },
                        modifier = fieldModifier,
                        textStyle = if (pin == pinHint) LocalTextStyle.current.copy(
                            color = DarkBlue.copy(alpha = 0.4f)
                        ) else LocalTextStyle.current,
                        leadingIcon = {
                            Row() {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_lock),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .height(42.dp)
                                        .width(40.dp)
                                        .padding(top = 5.dp, bottom = 5.dp),
                                    tint = DarkBlue.copy(alpha = 0.5f)
                                )
                            }

                        },
                        trailingIcon = {
                            AnimatedVisibility(
                                visible = pin != pinHint,
                                enter = scaleIn(),
                                exit = scaleOut()
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_close),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(10.dp, end = 15.dp)
                                        .clickable(
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() }) {
                                            pin = pinHint
                                        },
                                    tint = DarkBlue.copy(alpha = 0.5f)
                                )
                            }
                        },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(15.dp))
                    val companyHint = stringResource(id = R.string.company)
                    val company =
                        state.selectedCompany.ifEmpty { companyHint }
                    Box(modifier = fieldModifier.clickable {
                        dialog = Dialog.Companies
                        scope.launch {
                            if (sheetState.isCollapsed) {
                                sheetState.expand()
                            }
                        }
                        onEvent(LoginEvent.ReloadCompanies)
                    }) {
                        Row(
                            verticalAlignment = CenterVertically,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.ic_company),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(39.dp)
                                    .padding(
                                        start = 10.dp,
                                        bottom = 5.dp,
                                        top = 5.dp,
                                        end = 4.dp
                                    ),
                                tint = DarkBlue.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = company,
                                style = if (company == companyHint) LocalTextStyle.current.copy(
                                    color = DarkBlue.copy(alpha = 0.4f)
                                ) else LocalTextStyle.current
                            )
                        }

                    }

                    Spacer(modifier = Modifier.height(15.dp))
                    val restaurantHint = stringResource(id = R.string.restaurant)
                    val restaurant =
                        state.selectedRestaurant.ifEmpty { restaurantHint }

                    Box(modifier = fieldModifier.clickable {
                        dialog = Dialog.Restaurants
                        scope.launch {
                            if (sheetState.isCollapsed) {
                                sheetState.expand()
                            }
                        }
                        onEvent(LoginEvent.ReloadRestaurants)
                    }) {
                        Row(
                            verticalAlignment = CenterVertically,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.ic_store),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(39.dp)
                                    .padding(
                                        start = 8.dp,
                                        bottom = 4.dp,
                                        top = 4.dp,
                                        end = 3.dp
                                    ),
                                tint = DarkBlue.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = restaurant,
                                style = if (restaurant == restaurantHint) LocalTextStyle.current.copy(
                                    color = DarkBlue.copy(alpha = 0.4f)
                                ) else LocalTextStyle.current
                            )
                        }

                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Box(
                        modifier = Modifier
                            .requiredHeight(50.dp)
                            .fillMaxWidth(0.7f)
                            .clip(shape)
                            .background(DarkBlue)
                            .clickable {
                                if (state.isLoading) return@clickable
                                onEvent(
                                    LoginEvent.Login(
                                        Credentials(
                                            rfc.let { if (it == rfcHint) "" else it.trim() },
                                            pin.let { if (it == pinHint) "" else it.trim() }
                                        )
                                    )
                                )
                            }
                    ) {
                        Row(
                            verticalAlignment = CenterVertically,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Spacer(modifier = Modifier.width(10.dp))
                            Image(
                                painter = painterResource(id = R.drawable.savent_logo_128),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(
                                        start = 3.dp,
                                        bottom = 4.dp,
                                        top = 4.dp,
                                        end = 3.dp
                                    )
                                    .background(
                                        Color.Transparent
                                    ),
                            )
                            Text(
                                text = stringResource(id = R.string.login),
                                style = MaterialTheme.typography.body2.copy(
                                    color = Color.White
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Rounded.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier
                                    .width(45.dp)
                                    .height(40.dp)
                                    .padding(
                                        start = 3.dp,
                                        bottom = 4.dp,
                                        top = 4.dp,
                                        end = 3.dp
                                    ),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                        }

                    }

                }
                Spacer(modifier = Modifier.height(70.dp))
            }
            AnimatedVisibility(visible = state.isLoading, enter = scaleIn(), exit = scaleOut()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Center) {
                    LottieAnimation(
                        composition = loadingComposition,
                        progress = loadingProgress,
                        modifier = Modifier
                            .size(500.dp)
                    )
                }
            }

        }
    }

}

private enum class Dialog {
    Companies, Restaurants
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