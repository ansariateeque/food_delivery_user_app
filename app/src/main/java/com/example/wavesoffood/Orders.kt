package com.example.wavesoffood

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Orders(
    val orderID: String = "",
    val items: List<RetrieveAddToCart> = emptyList(),
    val userDetails:@RawValue Map<String, Any?> = emptyMap(),
    val totalAmount: Double = 0.0,
    val status: String = "Pending",
    val timestamp: Long = System.currentTimeMillis(),
    val paymentMethod: String = "",
    val deliveredTimestamp: Long = 0,
    val hotelname:String="",
    ) : Parcelable