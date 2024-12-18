package com.parambhog.data.model

data class CartItem(
    val itemID: Int,
    val title: String,
    val price: Int,
    val weight: Int,
    var quantity: Int
)