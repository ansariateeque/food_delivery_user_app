package com.example.wavesoffood.Models

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    //singuplivedata
    val registerResult: LiveData<Pair<Boolean, String?>> = repository.registerresult

    val loading = repository.loading

    //loginlivedata
    val loginresult: LiveData<Pair<Boolean, String?>> = repository.loginresult

    //logincredential
    val crednetialresult: LiveData<Pair<Boolean, String?>> = repository.credentialresult


    private var userCountry: String = ""
    private var userState: String = ""
    private var userCity: String = ""


    fun setUserLocation(country: String, state: String, city: String) {
        userCountry = country
        userState = state
        userCity = city

    }


    fun registerUser(name: String, email: String, password: String) {
        repository.registerUser(name, email, password, userCountry, userState, userCity)
    }

    fun loginuser(email: String, password: String) {
        repository.loginuser(email, password, userCountry, userState, userCity)
    }

    fun configuregooglesignin(activity: Activity): GoogleSignInClient {
        return repository.configuregooglsignin(activity)
    }

    fun handleGoogleSignInResult(account: GoogleSignInAccount?) {
        repository.handleGoogleSignInResult(account, userCountry, userState, userCity)

    }


}