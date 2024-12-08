package com.parambhog.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parambhog.data.model.ApiResponse
import com.parambhog.data.repository.GoogleAuthRepository
import com.parambhog.data.repository.PhoneAuthRepository
import com.parambhog.data.repository.UserCacheManager
import kotlinx.coroutines.launch

class AuthViewModel(
    private val googleAuthRepository: GoogleAuthRepository,
    private val phoneAuthRepository: PhoneAuthRepository,
    private val userCacheManager: UserCacheManager
) : ViewModel() {
    private val _isSignedIn = MutableLiveData<Boolean>()
    val isSignedIn: LiveData<Boolean> get() = _isSignedIn

    private val _signInResult = MutableLiveData<Result<ApiResponse>>()
    val signInResult: LiveData<Result<ApiResponse>> get() = _signInResult

    private val _otpRequestStatus = MutableLiveData<Result<Boolean>>()
    val otpRequestStatus: LiveData<Result<Boolean>> get() = _otpRequestStatus

    private val _otpVerificationStatus = MutableLiveData<Result<ApiResponse>>()
    val otpVerificationStatus: LiveData<Result<ApiResponse>> get() = _otpVerificationStatus

    init {
        _isSignedIn.value = userCacheManager.isSignedIn()
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            try {
                val result = googleAuthRepository.signInWithGoogle()
                _signInResult.postValue(Result.success(result))
            } catch (e: Exception) {
                _signInResult.postValue(Result.failure(e))
            }
        }
    }

    fun requestOtp(phoneNumber: String) {
        viewModelScope.launch {
            try {
                phoneAuthRepository.requestOtp(phoneNumber) { success, message ->
                    if (success) {
                        _otpRequestStatus.postValue(Result.success(true))
                    } else {
                        _otpRequestStatus.postValue(Result.failure(Exception(message)))
                    }
                }
            } catch (e: Exception) {
                _otpRequestStatus.postValue(Result.failure(e))
            }
        }
    }

    fun verifyOtp(otp: String, phoneNumber: String) {
        viewModelScope.launch {
            try {
                phoneAuthRepository.verifyOtp(otp)

                val response = phoneAuthRepository.signInWithPhoneOtp(phoneNumber)
                _otpVerificationStatus.postValue(Result.success(response))
            } catch (e: Exception) {
                e.printStackTrace()
                _otpVerificationStatus.postValue(Result.failure(e))
            }
        }
    }
}