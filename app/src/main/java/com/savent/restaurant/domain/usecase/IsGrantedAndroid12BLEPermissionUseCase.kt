package com.savent.restaurant.domain.usecase

import android.content.Context
import com.savent.restaurant.AppConstants
import com.savent.restaurant.utils.CheckPermissions

class IsGrantedAndroid12BLEPermissionUseCase(private val context: Context) {

    operator fun invoke() = CheckPermissions.check(context, AppConstants.ANDROID_12_BLE_PERMISSIONS)
}