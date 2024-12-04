package com.parambhog

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.parambhog.data.remote.RetrofitClient
import com.parambhog.data.repository.GoogleAuthRepository
import com.parambhog.viewmodels.AuthViewModel
import com.parambhog.viewmodels.AuthViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val googleAuthClient = GoogleAuthClient(this)
        val repository = GoogleAuthRepository(RetrofitClient.apiService, googleAuthClient, this)
        val factory = AuthViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        viewModel.isSignedIn.observe(this) { isSignedIn ->
            if (isSignedIn) {
                navigateToMainActivity()
            }
        }

        viewModel.signInResult.observe(this) { result ->
            result.onSuccess {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }.onFailure { error ->
                Toast.makeText(this, error.cause?.message ?: "Error signing in. Please try again", Toast.LENGTH_LONG).show()
            }
        }

        val btnSignIn = findViewById<Button>(R.id.buttonGoogleSignIn)
        btnSignIn.setOnClickListener {
            viewModel.signInWithGoogle()
        }

    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}