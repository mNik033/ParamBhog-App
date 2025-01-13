package com.parambhog.data.repository

import com.parambhog.PhoneAuthClient
import com.parambhog.data.model.ApiResponse
import com.parambhog.data.remote.ApiService
import com.parambhog.data.remote.PhoneSignInRequest
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PhoneAuthRepository(
    private val apiService: ApiService,
    private val phoneOTPAuthClient: PhoneAuthClient,
    private val userCacheManager: UserCacheManager
) {
    fun requestOtp(phoneNumber: String, callback: ((Int, String?) -> Unit)) {
        phoneOTPAuthClient.requestOtp(phoneNumber, callback)
    }

    suspend fun verifyOtp(otp: String): Boolean {
        return suspendCoroutine { continuation ->
            phoneOTPAuthClient.verifyOtp(otp) { success, message ->
                if (success == 1) {
                    continuation.resume(true)
                } else {
                    continuation.resumeWithException(Exception(message))
                }
            }
        }
    }

    suspend fun signInWithPhoneOtp(phoneNumber: String): ApiResponse {
        val idToken = phoneOTPAuthClient.getIdToken()
        val response = apiService.signInWithPhone(PhoneSignInRequest(phoneNumber, idToken))
        if (response.isSuccessful) {
            val apiResponse = response.body() ?: throw Exception("Empty response from server")
            userCacheManager.cacheUserLocally(apiResponse.user)
            return apiResponse
        } else {
            throw Exception("Sign-in failed: ${response.errorBody()?.string()}")
        }
    }
}