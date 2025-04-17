package com.example.wavesoffood.Models

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.wavesoffood.R
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("USERS")

    private lateinit var googleSignInClient: GoogleSignInClient

    var callbackManager: CallbackManager = CallbackManager.Factory.create();

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading


    //signupresult
    private val _registerresult = MutableLiveData<Pair<Boolean, String?>>()
    val registerresult: LiveData<Pair<Boolean, String?>> = _registerresult

    //loginresult
    private val _loginresult = MutableLiveData<Pair<Boolean, String?>>()
    val loginresult: LiveData<Pair<Boolean, String?>> = _loginresult

    //google login result
    private val _credentialresult = MutableLiveData<Pair<Boolean, String?>>()
    val credentialresult: LiveData<Pair<Boolean, String?>> = _credentialresult

    fun registerUser(
        name: String,
        email: String,
        password: String,
        usercountry: String,
        userstate: String,
        usercity: String,
    ) {
        _loading.postValue(true)
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                val user = auth.currentUser?.uid
                if (user != null) {

                    val users = Users(
                        name = name,
                        email = email,
                        password = password,
                        country = usercountry,
                        state = userstate,
                        city = usercity

                    )

                    databaseReference.child(user).setValue(users).addOnCompleteListener { task ->
                        _loading.postValue(false)
                        if (task.isSuccessful) {
                            _registerresult.postValue(Pair(true, null))

                        } else {
                            _registerresult.postValue(Pair(false, task.exception?.message))

                        }

                    }
                }
            } else {
                _loading.postValue(false)
                _registerresult.postValue(Pair(false, task.exception?.message.toString()))

            }


        }


    }

    fun loginuser(
        email: String, password: String, usercountry: String, userstate: String,
        usercity: String,
    ) {
        _loading.postValue(true) // 🔹 Loading Start

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val user = auth.currentUser?.uid

                databaseReference.child(user!!).get().addOnCompleteListener { snapshottask ->
                    _loading.postValue(false)

                    if (snapshottask.isSuccessful) {

                        val snapshot = snapshottask.result
                        if (snapshot.exists()) {
                            val role = snapshot.child("role").value.toString()

                            val country = snapshot.child("country").value.toString()
                            val state = snapshot.child("state").value.toString()
                            val city = snapshot.child("city").value.toString()

                            if (role == "USERS") {
                                _loginresult.postValue(Pair(true, null))

                                if (usercountry != country || userstate != state || usercity != city) {
                                    val updates = mapOf(
                                        "country" to usercountry,
                                        "state" to userstate,
                                        "city" to usercity
                                    )
                                    databaseReference.child(user!!).updateChildren(updates)
                                        .addOnSuccessListener {
                                            Log.d("Firebase", "Location updated successfully")
                                        }

                                }
                            } else {
                                _loginresult.postValue(
                                    Pair(
                                        false,
                                        "please login with the correct details"
                                    )
                                )
                                auth.signOut()
                            }
                        }
                    } else {
                        _loginresult.postValue(
                            Pair(
                                false,
                                "please login with the correct details"
                            )
                        )
                    }


                }


            } else {
                _loading.postValue(false) // 🔹 Loading Stop

                _loginresult.postValue(Pair(false, task.exception?.message))

            }

        }
    }

    fun configuregooglsignin(activity: Activity): GoogleSignInClient {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id)) // Replace with your Web Client ID from Firebase Console
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity, gso)
        return googleSignInClient
    }

    fun handleGoogleSignInResult(
        account: GoogleSignInAccount?, usercountry: String, userstate: String,
        usercity: String,
    ) {

        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        credentialLogin(credential, usercountry, userstate, usercity)
    }

    private fun credentialLogin(
        credential: AuthCredential, usercountry: String, userstate: String,
        usercity: String,
    ) {
        _loading.postValue(true) // 🔹 Loading Start

        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("facebooklogin2", auth.currentUser?.uid.toString())

                val userId = auth.currentUser?.uid

                if (userId != null) {
                    databaseReference.child(userId).get().addOnSuccessListener { snapshot ->
                        _loading.postValue(false) // 🔹 Loading stop

                        if (snapshot.exists()) {
                            val role = snapshot.child("role").value.toString()


                            val country = snapshot.child("country").value.toString()
                            val state = snapshot.child("state").value.toString()
                            val city = snapshot.child("city").value.toString()


                            if (role == "USERS") {

                                if (usercountry != country || userstate != state || usercity != city) {
                                    val updates = mapOf(
                                        "country" to usercountry,
                                        "state" to userstate,
                                        "city" to usercity
                                    )
                                    databaseReference.child(userId).updateChildren(updates)
                                        .addOnSuccessListener {
                                            Log.d("Firebase", "Location updated successfully")
                                        }

                                }
                                _credentialresult.postValue(Pair(true, null))


                            } else {
                                _credentialresult.postValue(
                                    Pair(
                                        false,
                                        "Access Denied! Please use the correct App"
                                    )
                                )
                                auth.signOut()
                                googleSignInClient.signOut()
                            }
                        } else {
                            saveNewUserToDatabase(usercountry, userstate, usercity)
                        }
                    }

                }

            } else {
                _loading.postValue(false) // 🔹 Loading stop

                _credentialresult.postValue(Pair(false, task.exception?.message))
            }
        }
    }

    private fun saveNewUserToDatabase(
        usercountry: String, userstate: String,
        usercity: String,
    ) {
        val user = auth.currentUser!!
        val users = Users(
            name = user.displayName ?: "",
            email = user.email  ?: "" ,
            country = usercountry,
            state = userstate,
            city = usercity
        )
        _loading.postValue(true) // 🔹 Loading Start

        databaseReference.child(user.uid).setValue(users).addOnCompleteListener() { task ->
            _loading.postValue(false) // 🔹 Loading Stop

            if (task.isSuccessful) {
                _credentialresult.postValue(Pair(true, null))
            } else {
                _credentialresult.postValue(Pair(false, task.exception?.message))
            }
        }
    }


}