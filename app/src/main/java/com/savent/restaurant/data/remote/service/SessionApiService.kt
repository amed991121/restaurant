package com.savent.restaurant.data.remote.service

import com.savent.restaurant.AppConstants
import com.savent.restaurant.data.common.model.Session
import com.savent.restaurant.ui.screen.login.Credentials
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface SessionApiService{
    @Headers("Content-Type: application/json")
    @POST(AppConstants.SESSION_API_PATH)
    suspend fun getSession(
        @Body credentials: Credentials,
        @Query("companyId") companyId: Int,
        @Query("restaurantId") restaurantId: Int
    ): Response<Session>
}