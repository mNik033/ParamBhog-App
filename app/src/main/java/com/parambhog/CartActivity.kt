package com.parambhog

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.parambhog.adapters.CartItemRecyclerAdapter
import com.parambhog.data.repository.CartRepository
import com.parambhog.databinding.ActivityCartBinding
import com.parambhog.databinding.BottomSheetPaymentBinding
import com.parambhog.viewmodels.CartViewModel
import com.parambhog.viewmodels.CartViewModelFactory
import java.util.Locale

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var cartViewModel: CartViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cart)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val repository = CartRepository(applicationContext)
        cartViewModel =
            ViewModelProvider(this, CartViewModelFactory(repository))[CartViewModel::class.java]

        binding.cartRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        val cartAdapter = CartItemRecyclerAdapter(mutableListOf()) { item, diffQuantity ->
            cartViewModel.addItemToCart(
                item.itemID,
                item.title,
                item.price,
                item.weight,
                diffQuantity
            )
        }
        binding.cartRecyclerView.adapter = cartAdapter

        cartViewModel.cart.observe(this) { cartItems ->
            cartAdapter.updateCartItems(cartItems.toMutableList())
        }

        cartViewModel.totalWeight.observe(this) { totalWeight ->
            binding.totalWeight.text = String.format(Locale.getDefault(), "%d gm", totalWeight)
            if (totalWeight > 1000) binding.totalWeight.text =
                String.format(Locale.getDefault(), "%.2f kg", totalWeight.toFloat().div(1000))

            if (totalWeight > 2000) {
                binding.buttonCheckout.isEnabled = false
                Snackbar.make(
                    binding.root,
                    getString(R.string.order_weight_limit_exceeded),
                    Snackbar.LENGTH_SHORT
                ).setAnchorView(binding.totalWeight).setBackgroundTint(Color.RED).show()
            } else {
                binding.buttonCheckout.isEnabled = true
            }
        }

        cartViewModel.totalPrice.observe(this) { totalPrice ->
            binding.totalPrice.text = String.format(Locale.getDefault(), "₹ %d", totalPrice)
        }

        val bottomSheet = PaymentBottomSheet()
        binding.buttonCheckout.setOnClickListener {
            bottomSheet.show(supportFragmentManager, "PaymentBottomSheet")
        }
    }
}

class PaymentBottomSheet() : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetPaymentBinding
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
        binding = BottomSheetPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheetDialog = dialog as BottomSheetDialog
        bottomSheetDialog.behavior.apply {
            isDraggable = false
            state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.buttonPayment.setOnClickListener {
            val name = binding.orderFullName.text
            val email = binding.orderEmail.text
            val phoneNumber = binding.orderPhoneNumber.text
            val pincode = binding.orderPincode.text
            val state = binding.orderState.text
            val district = binding.orderDistrict.text
            val address = binding.orderAddress.text

            val errorMessage = validateFields(name, email, phoneNumber, pincode, state, district, address)
            if (errorMessage != null) {
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.RED).show()
            } else {
                // TODO: implement payment gateway
            }
        }
    }

    private fun validateFields(
        name: Editable?,
        email: Editable?,
        phone: Editable?,
        pincode: Editable?,
        state: Editable?,
        district: Editable?,
        address: Editable?
    ): String? {
        val validNumber = Regex("^[+]?[0-9]{10}\$")
        val validEmail1 = Regex("[a-z0-9]+@[a-z]+.[a-z]{2,3}")
        val validEmail2 = Regex("[a-z0-9]+@[a-z]+.[a-z]+.[a-z]{2,3}")
        val validEmail3 = Regex("[a-z0-9]+@[a-z]+.[a-z]+.[a-z]+.[a-z]{2,3}")
        var errorMessage : String? = null

        if (name.isNullOrBlank()) {
            errorMessage = "Enter a valid name"
            binding.orderFullName.requestFocus()
        } else if (email.isNullOrBlank() || !(email.matches(validEmail1) ||
                    email.matches(validEmail2) || email.matches(validEmail3))) {
            errorMessage = "Enter a valid email address"
            binding.orderEmail.requestFocus()
        } else if (phone.isNullOrBlank() || !phone.matches(validNumber)) {
            binding.orderPhoneNumber.error = "Enter a valid phone number"
            binding.orderPhoneNumber.requestFocus()
        } else if (pincode.isNullOrBlank()) {
            errorMessage = "Enter a valid pin code"
            binding.orderPincode.requestFocus()
        } else if (state.isNullOrBlank()) {
            errorMessage = "Enter a valid state"
            binding.orderState.requestFocus()
        } else if (district.isNullOrBlank()) {
            errorMessage = "Enter a valid district"
            binding.orderDistrict.requestFocus()
        } else if (address.isNullOrBlank()) {
            errorMessage = "Enter a valid address"
            binding.orderAddress.requestFocus()
        }

        return errorMessage
    }
}