package com.parambhog.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.parambhog.data.model.CartItem
import com.parambhog.data.repository.CartRepository

class CartViewModel(private val repository: CartRepository) : ViewModel() {
    private val _cart = MutableLiveData<List<CartItem>>()
    val cart: LiveData<List<CartItem>> get() = _cart

    private val _totalWeight = MutableLiveData<Int>()
    val totalWeight: LiveData<Int> get() = _totalWeight

    private val _totalPrice = MutableLiveData<Int>()
    val totalPrice: LiveData<Int> get() = _totalPrice

    init {
        loadCart()
    }

    private fun loadCart() {
        _cart.value = repository.getCart()
        _totalWeight.value = getTotalWeight()
        _totalPrice.value = getTotalPrice()
    }

    fun addItemToCart(itemID: Int, title: String, price: Int, weight: Int, diffQuantity: Int) {
        repository.addToCart(CartItem(itemID, title, price, weight, diffQuantity))
        loadCart()
    }

    fun clearCart() {
        repository.clearCart()
        loadCart()
    }

    private fun getTotalWeight(): Int {
        return repository.getTotalWeight()
    }

    private fun getTotalPrice(): Int {
        return repository.getTotalPrice()
    }
}