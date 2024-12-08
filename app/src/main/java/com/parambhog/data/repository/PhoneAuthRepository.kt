package com.parambhog.data.repository

import android.content.Context
import com.parambhog.PhoneAuthClient
import com.parambhog.data.model.ApiResponse
import com.parambhog.data.model.User
import com.parambhog.data.remote.ApiService
import com.parambhog.data.remote.PhoneSignInRequest
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PhoneAuthRepository(
    private val apiService: ApiService,
    private val phoneOTPAuthClient: PhoneAuthClient,
    context: Context
) {
    private val sharedPrefs = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE)

    fun requestOtp(phoneNumber: String, callback: ((Boolean, String?) -> Unit)) {
        phoneOTPAuthClient.requestOtp(phoneNumber, callback)
    }

    suspend fun verifyOtp(otp: String): Boolean {
        return suspendCoroutine { continuation ->
            phoneOTPAuthClient.verifyOtp(otp) { success, message ->
                if (success) {
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
            cacheUserLocally(apiResponse.user)
            return apiResponse
        } else {
            throw Exception("Sign-in failed: ${response.errorBody()?.string()}")
        }
    }

    fun isSignedIn(): Boolean {
        return phoneOTPAuthClient.isSignedIn()
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