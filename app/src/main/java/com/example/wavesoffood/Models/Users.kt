package com.example.wavesoffood.Models

data class Users(
    val role: String = "USERS",
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val country: String = "",
    val state: String = "",
    val city: String = "",
    val phone: String = "",
    val address: String = "",
    val fcmToken:String="",
    val nameofresturant: String = "",



    )