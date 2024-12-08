package com.parambhog.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.parambhog.data.repository.GoogleAuthRepository
import com.parambhog.data.repository.PhoneAuthRepository

class AuthViewModelFactory(
    private val googleAuthRepository: GoogleAuthRepository,
    private val phoneAuthRepository: PhoneAuthRepository
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(googleAuthRepository, phoneAuthRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}