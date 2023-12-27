package com.savent.restaurant.ui.screen.login

import com.savent.restaurant.data.common.model.Company
import com.savent.restaurant.data.common.model.Restaurant


data class LoginState(
    val companies: List<Company> = listOf(),
    val restaurants: List<Restaurant> = listOf(),
    val selectedCompany: String = "",
    val selectedRestaurant: String = "",
    val isLoading: Boolean = false
)