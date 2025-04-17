package com.example.wavesoffood.Models

class LocationRepository(private val apiService: LocationApiService) {

    suspend fun getCountries() = apiService.getCountries()
    suspend fun getStates(countryCode: String) = apiService.getStates(countryCode)
    suspend fun getCities(countryCode: String, stateCode: String) =
        apiService.getCities(countryCode, stateCode)
}