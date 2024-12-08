package com.parambhog.data.remote

import com.parambhog.data.model.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/auth/google")
    suspend fun signInWithGoogle(@Body request: GoogleSignInRequest): Response<ApiResponse>

    @POST("api/auth/phone")
    suspend fun signInWithPhone(@Body request: PhoneSignInRequest): Response<ApiResponse>
}

data class GoogleSignInRequest(
    val tokenId: String
)

data class PhoneSignInRequest(
    val phoneNumber: String,
    val uid: String
)