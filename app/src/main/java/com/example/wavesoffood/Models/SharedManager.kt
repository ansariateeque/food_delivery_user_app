package com.example.wavesoffood.Models

import android.content.Context

class SharedManager(context: Context) {

    private val sharedPref = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)


    fun getCountry(): String? {
        return sharedPref.getString("user_country", null)
    }

    fun getStates(): String? {
        return sharedPref.getString("user_states", null)
    }

    fun getCity(): String? {
        return sharedPref.getString("user_city", null)
    }


    fun saveStates(states: String) {
        sharedPref.edit().putString("user_states", states).apply()
    }

    fun saveCountry(country: String) {
        sharedPref.edit().putString("user_country", country).apply()

    }

    fun saveCity(city: String) {
        sharedPref.edit().putString("user_city", city).apply()

    }

}