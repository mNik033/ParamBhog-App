package com.parambhog

import android.content.Intent
import android.content.res.Resources.getSystem
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import com.parambhog.data.remote.RetrofitClient
import com.parambhog.data.repository.GoogleAuthRepository
import com.parambhog.data.repository.PhoneAuthRepository
import com.parambhog.data.repository.UserCacheManager
import com.parambhog.databinding.ActivityLoginBinding
import com.parambhog.databinding.BottomSheetPhoneAuthBinding
import com.parambhog.viewmodels.AuthViewModel
import com.parambhog.viewmodels.AuthViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: AuthViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userCacheManager = UserCacheManager(this)
        val googleAuthClient = GoogleAuthClient(this)
        val phoneAuthClient = PhoneAuthClient(this)
        val googleAuthRepository =
            GoogleAuthRepository(RetrofitClient.apiService, googleAuthClient, userCacheManager)
        val phoneAuthRepository =
            PhoneAuthRepository(RetrofitClient.apiService, phoneAuthClient, userCacheManager)
        val factory =
            AuthViewModelFactory(googleAuthRepository, phoneAuthRepository, userCacheManager)
        val bottomSheet = PhoneAuthBottomSheet(
            onOtpRequest = { phoneNumber ->
                viewModel.requestOtp(phoneNumber)
            },
            onOtpVerify = { otp, phoneNumber ->
                viewModel.verifyOtp(otp, phoneNumber)
            }
        )

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

        viewModel.otpRequestStatus.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "OTP Sent Successfully", Toast.LENGTH_SHORT).show()
                bottomSheet.showOtpLayout()
            }.onFailure {
                bottomSheet.handleOtpRequestFailure()
                Toast.makeText(
                    this,
                    "Failed to send OTP: ${it.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        viewModel.otpVerificationStatus.observe(this) { result ->
            result.onSuccess {
                navigateToMainActivity()
            }.onFailure {
                bottomSheet.handleOtpVerificationFailure()
                Toast.makeText(
                    this,
                    "OTP Verification Failed: ${it.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.buttonGoogleSignIn.setOnClickListener {
            viewModel.signInWithGoogle()
        }

        binding.buttonPhoneSignIn.setOnClickListener {
            bottomSheet.show(supportFragmentManager, "PhoneAuthBottomSheet")
        }

    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

class PhoneAuthBottomSheet(
    private val onOtpRequest: (String) -> Unit,
    private val onOtpVerify: (String, String) -> Unit
) : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetPhoneAuthBinding
    var phoneNumber = ""
    var otp = ""

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            navigationBarColor = Color.TRANSPARENT
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetPhoneAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonRequestOtp.setOnClickListener {
            phoneNumber = "+91-" + binding.editTextPhoneNumber.text.toString()
            binding.buttonRequestOtp.text = getString(R.string.requesting_otp)
            binding.buttonRequestOtp.isEnabled = false
            onOtpRequest(phoneNumber)
        }

        binding.buttonVerifyOtp.setOnClickListener {
            binding.buttonVerifyOtp.text = getString(R.string.verifying_otp)
            binding.buttonVerifyOtp.isEnabled = false
            onOtpVerify(otp, phoneNumber)
        }
    }

    fun handleOtpRequestFailure() {
        binding.buttonRequestOtp.text = getString(R.string.request_otp)
        binding.buttonRequestOtp.isEnabled = true
    }

    fun handleOtpVerificationFailure() {
        binding.buttonVerifyOtp.text = getString(R.string.verify_otp)
        binding.buttonVerifyOtp.isEnabled = true
    }

    fun showOtpLayout() {
        binding.buttonRequestOtp.visibility = View.GONE
        binding.editTextPhoneNumberLayout.visibility = View.GONE
        binding.otpContainer.visibility = View.VISIBLE
        binding.buttonVerifyOtp.visibility = View.VISIBLE
        binding.otpContainer.removeAllViews()
        binding.titleText.text = getString(R.string.enter_otp, phoneNumber)

        val otpFields = mutableListOf<EditText>()
        for (i in 0..5) {
            val otpDigitLayout = LayoutInflater.from(context).inflate(
                R.layout.otp_digit,
                binding.otpContainer,
                false
            ) as TextInputLayout

            val layoutParams =
                LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)

            if (i < 5) layoutParams.marginEnd = (4 * getSystem().displayMetrics.density).toInt()

            otpDigitLayout.layoutParams = layoutParams
            otpDigitLayout.editText!!.addTextChangedListener(EditTextWatcher(i, otpFields))
            otpFields.add(otpDigitLayout.editText!!)
            binding.otpContainer.addView(otpDigitLayout)
        }

        otpFields[0].requestFocus()
    }

    inner class EditTextWatcher(private val index: Int, private val otpFields: List<EditText>) :
        TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            val text = s.toString()

            if (text.length == 1 && index < otpFields.size - 1)
                otpFields[index + 1].requestFocus()
            else if (text.isEmpty() && index > 0)
                otpFields[index - 1].requestFocus()

            if (otpFields.all { it.text?.length == 1 }) {
                otp = otpFields.joinToString("") { it.text.toString() }
                binding.buttonVerifyOtp.text = getString(R.string.verifying_otp)
                binding.buttonVerifyOtp.isEnabled = false
                onOtpVerify(otp, phoneNumber)
            }
        }
    }

}