package com.example.wavesoffood

import android.content.Context
import android.util.Log
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream

object FirebaseAuthHelper {

    suspend fun getAccessToken(context: Context): String? {
        return withContext(Dispatchers.IO) { // 🔥 Runs on background thread
            try {
                val inputStream: InputStream = context.assets.open("service_account.json")
                val googleCredentials = GoogleCredentials.fromStream(inputStream)
                    .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))

                googleCredentials.refreshIfExpired() // Ensure token refresh

                val token = googleCredentials.accessToken?.tokenValue

                token ?: throw IOException("Access Token is null")
            } catch (e: IOException) {
                Log.e("getAccessToken", "IOException occurred: ${e.message}", e)
                null
            } catch (e: NullPointerException) {
                Log.e("getAccessToken", "NullPointerException: ${e.message}", e)
                null
            } catch (e: SecurityException) {
                Log.e("getAccessToken", "SecurityException: ${e.message}", e)
                null
            } catch (e: Exception) {
                Log.e("getAccessToken", "Unknown Exception: ${e.message}", e)
                null
            }
        }
    }
}
