package com.parambhog.data.repository

import android.content.Context
import com.parambhog.GoogleAuthClient
import com.parambhog.data.model.ApiResponse
import com.parambhog.data.model.User
import com.parambhog.data.remote.ApiService
import com.parambhog.data.remote.GoogleSignInRequest

class GoogleAuthRepository(
    private val apiService: ApiService,
    private val googleAuthClient: GoogleAuthClient,
    context: Context
) {

    private val sharedPrefs = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE)

    suspend fun signInWithGoogle(): ApiResponse {
        val idToken = googleAuthClient.getIdToken()
        val response = apiService.signInWithGoogle(GoogleSignInRequest(idToken))
        if (response.isSuccessful) {
            val apiResponse = response.body() ?: throw Exception("Empty response from server")
            cacheUserLocally(apiResponse.user)
            return apiResponse
        } else {
            throw Exception("Sign-in failed: ${response.errorBody()?.string()}")
        }
    }

    fun isSignedIn(): Boolean {
        return googleAuthClient.isSignedIn()
    }

    private fun cacheUserLocally(user: User) {
        with(sharedPrefs.edit()) {
            putString("id", user.id)
            putString("email", user.email)
            putString("name", user.name)
            putString("contactNo", user.contactNo)
            putString("pincode", user.pincode)
            putString("state", user.state)
            putString("district", user.district)
            putString("address", user.address)
            apply()
        }
    }
}