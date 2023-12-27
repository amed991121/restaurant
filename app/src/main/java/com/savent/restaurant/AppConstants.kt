package com.savent.restaurant

import android.Manifest


object AppConstants {
    const val EMPTY_JSON_STRING = "[]"
    const val APP_PREFERENCES = "app_preferences"
    const val SESSION_PREFERENCES = "session_preferences"
    const val APP_DATABASE_NAME = "app_database"
    const val MAPS_API_KEY = "Ci03KBWSzUK95AmoKlyEVpSMV1FNuZFQ"
    const val SAVENT_RESTAURANT_API_BASE_URL = "your_api_url"
    const val SESSION_API_PATH = "session/"
    const val COMPANIES_API_PATH = "companies/"
    const val RESTAURANTS_API_PATH = "restaurants/"
    const val DISHES_API_PATH = "dishes/"
    const val TABLES_API_PATH = "tables/"
    const val DINERS_API_PATH = "diners/"
    const val SALES_API_PATH = "sales/"
    const val AUTHORIZATION = "your_auth"

    val ANDROID_12_BLE_PERMISSIONS = arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    const val REQUEST_12_BLE_CODE = 9090
    const val CODE_SCANNER = 1
    const val RESULT_CODE_SCANNER = "result_code_scanner"
    const val RECEIPT_KEY = "receipt"
}