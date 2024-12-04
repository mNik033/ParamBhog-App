package com.parambhog.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parambhog.data.model.ApiResponse
import com.parambhog.data.repository.GoogleAuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: GoogleAuthRepository) : ViewModel() {
    private val _isSignedIn = MutableLiveData<Boolean>()
    val isSignedIn: LiveData<Boolean> get() = _isSignedIn

    private val _signInResult = MutableLiveData<Result<ApiResponse>>()
    val signInResult: LiveData<Result<ApiResponse>> get() = _signInResult

    init {
        _isSignedIn.value = repository.isSignedIn()
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            try {
                val result = repository.signInWithGoogle()
                _signInResult.postValue(Result.success(result))
            } catch (e: Exception) {
                _signInResult.postValue(Result.failure(e))
            }
        }
    }
}