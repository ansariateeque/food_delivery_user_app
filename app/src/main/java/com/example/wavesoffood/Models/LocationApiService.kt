package com.example.wavesoffood.Models

import retrofit2.http.GET
import retrofit2.http.Path

interface LocationApiService {

    @GET("v1/countries")
    suspend fun getCountries(): List<Country>

    @GET("v1/countries/{country}/states")
    suspend fun getStates(@Path("country") countryCode: String): List<State>

    @GET("v1/countries/{country}/states/{state}/cities")
    suspend fun getCities(@Path("country") countryCode: String, @Path("state") stateCode: String): List<City>
}