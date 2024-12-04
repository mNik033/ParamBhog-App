package com.parambhog

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

class GoogleAuthClient(private val context: Context) {
    private val credentialManager = CredentialManager.create(context)

    fun isSignedIn(): Boolean {
        val sharedPreferences = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
        return sharedPreferences.contains("email")
    }

    suspend fun getIdToken(): String {
        return try {
            val request = buildCredentialRequest()
            val credential = request.credential

            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val tokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                tokenCredential.idToken
            } else {
                throw Exception("Invalid credential type")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception(e)
        }
    }

    private suspend fun buildCredentialRequest(): GetCredentialResponse {
        val request = GetCredentialRequest.Builder().addCredentialOption(
            GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildConfig.CLIENT_ID)
                .setAutoSelectEnabled(false).build()
        ).build()

        return credentialManager.getCredential(context, request)
    }
}