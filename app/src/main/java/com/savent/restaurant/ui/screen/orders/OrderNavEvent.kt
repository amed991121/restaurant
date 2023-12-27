package com.savent.restaurant.ui.screen.orders

import android.app.Activity
import android.content.Intent
import androidx.navigation.NavController
import com.savent.restaurant.ui.activity.MainActivity
import com.savent.restaurant.ui.activity.RegisterOrderActivity
import com.savent.restaurant.ui.navigation.Screen


fun Activity.orderNavEvent(navController: NavController, navEvent: OrderNavEvent) {
    when (navEvent) {
        OrderNavEvent.Back -> {
            when (this) {
                is MainActivity -> navController.popBackStack()
                is RegisterOrderActivity -> finish()
                else -> {}
            }

        }
        OrderNavEvent.ToNewOrder -> navController.navigate(Screen.Orders.Order.route)
        OrderNavEvent.ToRegisterOrder ->
            startActivity(
                Intent(
                    this,
                    RegisterOrderActivity::class.java
                )
            )
    }
}

sealed class OrderNavEvent {
    object ToNewOrder : OrderNavEvent()
    object ToRegisterOrder : OrderNavEvent()
    object Back : OrderNavEvent()
}