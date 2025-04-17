package com.example.wavesoffood

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger

@Suppress("DEPRECATION")
class MyApplication : Application() {

    lateinit var viewModelFactory: MainViewModelFactory

    override fun onCreate() {
        super.onCreate()


        val repository = MainRepository()
        viewModelFactory = MainViewModelFactory(repository, this) // ✅ Create factory here
    }


}