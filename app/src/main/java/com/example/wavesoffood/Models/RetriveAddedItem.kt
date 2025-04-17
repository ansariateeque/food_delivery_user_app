package com.example.wavesoffood.Models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RetriveAddedItem(
    val key: String? = null,
    val name: String? = null,
    val price: Double? = null,
    val uri: String? = null,
    val description: String? = null,
    val ingredeinets: List<String>? = null,
    val nameofrestuarant:String?=null,
    val userId:String?=null

) : Parcelable