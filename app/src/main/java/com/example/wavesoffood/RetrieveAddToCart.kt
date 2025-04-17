package com.example.wavesoffood

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RetrieveAddToCart(
    val name: String? = null,
    val price: Double? = null,
    val uri: String? = null,
    val description: String? = null,
    val ingredeinets: List<String>? = null,
    val quantity: Int? = null,
    val namepricekey: String? = null,
    val pushkey: String? = null,
    val nameofrestuarant: String? = null,
    val hotelUserId:String?=null

    ) : Parcelable