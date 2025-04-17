package com.example.wavesoffood.Models

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RerofitInstance {

    private const val BASE_URL = "https://api.countrystatecity.in/"

    private val client = OkHttpClient.Builder()
        .addInterceptor(ApiKeyInterceptors("N3A0ZGk5aWNmdEh1dDZZOTlkbElLSmxYV0JMWDJaNWI0TkZuQ1hOYQ==")) // ✅ Interceptor add kiya
        .build()

    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // ✅ OkHttpClient use kiya
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getInstance(): LocationApiService {
        return provideRetrofit().create(LocationApiService::class.java)
    }


}