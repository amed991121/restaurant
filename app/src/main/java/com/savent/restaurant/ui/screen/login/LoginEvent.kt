package com.savent.restaurant.ui.screen.login


sealed class LoginEvent {
    class SelectCompany(val id: Int): LoginEvent()
    class SearchCompany(val query: String): LoginEvent()
    class SelectRestaurant(val id: Int): LoginEvent()
    class SearchRestaurant(val query: String): LoginEvent()
    object ReloadCompanies: LoginEvent()
    object ReloadRestaurants: LoginEvent()
    class Login(val credentials: Credentials): LoginEvent()
}