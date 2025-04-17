package com.example.wavesoffood

import retrofit2.Response
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface FCMService {

    @POST("projects/food-waves-4e08c/messages:send")
    suspend fun sendNotification(
        @Header("Authorization") token: String,
        @Body notification: NotificationRequest
    ): Response<ResponseBody>

}