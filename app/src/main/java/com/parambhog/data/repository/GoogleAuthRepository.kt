package com.parambhog.data.repository

import com.parambhog.GoogleAuthClient
import com.parambhog.data.model.ApiResponse
import com.parambhog.data.remote.ApiService
import com.parambhog.data.remote.GoogleSignInRequest

class GoogleAuthRepository(
    private val apiService: ApiService,
    private val googleAuthClient: GoogleAuthClient,
    private val userCacheManager: UserCacheManager
) {

    suspend fun signInWithGoogle(): ApiResponse {
        val idToken = googleAuthClient.getIdToken()
        val response = apiService.signInWithGoogle(GoogleSignInRequest(idToken))
        if (response.isSuccessful) {
            val apiResponse = response.body() ?: throw Exception("Empty response from server")
            userCacheManager.cacheUserLocally(apiResponse.user)
            return apiResponse
        } else {
            throw Exception("Sign-in failed: ${response.errorBody()?.string()}")
        }
    }
}