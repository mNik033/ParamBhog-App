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
    private var authCallBack: ((Int, String?) -> Unit)? = null
    private var user: FirebaseUser? = null

    fun requestOtp(phoneNumber: String, callback: (Int, String?) -> Unit) {
        authCallBack = callback
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(phoneNumber)
            .setTimeout(60, TimeUnit.SECONDS)
            .setActivity(context as Activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithCredential(credential) { success, message ->
                        if (success == 1) callback(2, message)
                        else callback(0, message)
                    }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    callback(0, e.localizedMessage)
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    this@PhoneAuthClient.verificationId = verificationId
                    callback(1, null)
                }

            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(otp: String, callback: (Int, String?) -> Unit) {
        val credential = verificationId?.let { PhoneAuthProvider.getCredential(it, otp) }
        if (credential != null) {
            signInWithCredential(credential, callback)
        } else {
            callback(0, "Invalid verification ID")
        }
    }

    private fun signInWithCredential(
        credential: PhoneAuthCredential,
        callback: (Int, String?) -> Unit = authCallBack ?: { _, _ -> }
    ) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user = task.result.user
                    callback(1, task.result.user?.phoneNumber)
                } else {
                    callback(0, task.exception?.localizedMessage)
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