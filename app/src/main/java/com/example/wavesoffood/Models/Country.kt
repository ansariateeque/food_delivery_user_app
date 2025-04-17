package com.example.wavesoffood.Models

data class Country(
    val iso2: String,
    val name: String
 )

data class State(
    val iso2: String,   // State Code
    val name: String    // State Name
)

data class City(
    val name: String    // City Name
)