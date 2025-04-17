package com.example.wavesoffood.Models

import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptors(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request().newBuilder()
            .addHeader("X-CSCAPI-KEY", apiKey) // ✅ Header automatically add hoga
            .build()
        return chain.proceed(request)
    }
}