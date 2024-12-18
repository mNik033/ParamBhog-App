package com.parambhog.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.parambhog.data.model.CartItem

class CartRepository(context: Context) {
    private val cart_key = "Cart"
    private val sharedPrefs = context.getSharedPreferences(cart_key, Context.MODE_PRIVATE)

    fun getCart(): List<CartItem> {
        val cartJson = sharedPrefs.getString(cart_key, "[]") ?: "[]"
        val type = object : TypeToken<List<CartItem>>() {}.type
        return Gson().fromJson(cartJson, type)
    }

    fun getTotalWeight(): Int {
        val cart = getCart()
        return cart.sumOf { it.quantity * it.weight }
    }

    fun getTotalPrice(): Int {
        val cart = getCart()
        return cart.sumOf { it.quantity * it.price }
    }

    fun addToCart(item: CartItem) {
        val cart = getCart().toMutableList()
        val existingItem = cart.find { it.itemID == item.itemID && it.weight == item.weight }

        if (existingItem != null) {
            existingItem.quantity += item.quantity
            if (existingItem.quantity == 0) cart.remove(existingItem)
        } else cart.add(item)

        saveCart(cart)
    }

    private fun saveCart(cart: List<CartItem>) {
        val cartJson = Gson().toJson(cart)
        sharedPrefs.edit().putString(cart_key, cartJson).apply()
    }

    fun clearCart() {
        saveCart(emptyList())
    }
}