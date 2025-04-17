package com.example.wavesoffood.Models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LocationViewModel(private val repository: LocationRepository):ViewModel() {

    private val _countries = MutableLiveData<List<Country>>()
    val countries: LiveData<List<Country>> = _countries

    private val _states = MutableLiveData<List<State>>()
    val states: LiveData<List<State>> = _states

    private val _cities = MutableLiveData<List<City>>()
    val cities: LiveData<List<City>> = _cities

    fun fetchCountries() {
        viewModelScope.launch {
            try {
                val response = repository.getCountries()
                _countries.postValue(response)
            } catch (e: Exception) {
                Log.e("LocationViewModel", "Error fetching countries", e)
            }
        }
    }

    fun fetchStates(countryCode: String) {
        viewModelScope.launch {
            try {
                val response = repository.getStates(countryCode)
                _states.postValue(response)
            } catch (e: Exception) {
                Log.e("LocationViewModel", "Error fetching states", e)
            }
        }
    }

    fun fetchCities(countryCode: String, stateCode: String) {
        viewModelScope.launch {
            try {
                val response = repository.getCities(countryCode, stateCode)
                _cities.postValue(response)
            } catch (e: Exception) {
                Log.e("LocationViewModel", "Error fetching cities", e)
            }
        }
    }

}