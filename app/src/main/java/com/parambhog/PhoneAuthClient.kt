package com.parambhog

import android.app.Activity
import android.content.Context
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class PhoneAuthClient(private val context: Context) {
    private var verificationId: String? = null
    private var authCallBack: ((Boolean, String?) -> Unit)? = null
    private var user: FirebaseUser? = null

    fun isSignedIn(): Boolean {
        val sharedPreferences = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
        return sharedPreferences.contains("phoneNumber")
    }

    fun requestOtp(phoneNumber: String, callback: (Boolean, String?) -> Unit) {
        authCallBack = callback
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(phoneNumber)
            .setTimeout(60, TimeUnit.SECONDS)
            .setActivity(context as Activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    callback(false, e.localizedMessage)
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    this@PhoneAuthClient.verificationId = verificationId
                    callback(true, null)
                }

            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(otp: String, callback: (Boolean, String?) -> Unit) {
        val credential = verificationId?.let { PhoneAuthProvider.getCredential(it, otp) }
        if (credential != null) {
            signInWithCredential(credential, callback)
        } else {
            callback(false, "Invalid verification ID")
        }
    }

    private fun signInWithCredential(
        credential: PhoneAuthCredential,
        callback: (Boolean, String?) -> Unit = authCallBack ?: { _, _ -> }
    ) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user = task.result.user
                    callback(true, task.result.user?.phoneNumber)
                } else {
                    callback(false, task.exception?.localizedMessage)
                }
            }
    }

    suspend fun getIdToken(): String {
        return try {
            if (user != null) {
                val idToken = user?.getIdToken(false)?.await()?.token
                idToken ?: throw Exception("Failed to retrieve ID token")
            } else {
                throw Exception("User is not signed in. Please verify OTP first.")
            }
        } catch (e: Exception) {
            throw Exception("Phone OTP Authentication failed: ${e.message}", e)
        }
    }
}